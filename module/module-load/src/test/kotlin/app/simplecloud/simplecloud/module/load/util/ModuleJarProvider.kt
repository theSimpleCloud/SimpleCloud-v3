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

package app.simplecloud.simplecloud.module.load.util

import java.io.File

/**
 * Date: 01.09.22
 * Time: 10:16
 * @author Frederick Baier
 *
 */
object ModuleJarProvider {


    fun failingJar(name: String): File {
        return ModuleJarCreator(
            name,
            "app/simplecloud/simplecloud/module/load/testmodule/FailingModuleMain.class",
            emptyArray(),
            emptyArray()
        ).createJar()
    }

    fun emptyJar(name: String): File {
        return ModuleJarCreator(
            name,
            "app/simplecloud/simplecloud/module/load/testmodule/EmptyModuleMain.class",
            emptyArray(),
            emptyArray()
        ).createJar()
    }

    fun enableAwareJar(name: String): File {
        return ModuleJarCreator(
            name,
            "app/simplecloud/simplecloud/module/load/testmodule/OnEnableAwareModuleMain.class",
            emptyArray(),
            emptyArray()
        ).createJar()
    }

    fun builder(moduleName: String): ModuleJarBuilder {
        return ModuleJarBuilder(moduleName)
    }


}