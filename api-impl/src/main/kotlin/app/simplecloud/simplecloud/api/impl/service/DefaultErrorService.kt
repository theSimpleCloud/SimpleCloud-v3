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

package app.simplecloud.simplecloud.api.impl.service

import app.simplecloud.simplecloud.api.error.Error
import app.simplecloud.simplecloud.api.error.ErrorFactory
import app.simplecloud.simplecloud.api.error.configuration.ErrorConfiguration
import app.simplecloud.simplecloud.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.impl.repository.distributed.DistributedErrorRepository
import app.simplecloud.simplecloud.api.impl.request.error.ErrorCreateRequestImpl
import app.simplecloud.simplecloud.api.internal.service.InternalErrorService
import app.simplecloud.simplecloud.api.request.error.ErrorCreateRequest
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Date: 18.10.22
 * Time: 12:25
 * @author Frederick Baier
 *
 */
class DefaultErrorService(
    private val repository: DistributedErrorRepository,
    private val factory: ErrorFactory,
) : InternalErrorService {


    override suspend fun createErrorInternal(configuration: ErrorConfiguration) {
        this.repository.save(configuration.id, configuration).await()
    }

    override fun findByProcessName(processName: String): CompletableFuture<List<Error>> {
        val errorsFuture = this.repository.findByProcessName(processName)
        return errorsFuture.thenApply { list -> mapConfigurationListToError(list) }
    }

    override fun findAll(): CompletableFuture<List<Error>> {
        val errorsFuture = this.repository.findAll()
        return errorsFuture.thenApply { list -> mapConfigurationListToError(list) }
    }

    override fun findById(id: UUID): CompletableFuture<Error> {
        val completableFuture = this.repository.find(id)
        return completableFuture.thenApply { this.factory.create(it) }
    }

    override fun createCreateRequest(errorConfiguration: ErrorCreateConfiguration): ErrorCreateRequest {
        return ErrorCreateRequestImpl(errorConfiguration, this)
    }

    override suspend fun deleteResolvedErrors() {
        val list = this.repository.findResolvedErrors().await()
        list.forEach { this.repository.remove(it.id).await() }
    }

    private fun mapConfigurationListToError(list: Collection<ErrorConfiguration>): List<Error> {
        return list.map { this.factory.create(it) }
    }

}