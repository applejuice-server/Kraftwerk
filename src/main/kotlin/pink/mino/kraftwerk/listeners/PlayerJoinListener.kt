package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import pink.mino.kraftwerk.utils.Chat

class PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (player.hasPlayedBefore()) {
            Chat.sendMessage(player, "${Chat.prefix} Welcome back to &4Xestra &cUHC&7, &f${player.displayName}&7!")
        } else {
            for (p in Bukkit.getOnlinePlayers()) {
                Chat.sendMessage(p, "${Chat.prefix} Welcome to &4Xestra &cUHC&7, &f${player.displayName}&7! &8(&c${Bukkit.getOfflinePlayers().size}&8)")
            }
        }
        e.joinMessage = ChatColor.translateAlternateColorCodes('&', "&8[&2+&8] &a${player.displayName} &8(&2${Bukkit.getServer().onlinePlayers.size}&8/&2${Bukkit.getServer().maxPlayers}&8)")
    }
}