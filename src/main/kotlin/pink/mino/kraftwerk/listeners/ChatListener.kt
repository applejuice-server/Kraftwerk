package pink.mino.kraftwerk.listeners

import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import pink.mino.kraftwerk.utils.PlayerUtils

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
        if (player.hasPermission("uhc.donator.emotes")) {
            if (e.message.contains(":shrug:", true)) {
                e.message = e.message.replace(":shrug:", pink.mino.kraftwerk.utils.Chat.colored("&e¯\\_(ツ)_/¯&r"))
            }
            if (e.message.contains("<3", true)) {
                e.message = e.message.replace("<3", pink.mino.kraftwerk.utils.Chat.colored("&c❤&r"))
            }
            if (e.message.contains("o/", true)) {
                e.message = e.message.replace("o/", pink.mino.kraftwerk.utils.Chat.colored("&d(・∀・)ノ&r"))
            }
            if (e.message.contains(":star:", true)) {
                e.message = e.message.replace(":star:", pink.mino.kraftwerk.utils.Chat.colored("&e✰&r"))
            }
            if (e.message.contains(":100:", true)) {
                e.message = e.message.replace(":100:", pink.mino.kraftwerk.utils.Chat.colored("&c&o&l&n100&r"))
            }
            if (e.message.contains("o7", true)) {
                e.message = e.message.replace("o7", pink.mino.kraftwerk.utils.Chat.colored("&e(｀-´)>&r"))
            }
            if (e.message.contains(":blush:", true)) {
                e.message = e.message.replace(":blush:", pink.mino.kraftwerk.utils.Chat.colored("&d(◡‿◡✿)&r"))
            }
        }
        if (player.hasPermission("uhc.donator.emotes")) {
            e.format = prefix + pink.mino.kraftwerk.utils.Chat.colored("${PlayerUtils.getPrefix(player)}%s") + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + "%s"
        } else {
            e.format = prefix + pink.mino.kraftwerk.utils.Chat.colored("${PlayerUtils.getPrefix(player)}%s") + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "%s"
        }
    }

}