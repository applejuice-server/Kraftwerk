package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.utils.Chat

class UnHelpopMuteCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player || !sender.hasPermission("uhc.staff.unhelpopmute")) {
            sender.sendMessage(Chat.colored("&cYou don't have permission to use this command."))
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(Chat.colored("&cUsage: /unhelpopmute <player>"))
            return true
        }

        val target = Bukkit.getOfflinePlayer(args[0])
        if (target.uniqueId == sender.uniqueId) {
            sender.sendMessage(Chat.colored("&cYou can't un-helpop-mute yourself."))
            return true
        }

        val activePunishment = PunishmentFeature.getActivePunishment(target, PunishmentType.HELPOP_MUTE)
        if (activePunishment == null) {
            sender.sendMessage(Chat.colored("&cThat player is not currently help-op muted."))
            return true
        }

        PunishmentFeature.revokePunishment(target.uniqueId, PunishmentType.HELPOP_MUTE)

        val message = Chat.colored("&c${sender.name} un-helpop muted ${target.name}.")
        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission("uhc.staff") }
            .forEach { it.sendMessage(message) }

        return true
    }
}
