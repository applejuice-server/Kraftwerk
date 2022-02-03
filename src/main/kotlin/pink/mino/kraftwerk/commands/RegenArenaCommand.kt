package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class RegenArenaCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            Chat.sendMessage(sender, "&cOnly console senders can execute this command.")
            return false
        }
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "pregen Arena 100"
        )
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "mvgamerule doMobSpawning false Arena"
        )
        return true
    }

}