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

package app.simplecloud.simplecloud.plugin.proxy.request.handler

import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.process.group.CloudLobbyGroup
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.service.CloudProcessGroupService
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.util.PlayerProcessGroupJoinChecker

class PlayerLobbyFinder(
    private val player: CloudPlayer,
    private val groupService: CloudProcessGroupService
) {

    private val allGroups = this.groupService.findAll().join()

    suspend fun findLobby(): String {
        val groups = getLobbyGroupsThePlayerIsAllowedToJoinSorted()
        for (group in groups) {
            val processes = group.getProcesses().join()
            val notFullProcesses = processes.filterNot { it.isFull() }
            if (processes.isEmpty()) continue
            return notFullProcesses.first().getName()
        }
        throw ProxyController.NoLobbyServerFoundException()
    }

    private suspend fun getLobbyGroupsThePlayerIsAllowedToJoinSorted(): List<CloudLobbyGroup> {
        val lobbyGroups = this.allGroups.filterIsInstance<CloudLobbyGroup>()
        val notInMaintenanceGroups = lobbyGroups.filter { isPlayerAllowedToJoin(it) }
        return notInMaintenanceGroups.sortedByDescending { it.getLobbyPriority() }
    }

    private suspend fun isPlayerAllowedToJoin(group: CloudProcessGroup): Boolean {
        return PlayerProcessGroupJoinChecker(this.player, group).isAllowedToJoin()
    }




}