package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.utils.Chat

class StaffChatCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender.hasPermission("uhc.staff")) {
            val message = StringBuilder()
            if (args.isEmpty()) {
                sender.sendMessage("${ChatColor.RED}Usage: /ac <message>")
                return true
            }
            for (element in args) {
                message.append(element).append(" ")
            }
            val msg = message.toString().trim()
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("uhc.staff")) {
                    Chat.sendMessage(player, "&8[${Chat.primaryColor}Staff Chat&8] ${Chat.secondaryColor}${sender.name} ${Chat.dash} &f&o${msg}")
                }
            }
        } else {
            sender.sendMessage(Chat.colored("&cYou aren't in spectator mode!"))
            return false
        }
        return true
    }

}