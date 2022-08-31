package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class TeleportCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.tp")) {
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/tp <player> [player]")
            return false
        }
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/tp <player> [player]")
            return false
        }
        when (args.size) {
            2 -> {
                val destination = Bukkit.getPlayer(args[1])
                if (destination == null) {
                    Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/tp <player> [player]")
                    return false
                }
                target.teleport(destination.location)
                Chat.sendMessage(target, "${Chat.dash} Teleported to &f${destination.name}&7.")
            }
            1 -> {
                if (sender !is Player) {
                    sender.sendMessage("You can't use this command as you aren't technically a player.")
                    return false
                }
                sender.teleport(target.location)
                Chat.sendMessage(sender, "${Chat.dash} Teleported to &f${target.name}&7.")
            }
            else -> {
                Chat.sendMessage(sender, "&c???")
                return false
            }
        }
        return true
    }

}