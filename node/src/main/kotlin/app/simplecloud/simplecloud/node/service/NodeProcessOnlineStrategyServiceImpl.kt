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

package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.future.CloudScope
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.future
import app.simplecloud.simplecloud.api.impl.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequestImpl
import app.simplecloud.simplecloud.api.impl.request.onlinestrategy.ProcessOnlineCountStrategyDeleteRequestImpl
import app.simplecloud.simplecloud.api.impl.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalNodeProcessOnlineCountStrategyService
import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyCreateRequest
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyDeleteRequest
import app.simplecloud.simplecloud.api.request.onlinestrategy.ProcessOnlineCountStrategyUpdateRequest
import app.simplecloud.simplecloud.node.onlinestrategy.UniversalProcessOnlineCountStrategyFactory
import app.simplecloud.simplecloud.node.repository.distributed.DistributedOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy.MongoOnlineCountStrategyRepository
import app.simplecloud.simplecloud.node.repository.mongo.onlinecountstrategy.OnlineCountStrategyEntity
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessesChecker
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

/**
 * Date: 25.03.22
 * Time: 08:44
 * @author Frederick Baier
 *
 */
@Singleton
class NodeProcessOnlineStrategyServiceImpl @Inject constructor(
    private val injector: Injector,
    private val igniteRepository: DistributedOnlineCountStrategyRepository,
    private val mongoRepository: MongoOnlineCountStrategyRepository,
    private val factory: UniversalProcessOnlineCountStrategyFactory
) : InternalNodeProcessOnlineCountStrategyService {

    private val onlineProcessesChecker by lazy { this.injector.getInstance(NodeOnlineProcessesChecker::class.java) }

    override fun checkProcessOnlineCount() {
        CloudScope.launch {
            onlineProcessesChecker.checkOnlineCount()
        }
    }

    override fun findByName(name: String): CompletableFuture<ProcessesOnlineCountStrategy> = CloudScope.future {
        val configuration = igniteRepository.find(name).await()
        return@future factory.create(configuration)
    }

    override fun findAll(): CompletableFuture<List<ProcessesOnlineCountStrategy>> = CloudScope.future {
        val configurations = igniteRepository.findAll().await()
        return@future configurations.map { factory.create(it) }
    }

    override fun findByProcessGroupName(
        name: String
    ): CompletableFuture<ProcessesOnlineCountStrategy> = CloudScope.future {
        val foundStrategies = igniteRepository.findByTargetProcessGroup(name).await()
        if (foundStrategies.isEmpty()) return@future DefaultProcessesOnlineCountStrategy
        return@future factory.create(foundStrategies.first())
    }

    override fun createCreateRequest(configuration: ProcessOnlineCountStrategyConfiguration): ProcessOnlineCountStrategyCreateRequest {
        return ProcessOnlineCountStrategyCreateRequestImpl(configuration, this)
    }

    override fun createUpdateRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyUpdateRequest {
        return ProcessOnlineCountStrategyUpdateRequestImpl(strategy, this)
    }

    override fun createDeleteRequest(strategy: ProcessesOnlineCountStrategy): ProcessOnlineCountStrategyDeleteRequest {
        return ProcessOnlineCountStrategyDeleteRequestImpl(strategy, this)
    }

    override suspend fun createStrategyInternal(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy {
        val permissionGroup = this.factory.create(configuration)
        updateStrategyInternal(configuration)
        return permissionGroup
    }

    override suspend fun updateStrategyInternal(configuration: ProcessOnlineCountStrategyConfiguration) {
        this.igniteRepository.save(configuration.name, configuration).await()
        saveToDatabase(configuration)
        checkProcessOnlineCount()
    }

    override suspend fun deleteStrategyInternal(strategy: ProcessesOnlineCountStrategy) {
        this.igniteRepository.remove(strategy.getName())
        deleteStrategyFromDatabase(strategy)
        checkProcessOnlineCount()
    }

    private fun deleteStrategyFromDatabase(strategy: ProcessesOnlineCountStrategy) {
        this.mongoRepository.remove(strategy.getName())
    }

    private fun saveToDatabase(configuration: ProcessOnlineCountStrategyConfiguration) {
        val entity = OnlineCountStrategyEntity.fromConfiguration(configuration)
        this.mongoRepository.save(configuration.name, entity)
    }

    object DefaultProcessesOnlineCountStrategy : ProcessesOnlineCountStrategy {

        override fun getTargetGroupNames(): Set<String> {
            return emptySet()
        }

        override fun calculateOnlineCount(group: CloudProcessGroup): Int {
            return 0
        }

        override fun getName(): String {
            return "<default>"
        }

        override fun toConfiguration(): ProcessOnlineCountStrategyConfiguration {
            return ProcessOnlineCountStrategyConfiguration(
                getName(),
                DefaultProcessesOnlineCountStrategy::class.java.name,
                getTargetGroupNames(),
                emptyMap()
            )
        }

    }

}