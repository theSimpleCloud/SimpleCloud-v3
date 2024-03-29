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

rootProject.name = "simplecloud"
include("api")
include("api-impl")
include("api-internal")
include("event-api")
include("kubernetes")
include("rest-server")
include("node")
include("plugin-parent")
include("content-server")
include("process-prepare")
include("plugin-parent:bungeecord")
findProject(":plugin-parent:bungeecord")?.name = "bungeecord"
include("plugin-parent:plugin")
findProject(":plugin-parent:plugin")?.name = "plugin"
include("plugin-parent:spigot")
findProject(":plugin-parent:spigot")?.name = "spigot"
include("database")
include("database:database-api")
findProject(":database:database-api")?.name = "database-api"
include("database:database-inmemory")
findProject(":database:database-inmemory")?.name = "database-inmemory"
include("database:database-mongo")
findProject(":database:database-mongo")?.name = "database-mongo"
include("kubernetes:kubernetes-api")
findProject(":kubernetes:kubernetes-api")?.name = "kubernetes-api"
include("kubernetes:kubernetes-impl")
findProject(":kubernetes:kubernetes-impl")?.name = "kubernetes-impl"
include("kubernetes:kubernetes-test")
findProject(":kubernetes:kubernetes-test")?.name = "kubernetes-test"
include("rest-server:rest-server-base")
findProject(":rest-server:rest-server-base")?.name = "rest-server-base"
include("rest-server:rest-server-api")
findProject(":rest-server:rest-server-api")?.name = "rest-server-api"
include("bootstrap")
include("plugin-parent:minestom")
findProject(":plugin-parent:minestom")?.name = "minestom"
include("plugin-parent:velocity")
findProject(":plugin-parent:velocity")?.name = "velocity"
include("module")
include("module:module-api")
findProject(":module:module-api")?.name = "module-api"
include("module:module-load")
findProject(":module:module-load")?.name = "module-load"
include("graph")
include("module:module-api-impl")
findProject(":module:module-api-impl")?.name = "module-api-impl"
include("module:module-api-internal")
findProject(":module:module-api-internal")?.name = "module-api-internal"
