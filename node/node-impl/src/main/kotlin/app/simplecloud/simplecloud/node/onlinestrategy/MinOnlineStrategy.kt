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

package app.simplecloud.simplecloud.node.onlinestrategy

import app.simplecloud.simplecloud.api.process.group.CloudProcessGroup
import app.simplecloud.simplecloud.api.process.onlinestrategy.ProcessesOnlineCountStrategy
import app.simplecloud.simplecloud.api.process.onlinestrategy.configuration.ProcessOnlineCountStrategyConfiguration

/**
 * Date: 27.03.22
 * Time: 11:23
 * @author Frederick Baier
 *
 */
class MinOnlineStrategy(
    private val configuration: ProcessOnlineCountStrategyConfiguration
) : ProcessesOnlineCountStrategy {

    private val minCount = configuration.data["min"]!!.toInt()

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getTargetGroupNames(): Set<String> {
        return this.configuration.targetGroupNames
    }

    override fun calculateOnlineCount(group: CloudProcessGroup): Int {
        return this.minCount
    }

    override fun toConfiguration(): ProcessOnlineCountStrategyConfiguration {
        return this.configuration
    }


    class Factory : ProcessesOnlineCountStrategy.Factory {

        override fun create(configuration: ProcessOnlineCountStrategyConfiguration): ProcessesOnlineCountStrategy {
            return MinOnlineStrategy(configuration)
        }

    }

}