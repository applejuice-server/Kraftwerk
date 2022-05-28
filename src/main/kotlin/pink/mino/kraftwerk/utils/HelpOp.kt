package pink.mino.kraftwerk.utils

import org.bukkit.entity.Player

class HelpOp {
    companion object {
        val helpop: HashMap<Int, Player> = HashMap()
        val answeredHelpop: HashMap<Int, Boolean> = HashMap()
        val helpopContent: HashMap<Int, String> = HashMap()
        var count: Int = 0

        fun addHelpop(player: Player, content: String): Int {
            count++
            helpop[count] = player
            answeredHelpop[count] = false
            helpopContent[count] = content
            return count
        }

        fun getHelpop(int: Int): Player? {
            return helpop[int]
        }

        fun isHelpopAnswered(int: Int): Boolean {
            if (answeredHelpop.contains(int)) {
                return answeredHelpop[int]!!
            }
            return false
        }

        fun getHelpops(): Int {
            return count
        }

        fun answered(int: Int) {
            answeredHelpop[int] = true
        }
    }

}