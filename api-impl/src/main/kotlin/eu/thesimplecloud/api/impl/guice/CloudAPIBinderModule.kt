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

package eu.thesimplecloud.api.impl.guice

import com.google.inject.AbstractModule
import eu.thesimplecloud.api.impl.messagechannel.MessageChannelManager
import eu.thesimplecloud.api.impl.repository.node.IgniteNodeRepository
import eu.thesimplecloud.api.impl.repository.process.IgniteCloudProcessRepository
import eu.thesimplecloud.api.messagechannel.manager.IMessageChannelManager
import eu.thesimplecloud.api.repository.node.INodeRepository
import eu.thesimplecloud.api.repository.process.ICloudProcessRepository
import org.apache.ignite.Ignite

/**
 * Created by IntelliJ IDEA.
 * Date: 31.05.2021
 * Time: 11:31
 * @author Frederick Baier
 */
class CloudAPIBinderModule(
    private val igniteInstance: Ignite
) : AbstractModule() {

    override fun configure() {
        bind(Ignite::class.java).toInstance(igniteInstance)
        bind(IMessageChannelManager::class.java).to(MessageChannelManager::class.java)
        bind(INodeRepository::class.java).to(IgniteNodeRepository::class.java)
        bind(ICloudProcessRepository::class.java).to(IgniteCloudProcessRepository::class.java)
    }

}