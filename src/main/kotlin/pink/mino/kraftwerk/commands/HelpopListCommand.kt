package pink.mino.kraftwerk.commands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HelpOp

class HelpopListCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        cmd: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.hl")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        Chat.sendMessage(sender, "&cUnanswered Help-Ops:")
        val count = HelpOp.getHelpops()
        if (count == 0) {
            Chat.sendMessage(sender, "&cThere are no unanswered Help-Ops.")
            return false
        }
        for (i in 1..count) {
            if (HelpOp.isHelpopAnswered(i)) {
                continue
            }
            if (sender is Player) {
                val text =
                    TextComponent(Chat.colored(" ${Chat.dash} &8[&c#${i}&8] &f${HelpOp.helpop[i]?.name} ${Chat.dash}&7 ${HelpOp.helpopContent[i]}"))
                text.clickEvent = ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND,
                    "/hr ${i} "
                )
                sender.spigot().sendMessage(text)
            } else {
                sender.sendMessage(Chat.colored(" ${Chat.dash} &8[&c#${i}&8] &f${HelpOp.helpop[i]!!.name} ${Chat.dash}&7 ${HelpOp.helpopContent[i]}"))
            }
        }
        return true
    }
}