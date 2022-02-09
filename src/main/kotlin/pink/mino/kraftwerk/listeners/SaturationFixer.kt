package pink.mino.kraftwerk.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import java.util.*


class SaturationFixer : Listener {
    @EventHandler
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        val before = player.saturation
        object : BukkitRunnable() {
            override fun run() {
                val change = player.saturation - before
                player.saturation = (before + change * 2.5).toFloat()
            }
        }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 1L)
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (event.foodLevel < (event.entity as Player).foodLevel) {
            event.isCancelled = Random().nextInt(100) < 66
        }
    }
}