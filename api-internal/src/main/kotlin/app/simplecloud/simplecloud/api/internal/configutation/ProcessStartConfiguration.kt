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

package app.simplecloud.simplecloud.api.internal.configutation

import app.simplecloud.simplecloud.api.template.ProcessTemplateType

/**
 * Created by IntelliJ IDEA.
 * Date: 04.04.2021
 * Time: 13:47
 * @author Frederick Baier
 */
data class ProcessStartConfiguration(
    val processTemplateName: String,
    val processNumber: Int,
    val imageName: String,
    val maxMemory: Int,
    val maxPlayers: Int,
    val templateType: ProcessTemplateType,
    val isStatic: Boolean,
) {

    fun isProcessNumberSet(): Boolean {
        return this.processNumber != -1
    }

    fun getNewProcessName(): String {
        if (this.isStatic)
            return this.processTemplateName
        return this.processTemplateName + "-" + this.processNumber
    }

}