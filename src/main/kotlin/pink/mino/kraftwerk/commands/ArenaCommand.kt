package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ArenaFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class ArenaCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you aren't technically a player.")
            return false
        }
        if (GameState.currentState == GameState.LOBBY) {
            if (sender.world.name == "Arena") {
                Chat.sendMessage(sender, "&cYou can't use this command in the arena.")
                return false
            }
            ArenaFeature().send(sender)
        } else {
            Chat.sendMessage(sender, "&cThe arena is disabled at the moment.")
        }
        return true
    }
}