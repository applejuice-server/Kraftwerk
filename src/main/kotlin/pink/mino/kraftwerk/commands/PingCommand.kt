package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class PingCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player && args.isEmpty()) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        val player: Player? = if (args.isEmpty()) {
            sender as Player
        } else {
            Bukkit.getPlayer(args[0])
        }
        if (player == null) {
            Chat.sendMessage(sender, "&cInvalid player!")
        }
        val ping = (player as CraftPlayer).handle.ping
        Chat.sendMessage(sender, "${Chat.prefix} &f${player.name}'s&7 ping is &f${ping}ms&7.")
        return true
    }
}