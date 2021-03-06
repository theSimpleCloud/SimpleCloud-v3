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

package app.simplecloud.simplecloud.node.startup.prepare

import app.simplecloud.simplecloud.node.api.NodeCloudAPI
import app.simplecloud.simplecloud.node.defaultcontroller.v1.*
import app.simplecloud.simplecloud.restserver.api.RestServer
import app.simplecloud.simplecloud.restserver.api.auth.AuthService
import app.simplecloud.simplecloud.restserver.api.controller.ControllerHandlerFactory

/**
 * Created by IntelliJ IDEA.
 * Date: 04/08/2021
 * Time: 11:22
 * @author Frederick Baier
 */
class RestServerStartTask(
    private val cloudAPI: NodeCloudAPI,
    private val controllerHandlerFactory: ControllerHandlerFactory,
    private val restServer: RestServer,
    private val authService: AuthService
) {

    private val controllerHandler = this.controllerHandlerFactory.create(restServer)

    init {
        this.restServer.setAuthService(authService)
    }

    fun run() {
        registerController()
    }

    private fun registerController() {
        this.controllerHandler.registerController(LoginController(this.authService))

        this.controllerHandler.registerController(ProcessGroupController(this.cloudAPI.getProcessGroupService()))

        this.controllerHandler.registerController(
            ProcessController(
                this.cloudAPI.getProcessService(),
                this.cloudAPI.getProcessGroupService()
            )
        )

        this.controllerHandler.registerController(NodeController(this.cloudAPI.getNodeService()))

        this.controllerHandler.registerController(
            PermissionGroupController(
                this.cloudAPI.getPermissionGroupService(),
                this.cloudAPI.getPermissionFactory()
            )
        )

        this.controllerHandler.registerController(
            PlayerController(
                this.cloudAPI.getCloudPlayerService(),
                this.cloudAPI.getPermissionFactory()
            )
        )

        this.controllerHandler.registerController(OnlineStrategyController(this.cloudAPI.getOnlineStrategyService()))
    }


}