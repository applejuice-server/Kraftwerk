package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.events.TeamJoinEvent
import pink.mino.kraftwerk.events.TeamLeaveEvent
import pink.mino.kraftwerk.features.SpecFeature
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
    companion object {
        val instance = MolesScenario()
    }

    val prefix = "&8[${Chat.primaryColor}Moles&8]&7"
    val moles: HashMap<UUID, Boolean> = HashMap()
    var moleTeam: Team? = null

    fun sendMoles(message: String) {
        for (mole in moles) {
            val player = Bukkit.getOfflinePlayer(mole.key)
            if (player.isOnline) {
                Chat.sendMessage(player as Player, "$prefix $message")
            }
        }
        for (spec in SpecFeature.instance.getSpecs()) {
            val specPlayer = Bukkit.getPlayer(spec)
            if (specPlayer != null) {
                Chat.sendMessage(specPlayer, "$prefix $message")
            }
        }
    }

    fun getMoles(): ArrayList<String> {
        val list: ArrayList<String> = ArrayList<String>()
        for (mole in moles) {
            val player = Bukkit.getOfflinePlayer(mole.key)
            if (player.isOnline) {
                list.add(Chat.colored("&a${player.name}&7"))
            } else {
                list.add(Chat.colored("&c${player.name}&7"))
            }
        }
        return list
    }

    fun assignMoles() {
        moleTeam = TeamsFeature.manager.createTeam()
        for (team in TeamsFeature.manager.getTeams()) {
            if (team.size != 0) {
                val list = ArrayList<Player>()
                for (teammate in team.players) {
                    if (teammate.isOnline) {
                        list.add(teammate as Player)
                        Bukkit.getPluginManager().callEvent(TeamLeaveEvent(team, teammate))
                        Bukkit.getPluginManager().callEvent(TeamJoinEvent(moleTeam!!, teammate))
                    }
                }
                if (list.size == 0) continue
                val teammateIndex = Random.nextInt(list.size)
                for ((index, teammate) in list.withIndex()) {
                    if (index == teammateIndex) {
                        moles[teammate.uniqueId] = false
                        Chat.sendMessage(teammate, "$prefix You are the ${Chat.secondaryColor}mole&7! Use ${Chat.secondaryColor}/mole help&7 to see mole commands.")
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