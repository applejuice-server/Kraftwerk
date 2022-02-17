package pink.mino.kraftwerk.utils

import org.bukkit.entity.Player

class HelpOp {
    companion object {
        val helpop: HashMap<Int, Player> = HashMap()
        var count: Int = 0

        fun addHelpop(player: Player): Int {
            helpop[count] = player
            count++
            return count
        }

        fun getHelpop(int: Int): Player {
            return helpop[int]!!
        }
    }

}