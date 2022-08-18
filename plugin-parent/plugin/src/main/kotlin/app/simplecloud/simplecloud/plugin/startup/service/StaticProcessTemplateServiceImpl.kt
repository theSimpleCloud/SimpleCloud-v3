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

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedStaticProcessTemplateRepository
import app.simplecloud.simplecloud.api.impl.service.AbstractStaticProcessTemplateService
import app.simplecloud.simplecloud.api.impl.template.statictemplate.factory.UniversalStaticProcessTemplateFactory
import app.simplecloud.simplecloud.api.internal.messagechannel.InternalMessageChannelProvider
import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.service.NodeService
import app.simplecloud.simplecloud.api.template.static.StaticProcessTemplate

/**
 * Date: 17.08.22
 * Time: 13:32
 * @author Frederick Baier
 *
 */
class StaticProcessTemplateServiceImpl(
    distributedRepository: DistributedStaticProcessTemplateRepository,
    staticTemplateFactory: UniversalStaticProcessTemplateFactory,
    internalMessageChannelProvider: InternalMessageChannelProvider,
    private val nodeService: NodeService,
) : AbstractStaticProcessTemplateService(distributedRepository, staticTemplateFactory) {

    private val deleteMessageChannel = internalMessageChannelProvider.getInternalDeleteStaticTemplateChannel()

    private val updateMessageChannel = internalMessageChannelProvider.getInternalUpdateStaticTemplateChannel()

    override suspend fun updateGroupInternal0(template: StaticProcessTemplate) {
        val node = this.nodeService.findFirst().await()
        sendUpdateRequestToNode(template, node)
    }

    override suspend fun deleteStaticTemplateInternal(template: StaticProcessTemplate) {
        val node = this.nodeService.findFirst().await()
        sendDeleteRequestToNode(template, node)
    }

    private suspend fun sendUpdateRequestToNode(template: StaticProcessTemplate, node: Node) {
        this.updateMessageChannel.createMessageRequest(template.toConfiguration(), node).submit().await()
    }

    private suspend fun sendDeleteRequestToNode(template: StaticProcessTemplate, node: Node) {
        this.deleteMessageChannel.createMessageRequest(template.getName(), node).submit().await()
    }


}