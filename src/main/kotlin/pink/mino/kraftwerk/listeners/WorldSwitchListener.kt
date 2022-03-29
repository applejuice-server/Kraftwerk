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
        if (e.player.gameMode == GameMode.CREATIVE) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.player.gameMode = GameMode.CREATIVE
            }, 20L)
        } else if (e.player.gameMode == GameMode.SPECTATOR) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.player.gameMode = GameMode.SPECTATOR
            }, 20L)
        } else if (e.player.gameMode == GameMode.SURVIVAL) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.player.gameMode = GameMode.SURVIVAL
            }, 20L)
        }
    }
}