package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener : Listener {
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        e.quitMessage = ChatColor.translateAlternateColorCodes('&', "&8[&4-&8] &c${player.displayName} &8(&4${Bukkit.getServer().onlinePlayers.size - 1}&8/&4${Bukkit.getServer().maxPlayers}&8)")
    }
}