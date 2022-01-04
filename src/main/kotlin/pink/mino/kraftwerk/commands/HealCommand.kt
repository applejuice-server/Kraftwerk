package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class HealCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("uhc.command.heal")) {
            Chat.sendMessage(sender as Player, "&cYou do not have permission to use this command.")
            return false
        }
        if (args.isEmpty()) {
            val player = sender as Player
            player.health = 20.0
            Chat.sendMessage(player, "&7You have healed yourself.")
            return true
        } else {
            if (args[0] == "*") {
                for (online in ArrayList(Bukkit.getServer().onlinePlayers)) {
                    online.health = 20.0
                    Chat.sendMessage(online, "&7You have been healed by §c${sender.name}§7.")
                }
                Chat.sendMessage(sender as Player, "&7You've healed all players.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player,"&cThat player is not online or has never logged onto the server.")
                    return true
                }
                target.health = 20.0
                Chat.sendMessage(target, "&7You've been healed by &c${sender.name}&7.")
                Chat.sendMessage(sender as Player, "&7Healed &c${target.name}&7.")
                return true
            }
        }
    }

}