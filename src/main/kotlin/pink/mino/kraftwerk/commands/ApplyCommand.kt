package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat

class ApplyCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        sender.sendMessage(Chat.colored("${Chat.prefix} The staff application URL is: ${Chat.secondaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.staffAppUrl") else "no staff app set in config tough tits"}"))
        return true
    }
}