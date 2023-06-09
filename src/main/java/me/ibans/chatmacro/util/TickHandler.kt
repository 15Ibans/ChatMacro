package me.ibans.chatmacro.util

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object TickHandler {
    class Action(private val delay: Long, val isPeriodic: Boolean, val function: Action.() -> Unit) {
        private var lastRunTime = System.currentTimeMillis()
        var isStopped = false
            private set

        fun getNextRunTime(): Long {
            return lastRunTime + delay
        }

        fun updateLastRunTime(time: Long) {
            lastRunTime = time
        }

        fun close() {
            isStopped = true
        }
    }

    private val handlers = mutableListOf<Action>()

    @SubscribeEvent
    fun onTick(ev: TickEvent.ClientTickEvent) {
        val time = System.currentTimeMillis()
        val iterator = handlers.iterator()
        while (iterator.hasNext()) {
            val action = iterator.next()
            val function = action.function
            if (time > action.getNextRunTime()) {
                action.function()
                if (action.isPeriodic && !action.isStopped) {
                    action.updateLastRunTime(time)
                } else {
                    iterator.remove()
                }
            }
        }
    }

    fun periodic(delay: Long, function: Action.() -> Unit) {
        val action = Action(delay, true, function)
        handlers.add(action)
    }

    fun await(delay: Long, function: Action.() -> Unit) {
        val action = Action(delay, false, function)
        handlers.add(action)
    }

}