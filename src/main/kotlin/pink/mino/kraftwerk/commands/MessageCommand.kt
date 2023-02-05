package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ReplyTo

class MessageCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "&cYou need a user to send a message to.")
            return false
        }
        if (args.size < 2) {
            Chat.sendMessage(sender, "&cYou need a message to send to the user.")
            return false
        }

        var message = ""
        for (i in 1 until args.size) message += args[i] + " "
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            Chat.sendMessage(sender,"&cYou need a valid user to send this to.")
            return false
        }
        val player = sender
        val list = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(target.uniqueId)!!.ignored
        if (list.contains(player.uniqueId)) {
            Chat.sendMessage(sender, "&cThis person has you on their ignore list.")
            return false
        }
        Chat.sendMessage(sender, "&7To: &f${target.displayName} &8- &7$message")
        Chat.sendMessage(target, "&7From: &f${player.displayName} &8- &7$message")

        ReplyTo.setRepliedTo(player.uniqueId, target.uniqueId)
        ReplyTo.setRepliedTo(target.uniqueId, player.uniqueId)

        target.playSound(target.location, Sound.NOTE_PLING, 10.toFloat(), 0.toFloat())
        return true
    }

}