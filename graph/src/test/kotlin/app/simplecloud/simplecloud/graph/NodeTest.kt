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

package app.simplecloud.simplecloud.graph

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 28.04.2021
 * Time: 19:32
 * @author Frederick Baier
 */
class NodeTest {

    private lateinit var node: Node<String>

    @BeforeEach
    internal fun setUp() {
        this.node = Node<String>("test")
    }

    @Test
    fun newNode_HasNoSuccessors() {
        Assertions.assertTrue(node.getSuccessors().isEmpty())
    }

    @Test
    fun afterNodeSuccessorAddSuccessorsAreNotEmpty() {
        val node2 = Node("test2")
        node.addSuccessor(node2)
        Assertions.assertFalse(node.getSuccessors().isEmpty())
    }
}