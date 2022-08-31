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
        sender.sendMessage(Chat.colored("${Chat.dash} The discord URL is: &chttps://discord.gg/X7ujJThANP"))
        return false
    }
}