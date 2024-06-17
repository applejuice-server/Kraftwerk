package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.utils.Chat

class DiscordCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        sender.sendMessage(Chat.colored("${Chat.prefix} The discord URL is: ${Chat.secondaryColor}${if (pink.mino.kraftwerk.features.SettingsFeature.instance.data!!.getString("config.chat.discordUrl") != null) pink.mino.kraftwerk.features.SettingsFeature.instance.data!!.getString("config.chat.discordUrl") else "no discord url set in config tough tits"}"))
        return false
    }
}