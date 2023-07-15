package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class EmotesCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Nope!")
            return false
        }
        Chat.sendMessage(sender, "&l&6Emotes:")
        Chat.sendMessage(sender, " &8- &e:shrug: ${Chat.dash} &e¯\\_(ツ)_/¯&r")
        Chat.sendMessage(sender, " &8- &e:yes: ${Chat.dash} &l&a✔&r")
        Chat.sendMessage(sender, " &8- &e:no: ${Chat.dash} &l&c✖&r")
        Chat.sendMessage(sender, " &8- &e123 ${Chat.dash} &a1&e2&c3&r")
        Chat.sendMessage(sender, " &8- &e<3 ${Chat.dash} &c❤&r")
        Chat.sendMessage(sender, " &8- &eo/ ${Chat.dash} &d(・∀・)ノ&r")
        Chat.sendMessage(sender, " &8- &e:star: ${Chat.dash} &e✰&r")
        Chat.sendMessage(sender, " &8- &e:100: ${Chat.dash} &c&o&l&n100&r")
        Chat.sendMessage(sender, " &8- &eo7 ${Chat.dash} &e(｀-´)>&r")
        Chat.sendMessage(sender, " &8- &e:blush: ${Chat.dash} &d(◡‿◡✿)&r")
        return true
    }
}