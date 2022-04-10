package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class VoteYesCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).vote == null) {
            Chat.sendMessage(sender, "&cThere is no poll running at the moment.")
            return false
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).vote!!.voted.contains(sender)) {
            Chat.sendMessage(sender, "&cYou already voted.")
            return false
        }
        JavaPlugin.getPlugin(Kraftwerk::class.java).vote!!.yes += 1
        Chat.sendMessage(sender, "${Chat.prefix} Successfully voted &ayes&7 on the current poll.")
        return true
    }
}