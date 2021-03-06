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

package app.simplecloud.simplecloud.database.mongo.entity

import app.simplecloud.simplecloud.api.process.group.ProcessGroupType
import app.simplecloud.simplecloud.api.process.group.configuration.AbstractCloudProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudLobbyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudProxyProcessGroupConfiguration
import app.simplecloud.simplecloud.api.process.group.configuration.CloudServerProcessGroupConfiguration
import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id

@Entity("groups")
class CombinedProcessGroupEntity(
    @Id
    val name: String,
    val maxMemory: Int,
    val maxPlayers: Int,
    val maintenance: Boolean,
    val imageName: String?,
    val static: Boolean,
    val stateUpdating: Boolean,
    val startPriority: Int,
    val joinPermission: String?,
    val type: ProcessGroupType,
    val lobbyPriority: Int = -1,
    val startPort: Int = -1
) {

    private constructor() : this(
        "<>",
        1,
        1,
        false,
        "",
        false,
        false,
        1,
        "",
        ProcessGroupType.PROXY
    )

    fun toConfiguration(): AbstractCloudProcessGroupConfiguration {
        when (type) {
            ProcessGroupType.LOBBY -> {
                return CloudLobbyProcessGroupConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.static,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                    this.lobbyPriority
                )
            }
            ProcessGroupType.PROXY -> {
                return CloudProxyProcessGroupConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.static,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                    this.startPort
                )
            }
            ProcessGroupType.SERVER -> {
                return CloudServerProcessGroupConfiguration(
                    this.name,
                    this.maxMemory,
                    this.maxPlayers,
                    this.maintenance,
                    this.imageName,
                    this.static,
                    this.stateUpdating,
                    this.startPriority,
                    this.joinPermission,
                )
            }
        }
    }

    companion object {
        fun fromGroupConfiguration(configuration: AbstractCloudProcessGroupConfiguration): CombinedProcessGroupEntity {
            val startPort = if (configuration is CloudProxyProcessGroupConfiguration) configuration.startPort else -1
            val lobbyPriority =
                if (configuration is CloudLobbyProcessGroupConfiguration) configuration.lobbyPriority else -1
            return CombinedProcessGroupEntity(
                configuration.name,
                configuration.maxMemory,
                configuration.maxPlayers,
                configuration.maintenance,
                configuration.imageName,
                configuration.static,
                configuration.stateUpdating,
                configuration.startPriority,
                configuration.joinPermission,
                configuration.type,
                lobbyPriority,
                startPort
            )
        }
    }

}