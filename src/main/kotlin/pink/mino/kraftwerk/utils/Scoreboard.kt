package pink.mino.kraftwerk.utils

import org.bukkit.Bukkit
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard


class Scoreboard {
    companion object {
        var sb: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        var kills: Objective? = sb.getObjective("killboard")

        fun getScore(string: String): Int {
            return kills!!.getScore(string).score
        }

        fun setScore(key: String, value: Int) {
            val score = kills!!.getScore(key)
            score.score = value
        }

        fun deleteScore(key: String) {
            sb.resetScores(key)
        }

        fun setup() {
            if (sb.getObjective("killboard") == null) {
                kills = sb.registerNewObjective("killboard", "dummy")
            }
            kills!!.displayName = Chat.colored("&capple&ajuice")
            kills!!.displaySlot = DisplaySlot.SIDEBAR
        }
    }
}