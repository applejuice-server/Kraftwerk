package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat


class FeedCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("uhc.staff.feed")) {
            Chat.sendMessage(sender as Player, "&cYou do not have permission to use this command.")
            return false
        }
        if (args.isEmpty()) {
            val player = sender as Player
            player.foodLevel = 20
            Chat.sendMessage(player, "&7You have fed yourself.")
            return true
        } else {
            if (args[0] == "*") {
                for (online in ArrayList(Bukkit.getServer().onlinePlayers)) {
                    online.foodLevel = 20
                    Chat.sendMessage(online, "You have been fed by &c${sender.name}&7.")
                }
                Chat.sendMessage(sender as Player, "&7You've fed all players.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player, "&cThat player is not online or has never logged onto the server.")
                    return false
                }
                target.foodLevel = 20
                Chat.sendMessage(target, "You've been fed by &c${sender.name}&7.")
                Chat.sendMessage(sender as Player, "Fed &c${target.name}&7.")
                return true
            }
        }
    }

}