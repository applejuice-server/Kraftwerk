package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkPopulateEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.events.ChunkModifiableEvent

class ChunkPopulateListener : Listener {

    @EventHandler
    fun on(event: ChunkPopulateEvent) {
        val worldName = event.world.name

        val chunkX = event.chunk.x
        val chunkZ = event.chunk.z

        object : BukkitRunnable() {
            override fun run() {
                val world = Bukkit.getWorld(worldName) ?: return
                val chunk = world.getChunkAt(chunkX, chunkZ) ?: return
                Bukkit.getPluginManager().callEvent(ChunkModifiableEvent(chunk))
            }
        }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 400L)
    }
}