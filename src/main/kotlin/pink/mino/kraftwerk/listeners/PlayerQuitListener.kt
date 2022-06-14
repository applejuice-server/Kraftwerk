package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PlayerUtils
import pink.mino.kraftwerk.utils.Scoreboard

class PlayerQuitListener : Listener {
    private var vaultChat: net.milkbowl.vault.chat.Chat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(net.milkbowl.vault.chat.Chat::class.java)
    }
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        val group: String = vaultChat!!.getPrimaryGroup(player)
        val prefix: String = if (vaultChat!!.getGroupPrefix(player.world, group) != "&7") Chat.colored(vaultChat!!.getGroupPrefix(player.world, group)) else Chat.colored("&c")
        e.quitMessage = ChatColor.translateAlternateColorCodes('&', "&8(&4-&8) ${prefix}${player.displayName} &8[&4${Bukkit.getOnlinePlayers().size - 1}&8/&4${Bukkit.getServer().maxPlayers}&8]")
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), Math.max(PlayerUtils.getPlayingPlayers().size, 0))
        }, 3L)
    }
}