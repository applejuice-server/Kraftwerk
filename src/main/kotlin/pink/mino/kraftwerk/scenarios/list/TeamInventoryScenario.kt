package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
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