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

class KickCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission("uhc.staff.kick")) {
            sender.sendMessage(Chat.colored("&cYou do not have permission to use this command."))
            return true
        }

        if (args == null || args.size < 2) {
            sender.sendMessage(Chat.colored("&cUsage: /kick <player> [-s] <reason...>"))
            return true
        }

        val targetName = args[0]
        val target: OfflinePlayer = Bukkit.getOfflinePlayer(targetName)
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

        var hasSilentFlag = false
        var reasonStartIndex = 1

        if (args[1].equals("-s", ignoreCase = true)) {
            hasSilentFlag = true
            reasonStartIndex = 2
        }

        if (args.size <= reasonStartIndex) {
            sender.sendMessage(Chat.colored("&cMissing reason for the kick."))
            return true
        }

        val reason = args.copyOfRange(reasonStartIndex, args.size).joinToString(" ")

        val punishment = Punishment(
            uuid = target.uniqueId,
            punisherUuid = if (sender is Player) sender.uniqueId else UUID.randomUUID(),
            type = PunishmentType.KICK,
            expiresAt = System.currentTimeMillis(), // immediate
            reason = reason,
            silent = hasSilentFlag,
            punishedAt = System.currentTimeMillis(),
            revoked = false
        )

        PunishmentFeature.punish(target, punishment)

        // Notify appropriately
        if (!hasSilentFlag) {
            Bukkit.broadcastMessage(Chat.colored("&c${target.name} has been kicked. Reason: $reason"))
        } else {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("uhc.staff")) {
                    player.sendMessage(Chat.colored("&7[Silent] &c${target.name} has been kicked. Reason: $reason"))
                }
            }
        }

        return true
    }
}