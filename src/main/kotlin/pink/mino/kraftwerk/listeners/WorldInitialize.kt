package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent
import pink.mino.kraftwerk.features.CanePopulator

class WorldInitialize : Listener {
    @EventHandler
    fun onWorldInitialize(e: WorldInitEvent) {
        val world = e.world
        world.populators.add(CanePopulator())
    }
}