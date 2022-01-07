package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class Teams private constructor() {
    private val sb: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private val teams = ArrayList<Team>()

    /**
     * Gets a list of all teams.
     * @return the list of teams.
     */
    fun getTeams(): List<Team> {
        return teams
    }

    /**
     * Leaves the current team of the player.
     * @param player the player leaving.
     */
    fun leaveTeam(player: Player) {
        if (getTeam(player) != null) {
            getTeam(player)!!.removePlayer(player)
        }
    }

    /**
     * Joins a team.
     * @param teamName the team joining.
     * @param player the player joining.
     */
    fun joinTeam(teamName: String?, player: Player?) {
        val team: Team = sb.getTeam(teamName)
        team.addPlayer(player)
    }

    /**
     * Gets the team of a player.
     * @param player the player wanting.
     * @return The team.
     */
    private fun getTeam(player: Player): Team? {
        return player.scoreboard.getPlayerTeam(player)
    }

    /**
     * Sets up all the teams.
     */
    fun setupTeams() {
        val list = ArrayList<String>()
        for (color in ChatColor.values()) {
            if (color === ChatColor.RESET || color === ChatColor.MAGIC || color === ChatColor.BOLD || color === ChatColor.ITALIC || color === ChatColor.UNDERLINE || color === ChatColor.STRIKETHROUGH) {
                continue
            }
            list.add(color.toString())
        }
        val list2 = ArrayList<String>()
        for (li in list) {
            list2.add(li + ChatColor.BOLD)
            list2.add(li + ChatColor.ITALIC)
            list2.add(li + ChatColor.UNDERLINE)
            list2.add(li + ChatColor.BOLD + ChatColor.ITALIC)
            list2.add(li + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.UNDERLINE)
            list2.add(li + ChatColor.BOLD + ChatColor.UNDERLINE)
            list2.add(li + ChatColor.ITALIC + ChatColor.UNDERLINE)
        }
        list.remove(ChatColor.WHITE.toString())
        list.remove(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString())
        list.addAll(list2)
        val spec: Team = if (sb.getTeam("spec") == null) sb.registerNewTeam("spec") else sb.getTeam("spec")
        spec.displayName = "spec"
        spec.prefix = "§7§o"
        spec.suffix = "§r"
        spec.setAllowFriendlyFire(false)
        spec.setCanSeeFriendlyInvisibles(true)
        for (i in list.indices) {
            val team: Team = if (sb.getTeam("UHC" + (i + 1)) == null) sb.registerNewTeam("UHC" + (i + 1)) else sb.getTeam("UHC" + (i + 1))
            team.displayName = "UHC" + (i + 1)
            team.prefix = list[i]
            team.suffix = "§r"
            team.setAllowFriendlyFire(true)
            team.setCanSeeFriendlyInvisibles(true)
            teams.add(team)
        }
    }

    companion object {
        val manager = Teams()
    }
}