package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import pink.mino.kraftwerk.events.TeamJoinEvent
import pink.mino.kraftwerk.events.TeamLeaveEvent
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class PotentialMolesScenario : Scenario(
    "Potential Moles",
    "At PvP, a random teammate has a random chance to be assigned as a mole, moles must kill their own teammates.",
    "potential_moles",
    Material.IRON_SPADE
) {
    val prefix = "&8[&cPotential Moles&8]&7"

    fun assignMoles() {
        MolesScenario.instance.moleTeam = TeamsFeature.manager.createTeam()
        for (team in TeamsFeature.manager.getTeams()) {
            val random = Random.nextInt(0, 2)
            if (random <= 1) {
                if (team.size != 0) {
                    val list = ArrayList<Player>()
                    for (teammate in team.players) {
                        if (teammate.isOnline) {
                            list.add(teammate as Player)
                            Bukkit.getPluginManager().callEvent(TeamLeaveEvent(team, teammate))
                            Bukkit.getPluginManager().callEvent(TeamJoinEvent(MolesScenario.instance.moleTeam!!, teammate))
                        }
                    }
                    if (list.size == 0) continue
                    val teammateIndex = Random.nextInt(list.size)
                    for ((index, teammate) in list.withIndex()) {
                        if (index == teammateIndex) {
                            MolesScenario.instance.moles[teammate.uniqueId] = false
                            Chat.sendMessage(teammate, "$prefix You are the &fmole&7! Use &f/mole help&7 to see mole commands.")
                        }
                    }
                }
            }
        }
    }

    override fun onPvP() {
        assignMoles()
        Bukkit.broadcastMessage(Chat.colored("$prefix Moles have been assigned!"))
    }
}