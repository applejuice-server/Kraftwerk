package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class FlyCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("uhc.staff.fly")) {
            Chat.sendMessage(sender as Player, "&cYou do not have permission to use this command.")
            return false
        }
        if (args.isEmpty()) {
            val player = sender as Player
            return if (!player.allowFlight) {
                player.allowFlight = true
                player.isFlying = true
                Chat.sendMessage(player, "&7You have &aenabled&7 flight for yourself.")
                true
            } else {
                player.isFlying = false
                player.allowFlight = false
                Chat.sendMessage(player, "&7You have &cdisabled&7 flight for yourself.")
                true
            }

        } else {
            if (!sender.hasPermission("uhc.admin.fly")) {
                Chat.sendMessage(sender as Player, "&cYou do not have permission to set flight to other players.")
                return false
            }
            val target = Bukkit.getServer().getPlayer(args[0])
            if (target == null) {
                Chat.sendMessage(sender as Player, "&cThat player is not online or has never logged onto the server.")
                return false
            }
            return if (!target.allowFlight) {
                target.allowFlight = true
                target.isFlying = true
                Chat.sendMessage(target, "&7Your flight has been enabled by &c${sender.name}&7.")
                Chat.sendMessage(sender as Player, "&7Enabled &c${target.name}'s&7 flight.")
                true
            } else {
                target.allowFlight = false
                target.isFlying = false
                Chat.sendMessage(target, "&7Your flight has been disabled by &c${sender.name}&7.")
                Chat.sendMessage(sender as Player, "&7Disabled &c${target.name}'s&7 flight.")
                true
            }
        }
    }
}