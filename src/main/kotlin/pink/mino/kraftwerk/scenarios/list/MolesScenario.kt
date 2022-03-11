package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import java.util.*
import kotlin.random.Random

class MolesScenario : Scenario(
    "Moles",
    "At PvP, a random teammate is assigned as a mole, moles must kill their own teammates.",
    "moles",
    Material.STONE_SPADE
) {
    val moles: HashMap<UUID, Boolean> = HashMap()
    fun assignMoles() {
        for (team in TeamsFeature.manager.getTeams()) {
            if (team.size != 0) {
                val teammateIndex = Random.nextInt(team.players.size)
                for ((index, teammate) in team.players.withIndex()) {
                    if (index == teammateIndex) {
                        if (teammate.isOnline) {
                            moles[teammate.uniqueId] = false
                            Chat.sendMessage(teammate as Player, "${Chat.prefix} You are the &fmole&7! Use &f/mole help&7 to see mole commands.")
                        } else {
                            continue
                        }
                    }
                }
            }
        }
    }

    override fun onPvP() {
        assignMoles()
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Moles have been assigned!"))
    }
}