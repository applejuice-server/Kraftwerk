package pink.mino.kraftwerk.listeners

import me.lucko.helper.Schedulers
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat

class PlayerRespawnListener : Listener{
    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        if (e.player.hasPermission("uhc.staff")) {
            e.respawnLocation = e.player.location
        } else {
            e.respawnLocation = SpawnFeature.instance.spawnLocation
        }
        Schedulers.sync().runLater(runnable@ {
            if (!e.player.hasPermission("uhc.staff")) {
                SpawnFeature.instance.send(e.player)
            } else {
                SpecFeature.instance.spec(e.player)
            }
        }, 1L)
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            if (!e.player.hasPermission("uhc.staff")) {
                e.player.kickPlayer(Chat.colored("&7Thank you for playing!\n\n&7Join our discord for more games: &cdsc.gg/apple-juice"))
            }
        }, 1200L)
    }
}