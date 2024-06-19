package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class BuildModeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.admin.buildmode")) {
                Chat.sendMessage(sender, "&cOnly admins can execute this command.")
                return false
            }
        }
        if (sender !is Player) {
            sender.sendMessage("You can't use this command.")
            return true
        }
        if (Kraftwerk.instance.buildMode[sender.uniqueId] == false || Kraftwerk.instance.buildMode[sender.uniqueId] == null) {
            Chat.sendMessage(sender, "${Chat.prefix} Build mode has been &aenabled&7.")
            Kraftwerk.instance.buildMode[sender.uniqueId] = true
            return true
        } else {
            Chat.sendMessage(sender, "${Chat.prefix} Build mode has been &cdisabled&7.")
            Kraftwerk.instance.buildMode[sender.uniqueId] = false
            return true
        }
    }
}