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

package app.simplecloud.simplecloud.eventapi.test

import app.simplecloud.simplecloud.eventapi.Event
import app.simplecloud.simplecloud.eventapi.EventExecutor
import app.simplecloud.simplecloud.eventapi.Listener
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Date: 14.05.22
 * Time: 12:40
 * @author Frederick Baier
 *
 */
class EventAPICallTest : EventAPITest() {

    @Test
    fun registerListener_doNotCallEvent_ListenerWillNotBeCalled() {
        val testEventListener = TestEventListener()
        this.eventManager.registerListener(eventRegisterer, testEventListener)
        Assertions.assertFalse(testEventListener.wasEventCalled)
    }

    @Test
    fun registerListener_callEvent_ListenerWillBeCalled() {
        val testEventListener = TestEventListener()
        this.eventManager.registerListener(eventRegisterer, testEventListener)
        this.eventManager.call(TestEvent())
        Assertions.assertTrue(testEventListener.wasEventCalled)
    }

    @Test
    fun registerEventHandler_doNotCallEvent_handlerWasNotCalled() {
        val listenerObj = object : Listener {}
        val eventExecutor = TestEventExecutor()
        this.eventManager.registerEvent(eventRegisterer, TestEvent::class.java, listenerObj, eventExecutor)
        Assertions.assertNull(eventExecutor.lastCalledEvent)
    }

    @Test
    fun registerEventHandler_callEvent_handlerWillBeCalled() {
        val listenerObj = object : Listener {}
        val eventExecutor = TestEventExecutor()
        this.eventManager.registerEvent(eventRegisterer, TestEvent::class.java, listenerObj, eventExecutor)
        val calledEvent = TestEvent()
        this.eventManager.call(calledEvent)
        Assertions.assertEquals(calledEvent, eventExecutor.lastCalledEvent)
    }

    class TestEventExecutor() : EventExecutor {

        var lastCalledEvent: Event? = null
            private set

        override fun execute(event: Event) {
            this.lastCalledEvent = event
        }

    }

}