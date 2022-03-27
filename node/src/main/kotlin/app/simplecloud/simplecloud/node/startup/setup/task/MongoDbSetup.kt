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

package app.simplecloud.simplecloud.node.startup.setup.task

import app.simplecloud.simplecloud.node.startup.setup.body.MongoSetupResponseBody
import app.simplecloud.simplecloud.restserver.setup.RestSetupManager
import app.simplecloud.simplecloud.restserver.setup.type.Setup
import org.apache.logging.log4j.LogManager

/**
 * Created by IntelliJ IDEA.
 * Date: 07/08/2021
 * Time: 00:06
 * @author Frederick Baier
 */
class MongoDbSetup(
    private val restSetupManager: RestSetupManager
) {

    fun executeSetup(): String {
        logger.info("Executing MongoDB setup")
        val mongoSetupResponseBody = this.restSetupManager.setNextSetup(createSetup()).join()
        if (mongoSetupResponseBody.mongoMode == MongoSetupResponseBody.MongoMode.CREATE) {
            return createMongoDockerContainer(mongoSetupResponseBody)
        }
        return mongoSetupResponseBody.connectionString
    }

    private fun createSetup(): Setup<MongoSetupResponseBody> {
        return Setup("mongo", getMongoModePossibilities(), MongoSetupResponseBody::class)
    }

    private fun getMongoModePossibilities(): Array<MongoSetupResponseBody.MongoMode> {
        if (false) {
            return MongoSetupResponseBody.MongoMode.values()
        }
        return arrayOf(MongoSetupResponseBody.MongoMode.EXTERNAL)
    }

    private fun createMongoDockerContainer(mongoSetupResponseBody: MongoSetupResponseBody): String {
        throw IllegalStateException("Cannot create mongodb container because docker is not available")
    }

    companion object {
        private val logger = LogManager.getLogger(MongoDbSetup::class.java)
    }

}