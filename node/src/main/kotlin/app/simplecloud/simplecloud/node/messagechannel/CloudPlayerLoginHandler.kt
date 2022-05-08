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

package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.player.factory.CloudPlayerFactory
import app.simplecloud.simplecloud.api.internal.configutation.PlayerLoginConfiguration
import app.simplecloud.simplecloud.api.internal.service.InternalCloudPlayerService
import app.simplecloud.simplecloud.api.permission.configuration.PermissionPlayerConfiguration
import app.simplecloud.simplecloud.api.player.CloudPlayer
import app.simplecloud.simplecloud.api.player.PlayerWebConfig
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.OfflineCloudPlayerConfiguration
import app.simplecloud.simplecloud.database.api.DatabaseOfflineCloudPlayerRepository
import org.apache.logging.log4j.LogManager

class CloudPlayerLoginHandler(
    private val configuration: PlayerLoginConfiguration,
    private val playerFactory: CloudPlayerFactory,
    private val databasePlayerRepository: DatabaseOfflineCloudPlayerRepository,
    private val playerService: InternalCloudPlayerService
) {

    suspend fun handleLogin(): CloudPlayerConfiguration {
        logger.info(
            "Player {} is logging in on {}",
            this.configuration.connectionConfiguration.name,
            this.configuration.proxyName
        )
        checkPlayerAlreadyConnected()
        val player = createPlayer()
        savePlayerToDatabase(player)
        player.createUpdateRequest().submit().await()
        return player.toConfiguration()
    }

    private suspend fun checkPlayerAlreadyConnected() {
        if (doesPlayerAlreadyExist()) {
            throw PlayerAlreadyRegisteredException(this.configuration)
        }
    }


    private suspend fun doesPlayerAlreadyExist(): Boolean {
        return runCatching {
            this.playerService.findOnlinePlayerByUniqueId(this.configuration.connectionConfiguration.uniqueId).await()
        }.isSuccess
    }

    private fun savePlayerToDatabase(player: CloudPlayer) {
        val configuration = player.toConfiguration()
        this.databasePlayerRepository.save(configuration.uniqueId, configuration)
    }

    private suspend fun createPlayer(): CloudPlayer {
        try {
            val loadedPlayerConfiguration = loadPlayerFromDatabase()
            return createPlayerFromConfiguration(loadedPlayerConfiguration)
        } catch (e: NoSuchElementException) {
            return createNewCloudPlayer()
        }
    }

    private fun createNewCloudPlayer(): CloudPlayer {
        val connectionConfiguration = this.configuration.connectionConfiguration
        val cloudPlayerConfiguration = CloudPlayerConfiguration(
            connectionConfiguration.name,
            connectionConfiguration.uniqueId,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            0,
            connectionConfiguration,
            connectionConfiguration.name,
            PlayerWebConfig("", false),
            PermissionPlayerConfiguration(
                connectionConfiguration.uniqueId,
                emptyList()
            ),
            null,
            this.configuration.proxyName
        )
        return this.playerFactory.create(cloudPlayerConfiguration, this.playerService)
    }

    private fun createPlayerFromConfiguration(loadedPlayerConfiguration: OfflineCloudPlayerConfiguration): CloudPlayer {
        val cloudPlayerConfiguration = createCloudPlayerConfiguration(loadedPlayerConfiguration)
        return this.playerFactory.create(cloudPlayerConfiguration, this.playerService)
    }

    private fun createCloudPlayerConfiguration(loadedPlayerConfiguration: OfflineCloudPlayerConfiguration): CloudPlayerConfiguration {
        val connectionConfiguration = this.configuration.connectionConfiguration
        return CloudPlayerConfiguration(
            connectionConfiguration.name,
            connectionConfiguration.uniqueId,
            loadedPlayerConfiguration.firstLogin,
            System.currentTimeMillis(),
            loadedPlayerConfiguration.onlineTime,
            connectionConfiguration,
            loadedPlayerConfiguration.displayName,
            loadedPlayerConfiguration.webConfig,
            loadedPlayerConfiguration.permissionPlayerConfiguration,
            null,
            this.configuration.proxyName
        )
    }

    private suspend fun loadPlayerFromDatabase(): OfflineCloudPlayerConfiguration {
        return this.databasePlayerRepository.find(this.configuration.connectionConfiguration.uniqueId).await()
    }

    class PlayerAlreadyRegisteredException(
        configuration: PlayerLoginConfiguration
    ) : Exception("Player ${configuration.connectionConfiguration.name} (${configuration.connectionConfiguration.uniqueId}) is already registered")

    companion object {
        private val logger = LogManager.getLogger(CloudPlayerLoginHandler::class.java)
    }

}
