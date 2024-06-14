package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

/**
 * @author mrcsm
 * 2022-06-13
 */
class PMOresCommand : CommandExecutor {

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

            var iron = 0
            var gold = 0
            var diamond = 0

            for (item in sender.inventory.contents) {
                if (item == null) continue
                when (item.type) {
                    Material.IRON_ORE, Material.IRON_INGOT -> iron++
                    Material.GOLD_ORE, Material.GOLD_INGOT -> gold++
                    Material.DIAMOND_ORE, Material.DIAMOND -> diamond++
                    else -> continue
                }
            }

            for (team in sender.scoreboard.getPlayerTeam(sender).players) {
                if (team is Player) {
                    team.sendMessage(Chat.colored("§8[${Chat.primaryColor}Team Chat§8] ${ChatColor.WHITE}${sender.name} §7has ${iron} iron, §6${gold} gold§7, and §b${diamond} diamonds§7."))
                }
            }
        }
        return true
    }
}