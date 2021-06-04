package eu.thesimplecloud.simplecloud.eventapi

import com.google.common.collect.Maps
import eu.thesimplecloud.simplecloud.eventapi.exception.EventException
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList

class DefaultEventManager : IEventManager {

    private val listeners = Maps.newConcurrentMap<Class<out IEvent>, MutableList<RegisteredEventHandler>>()

    override fun registerListener(registerer: IEventRegisterer, listener: IListener) {
        for (method in getValidMethods(listener::class.java)) {
            val eventClass = method.parameterTypes[0] as Class<out IEvent>
            addRegisteredEvent(RegisteredEventHandler.fromEventMethod(registerer, eventClass, listener, method))
        }
    }

    override fun registerEvent(registerer: IEventRegisterer, eventClass: Class<out IEvent>, listener: IListener, eventExecutor: IEventExecutor) {
        addRegisteredEvent(RegisteredEventHandler(registerer, eventClass, listener, eventExecutor))
    }

    override fun unregisterListener(listener: IListener) {
        val allRegisteredEventHandlers = this.listeners.values.flatten()
        val handlersToUnregister = allRegisteredEventHandlers.filter { it.listener === listener }
        handlersToUnregister.forEach { removeRegisteredEvent(it) }
    }

    override fun call(event: IEvent) {
        this.listeners[event::class.java]?.forEach { registeredEvent ->
            registeredEvent.eventExecutor.execute(event)
        }
    }

    override fun unregisterAllListenersByRegisterer(registerer: IEventRegisterer) {
        listeners.values.forEach { list -> list.removeIf { it.registerer == registerer } }
    }

    override fun unregisterAll() {
        this.listeners.clear()
    }


    private fun getValidMethods(listenerClass: Class<out IListener>): List<Method> {
        val methods = listenerClass.declaredMethods
            .filter { it.isAnnotationPresent(CloudEventHandler::class.java) && it.parameterTypes.size == 1 && IEvent::class.java.isAssignableFrom(it.parameterTypes[0]) }
        methods.forEach { it.isAccessible = true }
        return methods
    }


    private fun addRegisteredEvent(registeredEventHandler: RegisteredEventHandler) {
        this.listeners.getOrPut(registeredEventHandler.eventClass, { CopyOnWriteArrayList() }).add(registeredEventHandler)
    }


    private fun removeRegisteredEvent(registeredEventHandler: RegisteredEventHandler) {
        this.listeners[registeredEventHandler.eventClass]?.remove(registeredEventHandler)
    }


    data class RegisteredEventHandler(val registerer: IEventRegisterer, val eventClass: Class<out IEvent>, val listener: IListener, val eventExecutor: IEventExecutor) {

        companion object {

            fun fromEventMethod(registerer: IEventRegisterer, eventClass: Class<out IEvent>, listener: IListener, method: Method): RegisteredEventHandler {
                return RegisteredEventHandler(registerer, eventClass, listener, object : IEventExecutor {

                    override fun execute(event: IEvent) {
                        if (!eventClass.isAssignableFrom(event.javaClass))
                            return
                        try {
                            method.invoke(listener, event)
                        } catch (ex: Exception) {
                            throw EventException(event, ex)
                        }
                    }
                })
            }
        }

    }

}