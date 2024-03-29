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

package app.simplecloud.simplecloud.kubernetes.impl.secret

import app.simplecloud.simplecloud.kubernetes.api.exception.KubeException
import app.simplecloud.simplecloud.kubernetes.api.secret.KubeSecret
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Secret
import java.nio.charset.StandardCharsets

/**
 * Date: 29.04.22
 * Time: 23:57
 * @author Frederick Baier
 *
 */
class KubeSecretImpl(
    name: String,
    private val api: CoreV1Api
) : KubeSecret {


    private val name: String = name.lowercase()

    private val secret = readNamespacedSecret()

    private fun readNamespacedSecret(): V1Secret {
        try {
            return api.readNamespacedSecret(this.name, "default", null)
        } catch (ex: ApiException) {
            throw KubeException(ex.responseBody, ex)
        }
    }

    override fun getName(): String {
        return this.name
    }

    override fun getStringValueOf(key: String): String {
        val bytes = secret.data?.get(key)!!
        return String(bytes, StandardCharsets.UTF_8)
    }
}