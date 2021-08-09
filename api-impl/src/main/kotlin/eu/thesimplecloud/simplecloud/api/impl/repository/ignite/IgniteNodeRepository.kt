/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.simplecloud.api.impl.repository.ignite

import com.google.inject.Inject
import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.impl.ignite.predicate.NodeCompareIgniteIdPredicate
import eu.thesimplecloud.simplecloud.api.node.configuration.NodeConfiguration
import eu.thesimplecloud.simplecloud.api.repository.INodeRepository
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 21.04.2021
 * Time: 21:21
 * @author Frederick Baier
 */
@Singleton
class IgniteNodeRepository @Inject constructor(
    private val ignite: Ignite
) : AbstractIgniteRepository<NodeConfiguration>(), INodeRepository {

    override fun getCache(): IgniteCache<String, NodeConfiguration> {
        return ignite.getOrCreateCache("cloud-nodes")
    }

    override fun findNodeByUniqueId(uniqueId: UUID): CompletableFuture<NodeConfiguration> {
        return executeQueryAndFindFirst(NodeCompareIgniteIdPredicate(uniqueId))
    }
}