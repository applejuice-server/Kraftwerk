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
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        val uuid = ReplyTo.getRepliedTo(sender.uniqueId)
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "&cYou need to send a message.")
            return false
        }

        var message = ""
        for (i in 0 until args.size) message += args[i] + " "
        val target = Bukkit.getPlayer(uuid)
        if (target == null) {
            Chat.sendMessage(sender, "&cYou need a valid user to send this to.")
            return false
        }

        ReplyTo.setRepliedTo(sender.uniqueId, target.uniqueId)
        ReplyTo.setRepliedTo(target.uniqueId, sender.uniqueId)

        Chat.sendMessage(sender, "&7To: &f${target.displayName} &8- &7$message")
        Chat.sendMessage(target, "&7From: &f${sender.displayName} &8- &7$message")

        target.playSound(sender.location, Sound.NOTE_PLING, 10.toFloat(), 0.toFloat())
        return true
    }
}