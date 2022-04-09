package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat


//internal class SpecMessage(var uuid: UUID, var username: String, var message: String)

class SpecChatCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (SpecFeature.instance.getSpecs().contains(sender.name)) {
            val message = StringBuilder()
            if (args.isEmpty()) {
                sender.sendMessage("${ChatColor.RED}Usage: /sc <message>")
                return true
            }
            for (element in args) {
                message.append("${ChatColor.GRAY}${element}").append(" " + ChatColor.GRAY)
            }
            val msg = message.toString().trim()
            SpecFeature.instance.specChat("&8[&cSpec Chat&8] &f${sender.name} ${Chat.dash} &f${msg}")
        } else {
            sender.sendMessage(Chat.colored("&cYou aren't in spectator mode!"))
            return false
        }
        return true
    }

}