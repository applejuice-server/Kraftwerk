package pink.mino.kraftwerk.listeners

import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener : Listener {

    private var vaultChat: Chat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(Chat::class.java)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val group: String? = vaultChat?.getPrimaryGroup(player)
        val prefix: String = ChatColor.translateAlternateColorCodes('&', vaultChat?.getGroupPrefix(player.world, group))
        if (player.hasPermission("uhc.staff.chat.white")) {
            e.format = prefix + " %s" + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + "%s"
        } else {
            e.format = prefix + " %s" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "%s"
        }
    }

}