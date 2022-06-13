package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.GameState

/**
 * @author mrcsm
 * 2022-06-13
 */
class PMMOresCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (sender.scoreboard.getPlayerTeam(sender) == null) {
            sender.sendMessage("${ChatColor.RED}You must be on a team to send a message.")
            return true
        }

        if (GameState.currentState != GameState.INGAME) {
            sender.sendMessage("${ChatColor.RED}You can only use this command during a game.")
            return true
        }

        if (sender.scoreboard.getPlayerTeam(sender) != null) {

            for (team in sender.scoreboard.getPlayerTeam(sender).players) {
                if (team is Player) {
                    team.sendMessage("§8[§4Team Chat§8] ${ChatColor.WHITE}${sender.name} §7has mined §6${if (SpecFeature.instance.goldMined[sender.uniqueId] == null) "0" else "${SpecFeature.instance.goldMined[sender.uniqueId]}"} gold§7, and §b${if (SpecFeature.instance.diamondsMined[sender.uniqueId] == null) "0" else "${SpecFeature.instance.diamondsMined[sender.uniqueId]}"} diamonds§7.")
                }
            }
        }
        return true
    }
}