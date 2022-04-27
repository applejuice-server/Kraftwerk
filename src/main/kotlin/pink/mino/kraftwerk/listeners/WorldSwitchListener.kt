package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk

class WorldSwitchListener : Listener {
    @EventHandler
    fun onWorldSwitch(e: PlayerChangedWorldEvent) {
        when (e.player.gameMode) {
            GameMode.CREATIVE -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    e.player.gameMode = GameMode.CREATIVE
                }, 1L)
            }
            GameMode.SPECTATOR -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    e.player.gameMode = GameMode.SPECTATOR
                }, 1L)
            }
            GameMode.SURVIVAL -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    e.player.gameMode = GameMode.SURVIVAL
                }, 1L)
            }
            else -> {}
        }
    }
}