/*
 * SimpleCloud is a software for administrating a minecraft server network.
 * Copyright (C) 2022 Frederick Baier & Philipp Eistrach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.simplecloud.simplecloud.api.impl.distribution

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.timeout.timout
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.distribution.api.Distribution
import app.simplecloud.simplecloud.distribution.api.DistributionComponent
import app.simplecloud.simplecloud.distribution.api.MessageListener
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 18:30
 * @author Frederick Baier
 */
class DistributedQueryHandlerImpl constructor(
    private val messageChannelManager: MessageChannelManager,
    private val nodeService: NodeService,
    private val processService: CloudProcessService,
    private val distribution: Distribution
) : DistributedQueryHandler {

    private val messageManager = this.distribution.getMessageManager()
    private val queries = CopyOnWriteArrayList<DistributedQuery>()


    init {
        startListening()
    }

    override fun <T> sendQuery(topic: String, message: Any, networkComponent: NetworkComponent): CompletableFuture<T> {
        val requestId = UUID.randomUUID()
        val future = createFutureWithTimeout<T>(1000)
        createDistributedQuery(requestId, future)

        val transferObject = DistributionDataTransferObject(topic, requestId, Result.success(message), false)
        sendPacketToSingleReceiver(transferObject, networkComponent)
        return future
    }

    fun sendPacketToSingleReceiver(transferObject: DistributionDataTransferObject, receiver: NetworkComponent) {
        if (isUnitMessage(transferObject)) {
            val editedTransferObject = DistributionDataTransferObject(
                transferObject.topic,
                transferObject.messageId,
                Result.success("kotlin.Unit"),
                transferObject.isResponse
            )
            this.messageManager.sendMessage(editedTransferObject, receiver.getDistributionComponent())
            return
        }
        this.messageManager.sendMessage(transferObject, receiver.getDistributionComponent())
    }

    private fun isUnitMessage(transferObject: DistributionDataTransferObject): Boolean {
        val message = transferObject.message
        return message.isSuccess && message.getOrThrow() is Unit
    }

    override fun sendToAll(topic: String, message: Any) {
        val requestId = UUID.randomUUID()
        val transferObject = DistributionDataTransferObject(topic, requestId, Result.success(message), false)
        this.messageManager.sendMessage(transferObject)
    }

    private fun <T> createFutureWithTimeout(timeout: Long): CompletableFuture<T> {
        val future = CloudCompletableFuture<T>()
        future.timout(timeout)
        return future
    }

    private fun createDistributedQuery(requestId: UUID, future: CompletableFuture<*>): DistributedQuery {
        val distributedQuery = DistributedQuery(requestId, future as CompletableFuture<Any>)
        this.queries.add(distributedQuery)
        registerUnregisterListenerForQuery(future, distributedQuery)
        return distributedQuery
    }

    private fun <T> registerUnregisterListenerForQuery(
        future: CompletableFuture<T>,
        distributedQuery: DistributedQuery
    ) {
        future.handle { _, _ ->
            this.queries.remove(distributedQuery)
        }
    }

    private fun startListening() {
        val messageListener = object : MessageListener {
            override fun messageReceived(message: Any, sender: DistributionComponent) {
                handleMessage(sender, message as DistributionDataTransferObject)
            }

        }
        this.distribution.getMessageManager().setMessageListener(messageListener)
    }

    private fun handleMessage(sender: DistributionComponent, transferObject: DistributionDataTransferObject) {
        val editedTransferObject = editUnitTransferObject(transferObject)
        if (transferObject.isResponse) {
            handleResponse(editedTransferObject)
        } else {
            handleQuery(sender, editedTransferObject)
        }
    }

    private fun editUnitTransferObject(transferObject: DistributionDataTransferObject): DistributionDataTransferObject {
        val message = transferObject.message
        if (message.isSuccess && message.getOrThrow() is String) {
            val string = message.getOrThrow()
            if (string == "kotlin.Unit") {
                return DistributionDataTransferObject(
                    transferObject.topic,
                    transferObject.messageId,
                    Result.success(Unit),
                    transferObject.isResponse
                )
            }
        }
        return transferObject
    }

    private fun handleQuery(sender: DistributionComponent, transferObject: DistributionDataTransferObject) {
        val networkComponent = getNetworkComponentByDistributionComponent(sender)
        networkComponent.thenAccept {
            DistributionIncomingQueryHandler(this, messageChannelManager, it, transferObject).handle()
        }.exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }
    }

    private fun handleResponse(transferObject: DistributionDataTransferObject) {
        val distributedQuery = getDistributedQueryByMessageId(transferObject.messageId) ?: return
        if (transferObject.message.isSuccess) {
            distributedQuery.future.complete(transferObject.message.getOrThrow())
        } else {
            distributedQuery.future.completeExceptionally(transferObject.message.exceptionOrNull())
        }
    }

    private fun getDistributedQueryByMessageId(messageId: UUID): DistributedQuery? {
        return this.queries.firstOrNull { it.queryId == messageId }
    }

    private fun getNetworkComponentByDistributionComponent(sender: DistributionComponent): CompletableFuture<out NetworkComponent> {
        val nodeFuture: CompletableFuture<out NetworkComponent> = this.nodeService.findByDistributionComponent(sender)
        val processFuture: CompletableFuture<out NetworkComponent> =
            this.processService.findByDistributionComponent(sender)
        val futureList = listOf(nodeFuture, processFuture).toFutureList()
        return futureList.thenApply { it.first() }
            .exceptionally {
                this.processService.findByDistributionComponent(sender).join()
            } //Tests will fail without it
            .exceptionally { throw NoSuchElementException("Could not find NetworkComponent by sender: $sender", it) }
    }

}