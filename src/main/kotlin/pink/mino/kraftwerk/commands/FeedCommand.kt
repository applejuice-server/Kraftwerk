package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat


class FeedCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.feed")) {
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage("You can't use this command as you technically aren't a player.")
                return false
            }
            sender.foodLevel = 20
            sender.saturation = 20F
            Chat.sendMessage(sender, "${Chat.dash} &7You have fed yourself.")
            return true
        } else {
            if (args[0] == "*") {
                for (online in ArrayList(Bukkit.getServer().onlinePlayers)) {
                    online.foodLevel = 20
                    online.saturation = 20F
                    Chat.sendMessage(online, "${Chat.dash} You have been fed by &c${sender.name}&7.")
                }
                Chat.sendMessage(sender as Player, "${Chat.dash} &7You've fed all players.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player, "${Chat.dash} &cThat player is not online or has never logged onto the server.")
                    return false
                }
                target.foodLevel = 20
                target.saturation = 20F
                Chat.sendMessage(target, "${Chat.dash} You've been fed by &c${sender.name}&7.")
                Chat.sendMessage(sender as Player, "${Chat.dash} Fed &c${target.name}&7.")
                return true
            }
        }
    }

}