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

package app.simplecloud.simplecloud.api.impl.ignite

import app.simplecloud.simplecloud.api.future.CloudCompletableFuture
import app.simplecloud.simplecloud.api.future.timeout.timout
import app.simplecloud.simplecloud.api.future.toFutureList
import app.simplecloud.simplecloud.api.messagechannel.manager.MessageChannelManager
import app.simplecloud.simplecloud.api.service.CloudProcessService
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteMessaging
import org.apache.ignite.lang.IgniteBiPredicate
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 29.05.2021
 * Time: 18:30
 * @author Frederick Baier
 */
@Singleton
class IgniteQueryHandlerImpl @Inject constructor(
    private val messageChannelManager: MessageChannelManager,
    private val injector: Injector,
    private val ignite: Ignite
) : IgniteQueryHandler {

    private val queries = CopyOnWriteArrayList<IgniteQuery>()

    private lateinit var nodeService: NodeService
    private lateinit var processService: CloudProcessService

    init {
        startListening()
    }

    override fun <T> sendQuery(topic: String, message: Any, networkComponent: NetworkComponent): CompletableFuture<T> {
        val requestId = UUID.randomUUID()
        val future = createFutureWithTimeout<T>(1000)
        createIgniteQuery(requestId, future)

        val transferObject = IgniteDataTransferObject(topic, requestId, Result.success(message), false)
        sendPacketToSingleReceiver(transferObject, networkComponent.getIgniteId())
        return future
    }

    override fun sendToAll(topic: String, message: Any) {
        val requestId = UUID.randomUUID()
        val transferObject = IgniteDataTransferObject(topic, requestId, Result.success(message), false)
        sendPacketToMessaging(this.ignite.message(), transferObject)
    }

    private fun <T> createFutureWithTimeout(timeout: Long): CompletableFuture<T> {
        val future = CloudCompletableFuture<T>()
        future.timout(timeout)
        return future
    }

    private fun createIgniteQuery(requestId: UUID, future: CompletableFuture<*>): IgniteQuery {
        val igniteQuery = IgniteQuery(requestId, future as CompletableFuture<Any>)
        this.queries.add(igniteQuery)
        registerUnregisterListenerForQuery(future, igniteQuery)
        return igniteQuery
    }

    fun sendPacketToSingleReceiver(transferObject: IgniteDataTransferObject, receiverNodeId: UUID) {
        val clusterGroup = this.ignite.cluster().forNodeId(receiverNodeId)
        sendPacketToMessaging(this.ignite.message(clusterGroup), transferObject)
    }

    private fun sendPacketToMessaging(messaging: IgniteMessaging, transferObject: IgniteDataTransferObject) {
        messaging.send("cloud-topic", transferObject)
    }

    private fun <T> registerUnregisterListenerForQuery(future: CompletableFuture<T>, igniteQuery: IgniteQuery) {
        future.handle { _, _ ->
            this.queries.remove(igniteQuery)
        }
    }

    private fun startListening() {
        this.ignite.message().localListen("cloud-topic", IgniteBiPredicate<UUID, IgniteDataTransferObject> { uuid, data ->
            handleMessage(uuid, data)
            return@IgniteBiPredicate true
        })
    }

    private fun handleMessage(senderNodeId: UUID, transferObject: IgniteDataTransferObject) {
        if (transferObject.isResponse) {
            handleResponse(transferObject)
        } else {
            handleQuery(senderNodeId, transferObject)
        }
    }

    private fun handleQuery(senderNodeId: UUID, transferObject: IgniteDataTransferObject) {
        val networkComponent = getNetworkComponentByUniqueId(senderNodeId)
        networkComponent.thenAccept {
            IgniteIncomingQueryHandler(this, messageChannelManager, it, transferObject).handle()
        }.exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }
    }

    private fun handleResponse(transferObject: IgniteDataTransferObject) {
        val igniteQuery = getIgniteQueryByMessageId(transferObject.messageId) ?: return
        if (transferObject.message.isSuccess) {
            igniteQuery.future.complete(transferObject.message.getOrThrow())
        } else {
            igniteQuery.future.completeExceptionally(transferObject.message.exceptionOrNull())
        }
    }

    private fun getIgniteQueryByMessageId(messageId: UUID): IgniteQuery? {
        return this.queries.firstOrNull { it.queryId == messageId }
    }

    private fun getNetworkComponentByUniqueId(senderNodeId: UUID): CompletableFuture<out NetworkComponent> {
        checkServicesInitialized()
        val nodeFuture: CompletableFuture<out NetworkComponent> = this.nodeService.findByUniqueId(senderNodeId)
        val processFuture: CompletableFuture<out NetworkComponent> =
            this.processService.findByIgniteId(senderNodeId)
        val futureList = listOf(nodeFuture, processFuture).toFutureList()
        return futureList.thenApply { it.first() }
            .exceptionally { throw NoSuchElementException("Could not find NetworkComponent by id: $senderNodeId") }
    }

    private fun checkServicesInitialized() {
        if (!areServicesInitialized())
            initializeServices()
    }

    private fun areServicesInitialized(): Boolean {
        return this::nodeService.isInitialized && this::processService.isInitialized
    }

    private fun initializeServices() {
        this.nodeService = this.injector.getInstance(NodeService::class.java)
        this.processService = this.injector.getInstance(CloudProcessService::class.java)
    }


}