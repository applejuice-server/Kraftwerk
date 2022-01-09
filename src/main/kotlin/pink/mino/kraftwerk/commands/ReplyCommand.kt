package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ReplyTo

class ReplyCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        val player = sender as Player
        val uuid = ReplyTo.getRepliedTo(player.uniqueId)
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "&cYou need to send a message.")
            return false
        }

        var message = ""
        for (i in 0 until args.size) message += args[i] + " "
        val target = Bukkit.getPlayer(uuid)
        if (target == null) {
            Chat.sendMessage(sender,"&cYou need a valid user to send this to.")
            return false
        }

        Chat.sendMessage(player, "&7To: &f${target.displayName} &8- &7$message")
        Chat.sendMessage(target, "&7From: &f${player.displayName} &8- &7$message")

        player.playSound(player.location, Sound.NOTE_PLING, 10.toFloat(), 0.toFloat())
        target.playSound(player.location, Sound.NOTE_PLING, 10.toFloat(), 0.toFloat())
        return true
    }
}