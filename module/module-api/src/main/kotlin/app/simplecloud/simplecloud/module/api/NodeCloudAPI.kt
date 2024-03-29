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

package app.simplecloud.simplecloud.module.api

import app.simplecloud.simplecloud.api.CloudAPI
import app.simplecloud.simplecloud.module.api.service.ErrorService
import app.simplecloud.simplecloud.module.api.service.LinkService
import app.simplecloud.simplecloud.module.api.service.NodeProcessOnlineStrategyService
import app.simplecloud.simplecloud.module.api.service.ResourceDefinitionService
import app.simplecloud.simplecloud.restserver.api.controller.ControllerHandler

/**
 * Date: 24.08.22
 * Time: 09:51
 * @author Frederick Baier
 *
 */
interface NodeCloudAPI : CloudAPI {

    fun getOnlineStrategyService(): NodeProcessOnlineStrategyService

    fun getErrorService(): ErrorService

    fun getLocalAPI(): LocalAPI

    fun getWebControllerHandler(): ControllerHandler

    fun getResourceDefinitionService(): ResourceDefinitionService

    fun getLinkService(): LinkService

}