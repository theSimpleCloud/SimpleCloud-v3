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

package app.simplecloud.simplecloud.plugin.proxy.type.bungee

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.distribution.api.Address
import app.simplecloud.simplecloud.plugin.proxy.ProxyController
import app.simplecloud.simplecloud.plugin.proxy.request.ServerConnectedRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerKickRequest
import app.simplecloud.simplecloud.plugin.proxy.request.ServerPreConnectRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.PendingConnection
import net.md_5.bungee.api.event.*
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import java.net.InetSocketAddress

/**
 * Date: 14.01.22
 * Time: 14:34
 * @author Frederick Baier
 *
 */
class BungeeListener(
    private val proxyController: ProxyController,
    private val plugin: Plugin,
    private val proxyServer: ProxyServer
) : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun handleJoin(event: LoginEvent) {
        if (event.isCancelled) return
        val connection = event.connection
        val configuration = createConnectionConfiguration(connection)
        event.registerIntent(this.plugin)
        CloudScope.launch {
            proxyController.handleLogin(configuration)
            event.completeIntent(plugin)
        }
    }

    @EventHandler
    fun handlePostLogin(event: PostLoginEvent) {
        val proxiedPlayer = event.player
        proxiedPlayer.reconnectServer = null
        val configuration = createConnectionConfiguration(proxiedPlayer.pendingConnection)
        CloudScope.launch {
            proxyController.handlePostLogin(configuration)
        }
    }

    @EventHandler
    fun handlePreConnect(event: ServerConnectEvent) {
        if (event.isCancelled) return
        val proxiedPlayer = event.player
        val configuration = createConnectionConfiguration(proxiedPlayer.pendingConnection)
        val currentServerName: String? = proxiedPlayer.server?.info?.name
        try {
            runBlocking {
                val response = proxyController.handleServerPreConnect(
                    ServerPreConnectRequest(
                        configuration,
                        currentServerName,
                        event.target.name
                    )
                )
                val serverInfo = proxyServer.getServerInfo(response.targetProcessName)
                event.target = serverInfo
            }
        } catch (ex: ProxyController.NoLobbyServerFoundException) {
            println("Connect: Found no Lobby-Server for player ${proxiedPlayer.name}")
            proxiedPlayer.disconnect(*TextComponent.fromLegacyText("§cNo fallback server found"))
            event.isCancelled = true
        } catch (ex: ProxyController.NoSuchProcessException) {
            proxiedPlayer.sendMessage(*TextComponent.fromLegacyText("§cProcess not found"))
            event.isCancelled = true
        } catch (ex: ProxyController.IllegalGroupTypeException) {
            proxiedPlayer.sendMessage(*TextComponent.fromLegacyText("§cCannot connect to a proxy process"))
            event.isCancelled = true
        } catch (ex: ProxyController.ProcessNotJoinableException) {
            proxiedPlayer.sendMessage(*TextComponent.fromLegacyText("§cProcess cannot be joined at the moment"))
            event.isCancelled = true
        } catch (ex: ProxyController.NoPermissionToJoinGroupException) {
            proxiedPlayer.sendMessage(*TextComponent.fromLegacyText("§cYou don't have the permission to join this group"))
            event.isCancelled = true
        }

    }

    @EventHandler
    fun handleConnect(event: ServerConnectedEvent) {
        val proxiedPlayer = event.player
        val configuration = createConnectionConfiguration(proxiedPlayer.pendingConnection)
        runBlocking {
            proxyController.handleServerConnected(ServerConnectedRequest(configuration, event.server.info.name))
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleDisconnect(event: PlayerDisconnectEvent) {
        val proxiedPlayer = event.player
        CloudScope.launch {
            proxyController.handleDisconnect(proxiedPlayer.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handleKick(event: ServerKickEvent) {
        val proxiedPlayer = event.player
        val kickReasonString = getKickReasonFromComponent(event.kickReasonComponent)
        val kickRequest = ServerKickRequest(proxiedPlayer.uniqueId, kickReasonString, event.kickedFrom.name)
        runBlocking {
            try {
                val response = proxyController.handleServerKick(kickRequest)
                event.cancelServer = proxyServer.getServerInfo(response.targetProcessName)
                event.isCancelled = true
            } catch (ex: ProxyController.NoLobbyServerFoundException) {
                println("Kick: Found no Lobby-Server for player ${proxiedPlayer.name}")
                proxiedPlayer.disconnect(*TextComponent.fromLegacyText("§cNo fallback server found"))
            }
        }
    }

    private fun getKickReasonFromComponent(kickReasonComponent: Array<BaseComponent>): String {
        return if (kickReasonComponent.isEmpty()) {
            ""
        } else {
            kickReasonComponent[0].toLegacyText()
        }
    }

    private fun createConnectionConfiguration(connection: PendingConnection): PlayerConnectionConfiguration {
        val socketAddress = connection.socketAddress
        socketAddress as InetSocketAddress
        return PlayerConnectionConfiguration(
            connection.uniqueId,
            connection.version,
            connection.name,
            Address(socketAddress.hostString, socketAddress.port),
            connection.isOnlineMode
        )
    }

}