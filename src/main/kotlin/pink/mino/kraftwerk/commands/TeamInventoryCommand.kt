package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.TeamInventoryScenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class TeamInventoryCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            Chat.sendMessage(sender, "You can't use this command as you technically aren't a player.")
            return false
        }
        if (!ScenarioHandler.getScenario("teaminventory")!!.enabled) {
            Chat.sendMessage(sender, "${Chat.prefix} ${Chat.primaryColor}Team Inventory&7 isn't enabled at the moment.")
            return false
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "${Chat.prefix} ${Chat.primaryColor}Team Inventory&7 cannot be used at the moment.")
            return false
        }

        val team = TeamsFeature.manager.getTeam(sender)
        if (team == null) { // Solo
            if (TeamInventoryScenario.instance.soloInventories!!.containsKey(sender.uniqueId)) {
                Chat.sendMessage(sender, "${Chat.prefix} Opening your team inventory...")
                sender.openInventory(TeamInventoryScenario.instance.soloInventories!![sender.uniqueId])
                return true
            }
            TeamInventoryScenario.instance.soloInventories!![sender.uniqueId] = Bukkit.createInventory(null, 27, "${sender.name}'s Inventory")
            Chat.sendMessage(sender, "${Chat.prefix} Opening your team inventory...")
            sender.openInventory(TeamInventoryScenario.instance.soloInventories!![sender.uniqueId])
            return true
        } else { // In a team
            if (TeamInventoryScenario.instance.teamInventories!!.containsKey(team)) {
                Chat.sendMessage(sender, "${Chat.prefix} Opening your team inventory...")
                sender.openInventory(TeamInventoryScenario.instance.teamInventories!![team])
                return true
            }

            TeamInventoryScenario.instance.teamInventories!![team] = Bukkit.createInventory(null, 27, "${team.prefix}${team.name}'s Inventory")
            Chat.sendMessage(sender, "${Chat.prefix} Opening your team inventory...")
            sender.openInventory(TeamInventoryScenario.instance.soloInventories!![sender.uniqueId])
            return true
        }
    }

}