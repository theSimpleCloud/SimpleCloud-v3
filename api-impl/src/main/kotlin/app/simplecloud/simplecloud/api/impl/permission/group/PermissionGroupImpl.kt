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

package app.simplecloud.simplecloud.api.impl.permission.group

import app.simplecloud.simplecloud.api.impl.permission.entity.PermissionEntityImpl
import app.simplecloud.simplecloud.api.permission.Permission
import app.simplecloud.simplecloud.api.permission.PermissionGroup
import app.simplecloud.simplecloud.api.permission.configuration.PermissionGroupConfiguration
import app.simplecloud.simplecloud.api.service.PermissionGroupService

/**
 * Date: 19.03.22
 * Time: 19:55
 * @author Frederick Baier
 *
 */
class PermissionGroupImpl constructor(
    private val configuration: PermissionGroupConfiguration,
    factory: Permission.Factory,
    permissionGroupService: PermissionGroupService,
) : PermissionEntityImpl(
    configuration.permissions.map { factory.create(it) },
    permissionGroupService
), PermissionGroup {

    override fun getName(): String {
        return this.configuration.name
    }

    override fun getPriority(): Int {
        return this.configuration.priority
    }

    override fun toConfiguration(): PermissionGroupConfiguration {
        return this.configuration
    }

}