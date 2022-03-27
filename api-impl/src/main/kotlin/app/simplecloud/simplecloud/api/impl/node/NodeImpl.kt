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

package app.simplecloud.simplecloud.api.impl.node

import app.simplecloud.simplecloud.api.node.Node
import app.simplecloud.simplecloud.api.node.configuration.NodeConfiguration
import app.simplecloud.simplecloud.api.utils.Address
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 28.05.2021
 * Time: 11:17
 * @author Frederick Baier
 */
class NodeImpl(
    private val configuration: NodeConfiguration
) : Node {

    override fun getIgniteId(): UUID {
        return this.configuration.igniteId
    }

    override fun getAddress(): Address {
        return this.configuration.address
    }

    override fun getName(): String {
        return "Node-" + this.configuration.igniteId.toString()
    }

    override fun getIdentifier(): String {
        return this.getName()
    }

    override fun toConfiguration(): NodeConfiguration {
        return this.configuration
    }

}