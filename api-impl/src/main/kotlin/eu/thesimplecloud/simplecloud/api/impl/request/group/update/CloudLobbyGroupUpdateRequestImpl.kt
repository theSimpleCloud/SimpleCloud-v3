/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.impl.request.group.update

import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.CloudLobbyGroup
import eu.thesimplecloud.simplecloud.api.request.group.update.CloudLobbyGroupUpdateRequest
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersion
import eu.thesimplecloud.simplecloud.api.template.Template
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 10:03
 * @author Frederick Baier
 */
class CloudLobbyGroupUpdateRequestImpl(
    private val internalService: InternalCloudProcessGroupService,
    private val lobbyGroup: CloudLobbyGroup
) : AbstractCloudProcessGroupUpdateRequest(lobbyGroup),
    CloudLobbyGroupUpdateRequest {

    @Volatile
    private var lobbyPriority = this.lobbyGroup.getLobbyPriority()

    override fun setLobbyPriority(lobbyPriority: Int): CloudLobbyGroupUpdateRequest {
        this.lobbyPriority = lobbyPriority
        return this
    }

    override fun getProcessGroup(): CloudLobbyGroup {
        return this.lobbyGroup
    }

    override fun setMaxMemory(memory: Int): CloudLobbyGroupUpdateRequest {
        super.setMaxMemory(memory)
        return this
    }

    override fun setMaxPlayers(players: Int): CloudLobbyGroupUpdateRequest {
        super.setMaxPlayers(players)
        return this
    }

    override fun setVersion(version: ProcessVersion): CloudLobbyGroupUpdateRequest {
        super.setVersion(version)
        return this
    }

    override fun setVersion(versionFuture: CompletableFuture<ProcessVersion>): CloudLobbyGroupUpdateRequest {
        super.setVersion(versionFuture)
        return this
    }

    override fun setTemplate(template: Template): CloudLobbyGroupUpdateRequest {
        super.setTemplate(template)
        return this
    }

    override fun setTemplate(templateFuture: CompletableFuture<Template>): CloudLobbyGroupUpdateRequest {
        super.setTemplate(templateFuture)
        return this
    }

    override fun setJvmArguments(jvmArguments: JVMArguments?): CloudLobbyGroupUpdateRequest {
        super.setJvmArguments(jvmArguments)
        return this
    }

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<JVMArguments>): CloudLobbyGroupUpdateRequest {
        super.setJvmArguments(jvmArgumentsFuture)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfiguration: ProcessesOnlineCountConfiguration): CloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfiguration)
        return this
    }

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration>): CloudLobbyGroupUpdateRequest {
        super.setOnlineCountConfiguration(onlineCountConfigurationFuture)
        return this
    }

    override fun setMaintenance(maintenance: Boolean): CloudLobbyGroupUpdateRequest {
        super.setMaintenance(maintenance)
        return this
    }

    override fun setMinimumOnlineProcessCount(minCount: Int): CloudLobbyGroupUpdateRequest {
        super.setMinimumOnlineProcessCount(minCount)
        return this
    }

    override fun setMaximumOnlineProcessCount(maxCount: Int): CloudLobbyGroupUpdateRequest {
        super.setMaximumOnlineProcessCount(maxCount)
        return this
    }

    override fun setJoinPermission(permission: String?): CloudLobbyGroupUpdateRequest {
        super.setJoinPermission(permission)
        return this
    }

    override fun setStateUpdating(stateUpdating: Boolean): CloudLobbyGroupUpdateRequest {
        super.setStateUpdating(stateUpdating)
        return this
    }

    override fun setStartPriority(priority: Int): CloudLobbyGroupUpdateRequest {
        super.setStartPriority(priority)
        return this
    }

    override fun setNodesAllowedToStartOn(nodes: List<String>): CloudLobbyGroupUpdateRequest {
        super.setNodesAllowedToStartOn(nodes)
        return this
    }

    override fun submit0(
        version: ProcessVersion,
        template: Template,
        jvmArguments: JVMArguments?,
        onlineCountConfiguration: ProcessesOnlineCountConfiguration,
        nodesAllowedToStartOn: List<String>
    ): CompletableFuture<CloudProcessGroup> {
        val updateObj = CloudLobbyProcessGroupConfiguration(
            this.lobbyGroup.getName(),
            this.maxMemory,
            this.maxPlayers,
            this.maintenance,
            this.minProcessCount,
            this.maxProcessCount,
            template.getName(),
            jvmArguments?.getName(),
            version.getName(),
            onlineCountConfiguration.getName(),
            this.lobbyGroup.isStatic(),
            this.stateUpdating,
            this.startPriority,
            this.joinPermission,
            this.nodesAllowedToStartOn,
            this.lobbyPriority
        )
        return this.internalService.updateGroupInternal(updateObj)
    }
}