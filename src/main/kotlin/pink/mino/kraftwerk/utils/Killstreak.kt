package pink.mino.kraftwerk.utils

import org.bukkit.entity.Player

class Killstreak {
    companion object {
        private val killstreaks: HashMap<Player, Int> = HashMap()

        fun addKillstreak(player: Player): Int {
            if (killstreaks[player] == null) killstreaks[player] = 0
            killstreaks[player] = killstreaks[player]!! + 1
            return killstreaks[player]!!
        }

        fun resetKillstreak(player: Player): Int {
            killstreaks[player] = 0
            return 0
        }

        fun getKillstreak(player: Player): Int {
            if (killstreaks[player] == null) killstreaks[player] = 0
            return killstreaks[player]!!
        }
    }
}