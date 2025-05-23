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

class MuteCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("uhc.staff.mute")) {
            sender.sendMessage(Chat.colored("&cYou don't have permission to do that."))
            return true
        }

        if (args.size < 3) {
            sender.sendMessage(Chat.colored("&cUsage: /mute <player> [-s] <duration> <reason>"))
            return true
        }

        val target: OfflinePlayer = Bukkit.getOfflinePlayer(args[0])
        val targetPlayerOnline = Bukkit.getPlayer(target.uniqueId)

        if (targetPlayerOnline != null) { // Target is online
            if (targetPlayerOnline == sender) {
                Chat.sendMessage(sender, "&cYou cannot punish yourself.")
                return true
            }

            if (targetPlayerOnline.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "&cYou cannot punish another staff member who is currently online.")
                return true
            }
        }

        var index = 1
        var silent = false

        if (args[index].equals("-s", ignoreCase = true)) {
            silent = true
            index++
        }

        if (args.size <= index + 1) {
            sender.sendMessage(Chat.colored("&cUsage: /mute <player> [-s] <duration> <reason>"))
            return true
        }

        val durationStr = args[index]
        val durationMillis = PunishmentFeature.parseDurationToMillis(durationStr)
        if (durationMillis == null) {
            sender.sendMessage(Chat.colored("&cInvalid duration '$durationStr'. Use formats like 10m, 1h, 2d."))
            return true
        }

        val reason = args.copyOfRange(index + 1, args.size).joinToString(" ")

        val punishment = Punishment(
            uuid = target.uniqueId,
            punisherUuid = if (sender is Player) sender.uniqueId else UUID(0, 0),
            type = PunishmentType.MUTE,
            expiresAt = System.currentTimeMillis() + durationMillis,
            reason = reason,
            silent = silent,
            punishedAt = System.currentTimeMillis(),
            revoked = false
        )

        PunishmentFeature.punish(target, punishment)

        val message = Chat.colored("&c${target.name} has been muted for ${PunishmentFeature.timeToString(durationMillis)}. Reason: $reason")

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