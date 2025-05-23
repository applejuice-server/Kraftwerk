package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.utils.Chat

class UnmuteCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player || !sender.hasPermission("uhc.staff.unmute")) {
            sender.sendMessage(Chat.colored("&cYou don't have permission to use this command."))
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(Chat.colored("&cUsage: /unmute <player>"))
            return true
        }

        val target = Bukkit.getOfflinePlayer(args[0])
        if (target.uniqueId == sender.uniqueId) {
            sender.sendMessage(Chat.colored("&cYou can't unmute yourself."))
            return true
        }

        val activePunishment = PunishmentFeature.getActivePunishment(target, PunishmentType.MUTE)
        if (activePunishment == null) {
            sender.sendMessage(Chat.colored("&cThat player is not currently muted."))
            return true
        }

        PunishmentFeature.revokePunishment(target.uniqueId, PunishmentType.MUTE)

        val message = Chat.colored("&c${sender.name} unmuted ${target.name}.")
        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission("uhc.staff") }
            .forEach { it.sendMessage(message) }

        return true
    }
}
