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

package app.simplecloud.simplecloud.plugin.server.type.spigot

import app.simplecloud.simplecloud.plugin.proxy.AbstractOnlineCountUpdater
import app.simplecloud.simplecloud.plugin.startup.SelfProcessProvider
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.Server

/**
 * Date: 23.01.22
 * Time: 19:13
 * @author Frederick Baier
 *
 */
@Singleton
class SpigotOnlineCountUpdater @Inject constructor(
    private val server: Server,
    private val selfProcessProvider: SelfProcessProvider
) : AbstractOnlineCountUpdater(selfProcessProvider) {

    override fun getSelfOnlineCount(): Int {
        return this.server.onlinePlayers.size
    }

}