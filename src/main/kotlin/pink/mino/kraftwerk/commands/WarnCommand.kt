package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.Punishment
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.utils.Chat
import java.util.*

class WarnCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("uhc.staff.warn")) {
            sender.sendMessage(Chat.colored("&cYou don't have permission to do that."))
            return true
        }

        if (args.size < 2) {
            sender.sendMessage(Chat.colored("&cUsage: /warn <player> [-s] <reason>"))
            return true
        }

        val target: OfflinePlayer = Bukkit.getOfflinePlayer(args[0])
        if (!target.hasPlayedBefore() && !target.isOnline) {
            sender.sendMessage(Chat.colored("&cPlayer '${args[0]}' not found."))
            return true
        }
        if (target == sender) {
            Chat.sendMessage(sender, "&cYou cannot punish yourself.")
            return true
        }

        if ((target as Player).hasPermission("uhc.staff")) {
            Chat.sendMessage(sender, "&cYou cannot punish another staff member.")
            return true
        }

        var index = 1
        var silent = false

        if (args[index].equals("-s", ignoreCase = true)) {
            silent = true
            index++
        }

        if (args.size <= index) {
            sender.sendMessage(Chat.colored("&cUsage: /warn <player> [-s] <reason>"))
            return true
        }

        val reason = args.copyOfRange(index, args.size).joinToString(" ")

        val punishment = Punishment(
            uuid = target.uniqueId,
            punisherUuid = if (sender is Player) sender.uniqueId else UUID(0, 0),
            type = PunishmentType.WARN,
            expiresAt = System.currentTimeMillis() + 5 * 60 * 1000, // warn expires in 5 minutes, arbitrary but not permanent
            reason = reason,
            silent = silent,
            punishedAt = System.currentTimeMillis(),
            revoked = false
        )

        PunishmentFeature.punish(target, punishment)

        val message = Chat.colored("&e${target.name} has been warned. Reason: $reason")

        if (!silent) {
            Bukkit.broadcast(message, "uhc.staff")
        } else {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("uhc.staff")) {
                    player.sendMessage(Chat.colored("&7[Silent] $message"))
                }
            }
        }

        return true
    }
}