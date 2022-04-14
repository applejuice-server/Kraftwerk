package pink.mino.kraftwerk.scenarios.list

import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class TimeBombTask(val player: Player, val location: Location, val hologram: Hologram) : BukkitRunnable() {
    var timer = 30
    override fun run() {
        if (timer == 0) {
            location.block.type = Material.AIR
            location.block.getRelative(BlockFace.NORTH).type = Material.AIR
            location.world.createExplosion(location.x, location.y, location.y, 10F, false, true)
            location.world.strikeLightning(location)
            hologram.delete()
            cancel()
        }
        timer--
        if (hologram.size() == 1) hologram.removeLine(0)
        hologram.appendTextLine("&e${timer}s")
    }
}

class TimeBombScenario : Scenario(
    "Time Bomb",
    "Items are placed in a chest when players die, after 30s, that chest will explode.",
    "timebomb",
    Material.TNT
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val chest1 = e.entity.location.block
        chest1.type = Material.CHEST
        val chest2 = e.entity.location.block.getRelative(BlockFace.NORTH)
        chest2.type = Material.CHEST
        val chest = chest2.state as Chest
        for (item in e.drops) {
            if (item != null && item.type != Material.AIR) {
                chest.inventory.addItem(item)
            }
        }
        e.drops.clear()
        val hologram = HologramsAPI.createHologram(JavaPlugin.getPlugin(Kraftwerk::class.java), Location(e.entity.world, e.entity.location.x, e.entity.location.y + 1.5, e.entity.location.z))

        TimeBombTask(e.entity, e.entity.location, hologram).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
    }
}