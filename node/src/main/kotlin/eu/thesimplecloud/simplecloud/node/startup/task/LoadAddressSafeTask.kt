/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.node.startup.task

import com.ea.async.Async.await
import com.fasterxml.jackson.databind.ObjectMapper
import eu.thesimplecloud.simplecloud.api.future.completedFuture
import eu.thesimplecloud.simplecloud.api.utils.Address
import eu.thesimplecloud.simplecloud.node.startup.NodeStartupSetupHandler
import eu.thesimplecloud.simplecloud.node.startup.setup.task.NodeAddressSetupTask
import eu.thesimplecloud.simplecloud.node.util.Logger
import java.io.File
import java.util.concurrent.CompletableFuture

class LoadAddressSafeTask(
    private val nodeSetupHandler: NodeStartupSetupHandler,
    private val bindAddressArgument: Address?
) {

    private val objectMapper = ObjectMapper()

    fun run(): CompletableFuture<Address> {
        Logger.info("Loading Address")
        if (this.bindAddressArgument != null)
            return completedFuture(bindAddressArgument)
        return loadAddress()
    }

    private fun loadAddress(): CompletableFuture<Address> {
        if (ADDRESS_FILE.exists()) {
            return loadAddressFromFile()
        }
        return executeSetupAndSafeAddressToFile()
    }

    private fun executeSetupAndSafeAddressToFile(): CompletableFuture<Address> {
        val address = await(executeSetup())
        saveAddressToFile(address)
        return completedFuture(address)
    }

    private fun saveAddressToFile(address: Address) {
        this.objectMapper.writeValue(ADDRESS_FILE, address)
    }

    private fun executeSetup(): CompletableFuture<Address> {
        return this.nodeSetupHandler.executeSetupTask() {
            NodeAddressSetupTask(it).run()
        }
    }

    private fun loadAddressFromFile(): CompletableFuture<Address> {
        return completedFuture(
            this.objectMapper.readValue(ADDRESS_FILE, Address::class.java)
        )
    }

    companion object {
        val ADDRESS_FILE = File("address.json")
    }

}