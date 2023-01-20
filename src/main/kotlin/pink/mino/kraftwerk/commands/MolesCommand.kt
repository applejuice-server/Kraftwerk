package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class MolesCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            Chat.sendMessage(sender, "You probably shouldn't use this command as you aren't a player.")
            return false
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles"))) {
            Chat.sendMessage(sender, "${Chat.prefix} &cMoles&7 isn't enabled!")
            return false
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "${Chat.prefix} &cMoles&7 isn't available right now!")
            return false
        }
        if (MolesScenario.instance.moles[sender.uniqueId] == null) {
            Chat.sendMessage(sender, "${Chat.prefix} &7You aren't a mole!")
            return false
        }
        Chat.sendMessage(sender, Chat.line)
        Chat.sendCenteredMessage(sender, "&c&lMoles Help")
        Chat.sendMessage(sender, "${Chat.prefix} &f/molekit [kit] &8-&7 Chooses a mole kit.")
        Chat.sendMessage(sender, "${Chat.prefix} &f/mcl &8-&7 Sends your location out to other moles.")
        Chat.sendMessage(sender, "${Chat.prefix} &f/mcc <message> &8-&7 Message other moles.")
        Chat.sendMessage(sender, "${Chat.prefix} &f/mcp &8-&7 View the list of other moles.")
        Chat.sendMessage(sender, Chat.line)
        return true
    }
}