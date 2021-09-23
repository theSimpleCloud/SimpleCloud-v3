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

package eu.thesimplecloud.simplecloud.node.process

import com.ea.async.Async.await
import com.google.inject.Injector
import eu.thesimplecloud.simplecloud.api.internal.configutation.ProcessStartConfiguration
import eu.thesimplecloud.simplecloud.api.messagechannel.manager.IMessageChannelManager
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.process.ICloudProcess
import eu.thesimplecloud.simplecloud.api.service.INodeService
import eu.thesimplecloud.simplecloud.node.task.NodeToStartProcessSelectionTask
import eu.thesimplecloud.simplecloud.task.submitter.TaskSubmitter
import java.util.concurrent.CompletableFuture

class MultiNodeProcessStarter(
    private val taskSubmitter: TaskSubmitter,
    private val nodeService: INodeService,
    private val configuration: ProcessStartConfiguration,
    private val injector: Injector
) {

    private val messageChannelManager = this.injector.getInstance(IMessageChannelManager::class.java)

    fun startProcess(): CompletableFuture<ICloudProcess> {
        val node = selectNodeForProcess()
        return startProcessOnNode(node)
    }

    private fun selectNodeForProcess(): INode {
        return await(this.taskSubmitter.submit(
            NodeToStartProcessSelectionTask(
                this.configuration.maxMemory,
                this.nodeService
            )
        ))
    }

    private fun startProcessOnNode(node: INode): CompletableFuture<ICloudProcess> {
        val messageChannel = this.messageChannelManager.getMessageChannelByName<ProcessStartConfiguration, ICloudProcess>("start_process")!!
        return messageChannel.createMessageRequest(this.configuration, node).submit()
    }

}