package pink.mino.kraftwerk.events

import org.bukkit.Chunk
import org.bukkit.event.HandlerList
import org.bukkit.event.world.ChunkEvent

class ChunkModifiableEvent(chunk: Chunk?) : ChunkEvent(chunk) {

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