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
    companion object {
        val instance = MolesScenario()
    }

    val prefix = "&8[&4Moles&8]&7"
    val moles: HashMap<UUID, Boolean> = HashMap()

    fun sendMoles(message: String) {
        for (mole in moles) {
            val player = Bukkit.getOfflinePlayer(mole.key)
            if (player.isOnline) {
                Chat.sendMessage(player as Player, "$prefix $message")
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

    fun assignMoles() = try {
        for (team in TeamsFeature.manager.getTeams()) {
            if (team.size != 0) {
                val list = ArrayList<Player>()
                for (teammate in team.players) {
                    if (teammate.isOnline) list.add(teammate as Player)
                }
                val teammateIndex = Random.nextInt(list.size)
                for ((index, teammate) in list.withIndex()) {
                    if (index == teammateIndex) {
                        moles[teammate.uniqueId] = false
                        Chat.sendMessage(teammate, "$prefix You are the &fmole&7! Use &f/mole help&7 to see mole commands.")
                    }
                }
            }
        }
    } catch (e: Error) { print(e) }

    override fun onPvP() {
        assignMoles()
        Bukkit.broadcastMessage(Chat.colored("$prefix Moles have been assigned!"))
    }
}