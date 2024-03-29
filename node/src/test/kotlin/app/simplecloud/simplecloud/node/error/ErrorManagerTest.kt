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

package app.simplecloud.simplecloud.node.error

import app.simplecloud.simplecloud.api.future.await
import app.simplecloud.simplecloud.api.future.completedFuture
import app.simplecloud.simplecloud.module.api.error.Error
import app.simplecloud.simplecloud.module.api.error.ErrorTypeFixedChecker
import app.simplecloud.simplecloud.module.api.error.configuration.ErrorCreateConfiguration
import app.simplecloud.simplecloud.module.api.internal.service.InternalErrorService
import app.simplecloud.simplecloud.node.api.NodeAPIBaseTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

/**
 * Date: 10.10.22
 * Time: 12:24
 * @author Frederick Baier
 *
 */
class ErrorManagerTest : NodeAPIBaseTest() {

    private lateinit var errorService: InternalErrorService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        this.errorService = this.cloudAPI.getErrorService()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }


    @Test
    fun createError_willNotFail(): Unit = runBlocking {
        createDefaultUnresolvableError()
    }

    @Test
    fun createError_errorWillBeInCache(): Unit = runBlocking {
        createDefaultUnresolvableError()
        assertErrorsCount(1)
    }

    @Test
    fun createSameErrorTwice_errorCountWillBe2(): Unit = runBlocking {
        createDefaultUnresolvableError()
        createDefaultUnresolvableError()
        assertErrorsCount(2)
    }

    @Test
    fun unresolvableErrorWillNotBeResolvedAfterCallingResolveErrors(): Unit = runBlocking {
        createDefaultUnresolvableError()
        errorService.deleteResolvedErrors(this@ErrorManagerTest.cloudAPI)
        assertErrorsCount(1)
    }

    @Test
    fun instantResolvableErrorWillBeResolvedAfterCallingResolveErrors(): Unit = runBlocking {
        createDefaultResolvableError()
        errorService.deleteResolvedErrors(this@ErrorManagerTest.cloudAPI)
        assertErrorsCount(0)
    }

    private fun assertErrorsCount(count: Int) = runBlocking {
        val errors = errorService.findAll().await()
        Assertions.assertEquals(count, errors.size)
    }

    private fun createDefaultResolvableError() {
        this.errorService.registerErrorType(3, InstantFixedErrorTypeFixedChecker())
        createError(
            ErrorCreateConfiguration(
                3,
                "Test",
                "Long Test",
                "Lobby-1",
                emptyMap(),
            )
        )
    }

    class InstantFixedErrorTypeFixedChecker : ErrorTypeFixedChecker {
        override fun isErrorFixed(error: Error): CompletableFuture<Boolean> {
            return completedFuture(true)
        }

    }

    private fun createDefaultUnresolvableError() = runBlocking {
        createError(
            ErrorCreateConfiguration(
                -1,
                "Test",
                "Long Test",
                "Lobby-1",
                emptyMap()
            )
        )
    }

    private fun createError(errorCreateConfiguration: ErrorCreateConfiguration) = runBlocking {
        errorService.createCreateRequest(errorCreateConfiguration).submit().await()
    }

}