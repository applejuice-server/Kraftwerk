package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PlayerUtils

class PMCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (sender.scoreboard.getPlayerTeam(sender) == null) {
            sender.sendMessage("${ChatColor.RED}You must be on a team to send a message.")
            return true
        }
        if (sender.scoreboard.getPlayerTeam(sender) != null) {
            val message = StringBuilder()
            if (args.isEmpty()) {
                sender.sendMessage("${ChatColor.RED}Usage: /pm <message>")
                return true
            }
            for (element in args) {
                message.append(element).append(" ")
            }

            val msg = message.toString().trim()

            for (team in sender.scoreboard.getPlayerTeam(sender).players) {
                if (team is Player) {
                    Chat.sendMessage(team, "&8[&4Team Chat&8] ${PlayerUtils.getPrefix(sender)}${sender.name} ${Chat.dash} &7&o${msg}")
                }
            }
        }
        return true
    }

}