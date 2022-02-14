package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ScatterFeature
import pink.mino.kraftwerk.utils.Chat

class ScatterCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.scatter")) {
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        val player = sender as Player
        if (args.isEmpty()) {
            Chat.sendMessage(player, "${Chat.prefix} &7No arguments: &f/scatter <world> <radius> <ffa/teams>&7.")
            return false
        }
        if (args.size != 3) {
            Chat.sendMessage(player, "${Chat.prefix} &7Invalid usage: &f/scatter <world> <radius> <ffa/teams>&7.")
            return false
        }
        if (Bukkit.getWorld(args[0]) == null) {
            Chat.sendMessage(player, "${Chat.prefix} &7Invalid world: &f/scatter <world> <radius> <ffa/teams>&7.")
            return false
        }
        if (args[1].toIntOrNull() == null) {
            Chat.sendMessage(player, "${Chat.prefix} &7Invalid border: &f/scatter <world> <radius> <ffa/teams>&7.")
            return false
        }
        if (args[2] == "ffa") {
            ScatterFeature.scatter(args[2], Bukkit.getWorld(args[0]), args[1].toInt(), false)
        } else if (args[2] == "teams") {
            ScatterFeature.scatter(args[2], Bukkit.getWorld(args[0]), args[1].toInt(), false)
        } else {
            Chat.sendMessage(player, "${Chat.prefix} &7Invalid arguments: &f/scatter <world> <radius> <ffa/teams>&7.")
            return false
        }
        return true
    }
}