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

package eu.thesimplecloud.simplecloud.api.request.group.update

import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.CloudLobbyGroup
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersion
import eu.thesimplecloud.simplecloud.api.template.Template
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 09:59
 * @author Frederick Baier
 */
interface CloudLobbyGroupUpdateRequest : CloudServerGroupUpdateRequest {

    /**
     * Sets the lobby priority for the group
     * @return this
     */
    fun setLobbyPriority(lobbyPriority: Int): CloudLobbyGroupUpdateRequest

    override fun getProcessGroup(): CloudLobbyGroup

    override fun setMaxMemory(memory: Int): CloudLobbyGroupUpdateRequest

    override fun setMaxPlayers(players: Int): CloudLobbyGroupUpdateRequest

    override fun setVersion(version: ProcessVersion): CloudLobbyGroupUpdateRequest

    override fun setVersion(versionFuture: CompletableFuture<ProcessVersion>): CloudLobbyGroupUpdateRequest

    override fun setTemplate(template: Template): CloudLobbyGroupUpdateRequest

    override fun setTemplate(templateFuture: CompletableFuture<Template>): CloudLobbyGroupUpdateRequest

    override fun setJvmArguments(jvmArguments: JVMArguments?): CloudLobbyGroupUpdateRequest

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<JVMArguments>): CloudLobbyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfiguration: ProcessesOnlineCountConfiguration): CloudLobbyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration>): CloudLobbyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): CloudLobbyGroupUpdateRequest

    override fun setMinimumOnlineProcessCount(minCount: Int): CloudLobbyGroupUpdateRequest

    override fun setMaximumOnlineProcessCount(maxCount: Int): CloudLobbyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): CloudLobbyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): CloudLobbyGroupUpdateRequest

    override fun setStartPriority(priority: Int): CloudLobbyGroupUpdateRequest

    override fun setNodesAllowedToStartOn(node: List<String>): CloudLobbyGroupUpdateRequest

    override fun submit(): CompletableFuture<CloudProcessGroup>
}