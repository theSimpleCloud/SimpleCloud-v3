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

package eu.thesimplecloud.simplecloud.api.impl.process.group

import eu.thesimplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import eu.thesimplecloud.simplecloud.api.process.group.ProcessGroupType
import eu.thesimplecloud.simplecloud.api.process.group.ICloudServerGroup
import eu.thesimplecloud.simplecloud.api.service.*

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2021
 * Time: 22:17
 * @author Frederick Baier
 */
class CloudServerProcessGroup constructor(
    private val configuration: CloudServerProcessGroupConfiguration,
    private val templateService: ITemplateService,
    private val processVersionService: IProcessVersionService,
    private val jvmArgumentsService: IJvmArgumentsService,
    private val processOnlineCountService: IProcessOnlineCountService,
    private val nodeService: INodeService,
    private val processService: ICloudProcessService,
) : AbstractCloudProcessGroup(
    configuration,
    templateService,
    processVersionService,
    jvmArgumentsService,
    processOnlineCountService,
    nodeService,
    processService
), ICloudServerGroup {

    override fun getProcessGroupType(): ProcessGroupType {
        return ProcessGroupType.SERVER
    }
    override fun toConfiguration(): AbstractCloudProcessGroupConfiguration {
        return this.configuration
    }


}