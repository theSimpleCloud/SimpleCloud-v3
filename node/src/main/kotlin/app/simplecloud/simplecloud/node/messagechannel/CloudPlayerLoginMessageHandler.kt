package app.simplecloud.simplecloud.node.messagechannel

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.player.CloudPlayerFactory
import app.simplecloud.simplecloud.api.messagechannel.handler.MessageHandler
import app.simplecloud.simplecloud.api.player.configuration.CloudPlayerConfiguration
import app.simplecloud.simplecloud.api.player.configuration.PlayerConnectionConfiguration
import app.simplecloud.simplecloud.api.utils.NetworkComponent
import app.simplecloud.simplecloud.node.repository.mongo.player.MongoCloudPlayerRepository
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

class CloudPlayerLoginMessageHandler @Inject constructor(
    private val playerFactory: CloudPlayerFactory,
    private val mongoPlayerRepository: MongoCloudPlayerRepository
) : MessageHandler<PlayerConnectionConfiguration, CloudPlayerConfiguration> {

    override fun handleMessage(
        message: PlayerConnectionConfiguration,
        sender: NetworkComponent
    ): CompletableFuture<CloudPlayerConfiguration> = CloudScope.future {
        return@future CloudPlayerLoginHandler(playerFactory, mongoPlayerRepository, message, sender.getName())
            .handleLogin()
    }




}