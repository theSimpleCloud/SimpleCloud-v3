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

import eu.thesimplecloud.simplecloud.api.image.Image
import eu.thesimplecloud.simplecloud.api.jvmargs.JVMArguments
import eu.thesimplecloud.simplecloud.api.process.group.CloudProcessGroup
import eu.thesimplecloud.simplecloud.api.process.group.CloudProxyGroup
import eu.thesimplecloud.simplecloud.api.process.onlineonfiguration.ProcessesOnlineCountConfiguration
import eu.thesimplecloud.simplecloud.api.process.version.ProcessVersion
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 06.04.2021
 * Time: 11:19
 * @author Frederick Baier
 */
interface CloudProxyGroupUpdateRequest : CloudProcessGroupUpdateRequest {

    /**
     * Sets the start priority for the group
     * @return this
     */
    fun setStartPort(startPort: Int): CloudProxyGroupUpdateRequest

    override fun getProcessGroup(): CloudProxyGroup

    override fun setMaxMemory(memory: Int): CloudProxyGroupUpdateRequest

    override fun setMaxPlayers(players: Int): CloudProxyGroupUpdateRequest

    override fun setVersion(version: ProcessVersion): CloudProxyGroupUpdateRequest

    override fun setVersion(versionFuture: CompletableFuture<ProcessVersion>): CloudProxyGroupUpdateRequest

    override fun setImage(image: Image): CloudProxyGroupUpdateRequest

    override fun setJvmArguments(jvmArguments: JVMArguments?): CloudProxyGroupUpdateRequest

    override fun setJvmArguments(jvmArgumentsFuture: CompletableFuture<JVMArguments>): CloudProxyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfiguration: ProcessesOnlineCountConfiguration): CloudProxyGroupUpdateRequest

    override fun setOnlineCountConfiguration(onlineCountConfigurationFuture: CompletableFuture<ProcessesOnlineCountConfiguration>): CloudProxyGroupUpdateRequest

    override fun setMaintenance(maintenance: Boolean): CloudProxyGroupUpdateRequest

    override fun setMinimumOnlineProcessCount(minCount: Int): CloudProxyGroupUpdateRequest

    override fun setMaximumOnlineProcessCount(maxCount: Int): CloudProxyGroupUpdateRequest

    override fun setJoinPermission(permission: String?): CloudProxyGroupUpdateRequest

    override fun setStateUpdating(stateUpdating: Boolean): CloudProxyGroupUpdateRequest

    override fun setStartPriority(priority: Int): CloudProxyGroupUpdateRequest

    override fun submit(): CompletableFuture<CloudProcessGroup>

}