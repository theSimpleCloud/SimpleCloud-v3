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

package app.simplecloud.simplecloud.plugin.startup.service

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.process.factory.CloudProcessFactory
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedCloudProcessRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractCloudProcessService
import app.simplecloud.simplecloud.api.internal.configutation.ProcessExecuteCommandConfiguration
import app.simplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.process.CloudProcessConfiguration
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.eventapi.EventManager
import java.util.concurrent.CompletableFuture

class CloudProcessServiceImpl(
    processFactory: CloudProcessFactory,
    distributedRepository: DistributedCloudProcessRepository,
    eventManager: EventManager,
    private val nodeService: NodeService
) : AbstractCloudProcessService(
    processFactory, distributedRepository, eventManager
) {

    private lateinit var messageChannelProvider: InternalMessageChannelProvider

    fun initializeMessageChannels(internalMessageChannelProvider: InternalMessageChannelProvider) {
        this.messageChannelProvider = internalMessageChannelProvider
    }

    override suspend fun startNewProcessInternal(configuration: ProcessStartConfiguration): CloudProcess {
        val node = findRandomNode()
        val processConfiguration = sendStartRequestToNode(configuration, node)
        return this.processFactory.create(processConfiguration, this)
    }

    private suspend fun sendStartRequestToNode(
        configuration: ProcessStartConfiguration,
        node: Node
    ): CloudProcessConfiguration {
        return this.messageChannelProvider.getInternalStartProcessChannel().createMessageRequest(configuration, node)
            .submit().await()
    }

    override suspend fun executeCommandInternal(configuration: ProcessExecuteCommandConfiguration) {
        val node = findRandomNode()
        return this.messageChannelProvider.getInternalExecuteCommandChannel().createMessageRequest(configuration, node)
            .submit().await()
    }

    override fun getLogs(process: CloudProcess): CompletableFuture<String> = CloudScope.future {
        val node = findRandomNode()
        return@future messageChannelProvider.getInternalProcessLogsMessageChannel()
            .createMessageRequest(process.getName(), node).submit().await()
    }

    override suspend fun shutdownProcessInternal(process: CloudProcess) {
        TODO()
    }

    private suspend fun findRandomNode(): Node {
        return this.nodeService.findFirst().await()
    }

}