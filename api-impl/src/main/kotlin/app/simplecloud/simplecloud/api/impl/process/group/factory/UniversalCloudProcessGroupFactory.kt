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

package app.simplecloud.simplecloud.api.impl.process.group.factory

import app.simplecloud.simplecloud.api.internal.service.InternalCloudProcessGroupService
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.group.ProcessTemplateType
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration

/**
 * Created by IntelliJ IDEA.
 * Date: 02/07/2021
 * Time: 11:03
 * @author Frederick Baier
 */
class UniversalCloudProcessGroupFactory(
    private val lobbyGroupFactory: CloudLobbyGroupFactory,
    private val proxyGroupFactory: CloudProxyGroupFactory,
    private val serverGroupFactory: CloudServerGroupFactory
) {

    fun create(
        configuration: AbstractCloudProcessGroupConfiguration,
        internalService: InternalCloudProcessGroupService
    ): CloudProcessGroup {
        return when (configuration.type) {
            ProcessTemplateType.PROXY -> {
                this.proxyGroupFactory.create(configuration as CloudProxyProcessGroupConfiguration, internalService)
            }

            ProcessTemplateType.LOBBY -> {
                this.lobbyGroupFactory.create(configuration as CloudLobbyProcessGroupConfiguration, internalService)
            }

            ProcessTemplateType.SERVER -> {
                this.serverGroupFactory.create(configuration as CloudServerProcessGroupConfiguration, internalService)
            }
        }
    }

}