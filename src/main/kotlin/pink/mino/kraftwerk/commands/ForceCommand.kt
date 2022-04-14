package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class ForceCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.force")) {
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) {
            Chat.sendMessage(sender, "&cThere is no game running at the moment.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: &f/force <start/fh/pvp/meetup>&7.")
            return false
        }
        if (args[0] == "start") {
            JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.timer = 45
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${sender.name}&7 has forced &eGame Start&7."))
        } else if (args[0] == "fh") {
            JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.timer = JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.finalHeal
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${sender.name}&7 has forced &eFinal Heal&7."))
        } else if (args[0] == "pvp") {
            JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.timer = JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.pvp
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${sender.name}&7 has forced &ePvP&7."))
        } else if (args[0] == "meetup") {
            JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.timer = JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.meetup
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${sender.name}&7 has forced &eMeetup&7."))
        } else {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: &f/force <start/fh/pvp/meetup>&7.")
            return false
        }
        return true
    }
}