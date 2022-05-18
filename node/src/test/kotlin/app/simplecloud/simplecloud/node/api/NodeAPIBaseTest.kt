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

package app.simplecloud.simplecloud.node.api

import app.simplecloud.simplecloud.database.memory.factory.InMemoryRepositorySafeDatabaseFactory
import app.simplecloud.simplecloud.kubernetest.test.KubeTestAPI
import app.simplecloud.simplecloud.node.DatabaseFactoryProvider
import app.simplecloud.simplecloud.node.start.NodeStartTestTemplate

/**
 * Date: 11.05.22
 * Time: 17:33
 * @author Frederick Baier
 *
 */
open class NodeAPIBaseTest {

    private val nodeStartTestTemplate = NodeStartTestTemplate()

    protected lateinit var kubeAPI: KubeTestAPI

    protected lateinit var cloudAPI: NodeCloudAPI

    protected var databaseFactory = InMemoryRepositorySafeDatabaseFactory()

    open fun setUp() {
        this.nodeStartTestTemplate.setUp()
        this.nodeStartTestTemplate.givenKubeAPIWithDatabaseConnection()
        this.databaseFactory = DatabaseFactoryProvider().withFirstUser().get()
        this.nodeStartTestTemplate.given(this.databaseFactory)
        this.cloudAPI = this.nodeStartTestTemplate.startNode()
        this.kubeAPI = nodeStartTestTemplate.kubeAPI
    }

}