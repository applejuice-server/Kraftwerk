package pink.mino.kraftwerk.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * @author mrcsm
 * 2022-06-16
 */
class WhitelistStateChangeEvent(enabled: Boolean?) : Event() {

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}