package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.Inventory
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*


class TeamInventoryScenario : Scenario(
  "Team Inventory",
  "Each team gets their own shared inventory.",
  "teaminventory",
  Material.ENDER_CHEST
) {
    var teamInventories: HashMap<Team, Inventory>? = null
    var soloInventories: HashMap<UUID, Inventory>? = null

    companion object {
        val instance = TeamInventoryScenario()
    }

    fun aliveTeammates(player: Player): ArrayList<Player> {
        val teammates = ArrayList<Player>()
        val team = TeamsFeature.manager.getTeam(player)
        if (team != null) {
            for (member in team.entries) {
                val p = Bukkit.getPlayer(member)
                if (p != null && !SpecFeature.instance.isSpec(p) && SettingsFeature.instance.data!!.getString("game.list").contains(p.name)) {
                    teammates.add(p)
                }
            }
        }
        return teammates
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val player = e.entity
        val team = TeamsFeature.manager.getTeam(player)
        if (team != null) {
            if (aliveTeammates(player).size == 0) {
                for (item in teamInventories!![team]!!.contents) {
                    e.drops.add(item)
                }
                teamInventories!![team]!!.clear()
                Chat.sendMessage(e.entity.killer, "${Chat.dash} The contents of their &f&oTeam Inventory&7 have been dropped as well.")
            }
        } else {
            if (soloInventories!![player.uniqueId] != null) {
                for (item in soloInventories!![player.uniqueId]!!.contents) {
                    e.drops.add(item)
                }
                soloInventories!![player.uniqueId]!!.clear()
                Chat.sendMessage(e.entity.killer, "${Chat.dash} The contents of their &f&oTeam Inventory&7 have been dropped as well.")
            }
        }
    }

    override fun onStart() {
        if (!enabled) return
        if (teamInventories == null) {
            teamInventories = HashMap()
        }

        if (soloInventories == null) {
            soloInventories = HashMap()
        }

        if (teamInventories!!.size != 0) teamInventories!!.clear()
        if (soloInventories!!.size != 0) soloInventories!!.clear()

        for (team in TeamsFeature.manager.getTeams()) {
            teamInventories!![team] = Bukkit.createInventory(null, 27, "${team.prefix}${team.name}'s Inventory")
        }
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                if (TeamsFeature.manager.getTeam(player) == null) {
                    soloInventories!![player.uniqueId] = Bukkit.createInventory(null, 27, "${player.name}'s Inventory")
                }
            }
        }
    }
}