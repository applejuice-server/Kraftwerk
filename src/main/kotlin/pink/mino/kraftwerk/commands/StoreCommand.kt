package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class StoreCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        sender.sendMessage(Chat.colored("${Chat.prefix} View the server store @ ${Chat.secondaryColor}${if (SettingsFeature.instance.data!!.getString("config.chat.storeUrl") != null) SettingsFeature.instance.data!!.getString("config.chat.storeUrl") else "no store url setup in config tough tits"}"))
        return false
    }
}