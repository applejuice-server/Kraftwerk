package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class ClearPotionEffectsCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("uhc.staff.ce")) {
            Chat.sendMessage(sender as Player, "&cYou do not have permission to use this command.")
            return false
        }
        if (args.isEmpty()) {
            val player = sender as Player
            val effects = player.activePotionEffects
            for (effect in effects) {
                player.removePotionEffect(effect.type)
            }
            Chat.sendMessage(sender, "Your effects have been cleared.")
            return true
        } else {
            if (args[0] == "*") {
                for (online in ArrayList(Bukkit.getServer().onlinePlayers)) {
                    val effects = online.activePotionEffects
                    for (effect in effects) {
                        online.removePotionEffect(effect.type)
                    }
                    Chat.sendMessage(online, "&7Your effects have been cleared by &c${sender.name}&7.")
                }
                Chat.sendMessage(sender as Player, "&7Cleared the effects of all players.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player, "&cThat player is not online or has never logged onto the server.")
                    return false
                }
                val effects = target.activePotionEffects
                for (effect in effects) {
                    target.removePotionEffect(effect.type)
                }
                Chat.sendMessage(sender as Player, "&7Cleared the effects of &c${target.name}&7.")
                Chat.sendMessage(target, "&7Your effects have been cleared by &c${sender.name}&7.")
                return true
            }
        }
    }
}