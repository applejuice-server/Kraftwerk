package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (player.hasPlayedBefore()) {
            Chat.sendMessage(player, "${Chat.prefix} Welcome back to &capple&ajuice&7, &f${player.displayName}&7!")
        } else {
            for (p in Bukkit.getOnlinePlayers()) {
                Chat.sendMessage(p, "${Chat.prefix} Welcome to &capple&ajuice&7, &f${player.displayName}&7! &8(&c#${Bukkit.getOfflinePlayers().size}&8)")
            }
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                SpawnFeature.instance.send(player)
            }, 20L)
        }
        e.joinMessage = ChatColor.translateAlternateColorCodes('&', "&8[&2+&8] &a${player.displayName} &8(&2${Bukkit.getServer().onlinePlayers.size}&8/&2${Bukkit.getServer().maxPlayers}&8)")
        if (GameState.currentState == GameState.LOBBY) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                SpawnFeature.instance.send(player)
            }, 1L)
        }
    }
}