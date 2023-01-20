package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class SpawnCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (GameState.currentState == GameState.LOBBY) {
            SpawnFeature.instance.send(sender)
            sender.sendMessage(Chat.colored("${Chat.prefix} You've been sent to spawn."))
        } else {
            Chat.sendMessage(sender, "&cYou can't use this command at the moment.")
        }
        return true
    }

}