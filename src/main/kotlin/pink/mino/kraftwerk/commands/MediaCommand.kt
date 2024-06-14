package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class MediaCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        sender.sendMessage(Chat.colored("${Chat.dash} The media rank application URL is: ${Chat.secondaryColor}${if (SettingsFeature.instance.data!!.getString("config.chat.mediaUrl") != null) SettingsFeature.instance.data!!.getString("config.chat.mediaUrl") else "no media app url setup in config tough tits"}"))
        return false
    }
}