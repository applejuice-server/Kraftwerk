package pink.mino.kraftwerk.listeners

import me.lucko.helper.promise.Promise
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.Scoreboard
import pink.mino.kraftwerk.utils.StatsHandler

class PlayerQuitListener : Listener {
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        e.quitMessage = ChatColor.translateAlternateColorCodes('&', "&8[&4-&8] &c${player.displayName} &8(&4${Bukkit.getServer().onlinePlayers.size - 1}&8/&4${Bukkit.getServer().maxPlayers}&8)")
        Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), Bukkit.getServer().onlinePlayers.size - 1)
        if (StatsHandler.statsPlayers[player.uniqueId] != null) {
            Promise.start()
                .thenApplyAsync { StatsHandler.statsPlayers[player.uniqueId]!!.saveAll() }
                .thenAcceptSync {
                    StatsHandler.statsPlayers.remove(player.uniqueId)
                    print("Saved stats for ${player.name}.")
                }
        }
    }
}