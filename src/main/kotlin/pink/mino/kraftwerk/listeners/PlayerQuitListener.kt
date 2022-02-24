package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.Scoreboard

class PlayerQuitListener : Listener {
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        e.quitMessage = ChatColor.translateAlternateColorCodes('&', "&8[&4-&8] &c${player.displayName} &8(&4${Bukkit.getServer().onlinePlayers.size - 1}&8/&4${Bukkit.getServer().maxPlayers}&8)")
        Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), Bukkit.getServer().onlinePlayers.size - 1)
    }
}