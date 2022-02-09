package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class PvPCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.pvp")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: &f/pvp <on/off>&7.")
            return false
        }
        if (args[0] == "on") {
            SettingsFeature.instance.data!!.set("game.pvp", false)
            for (player in Bukkit.getOnlinePlayers()) {
                Chat.sendMessage(player, "${Chat.prefix} PvP has been &aenabled&7 by &f${sender.name}&7.")
            }
        } else if (args[0] == "off") {
            SettingsFeature.instance.data!!.set("game.pvp", true)
            for (player in Bukkit.getOnlinePlayers()) {
                Chat.sendMessage(player, "${Chat.prefix} PvP has been &cdisabled&7 by &f${sender.name}&7.")
            }
        } else {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid argument: &f/pvp <on/off>&7.")
            return false
        }
        return true
    }
}