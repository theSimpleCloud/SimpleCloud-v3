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

package eu.thesimplecloud.simplecloud.restserver.service

import com.google.inject.Singleton
import eu.thesimplecloud.simplecloud.api.node.INode
import eu.thesimplecloud.simplecloud.api.service.INodeService
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * Date: 07/07/2021
 * Time: 20:41
 * @author Frederick Baier
 */
@Singleton
class TestNodeService : INodeService {

    override fun findNodeByName(name: String): CompletableFuture<INode> {
        TODO("Not yet implemented")
    }

    override fun findNodesByName(vararg names: String): CompletableFuture<List<INode>> {
        TODO("Not yet implemented")
    }

    override fun findAll(): CompletableFuture<List<INode>> {
        TODO("Not yet implemented")
    }

    override fun findNodeByUniqueId(uniqueId: UUID): CompletableFuture<INode> {
        TODO("Not yet implemented")
    }
}