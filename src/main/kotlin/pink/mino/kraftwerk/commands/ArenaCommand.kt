package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ArenaFeature
import pink.mino.kraftwerk.features.SpawnFeature
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
                SpawnFeature.instance.send(sender)
                sender.sendMessage(Chat.colored("${Chat.prefix} &7You left the arena."))
                return false
            }
            ArenaFeature.instance.send(sender)
            Chat.sendMessage(sender, "${Chat.prefix} Welcome to the arena, &f${sender.name}&7!")
            Chat.sendMessage(sender, "&8(&7Cross-teaming in the arena is not allowed!&8)")
        } else {
            Chat.sendMessage(sender, "&cThe arena is disabled at the moment.")
        }
        return true
    }
}