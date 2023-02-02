package pink.mino.kraftwerk.features

import me.lucko.helper.utils.Log
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.events.TeamJoinEvent
import pink.mino.kraftwerk.events.TeamLeaveEvent


class TeamsFeature private constructor() : Listener {
    val sb: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private val teams = ArrayList<Team>()
    val colors = ArrayList<String>()
    var teamCount: Int = 0
    val teamMap = hashMapOf<Int, Set<OfflinePlayer>>()

    @EventHandler
    fun onTeamJoin(e: TeamJoinEvent) {
        val team = e.team.name.replace("UHC", "").toInt()
        teamMap[team] = e.team.players
    }

    @EventHandler
    fun onTeamLeave(e: TeamLeaveEvent) {
        val team = e.team.name.replace("UHC", "").toInt()
        teamMap[team] = e.team.players
    }

    fun getId(team: Team) : Int {
        return team.name.replace("UHC", "").toInt()
    }

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
    fun leaveTeam(player: OfflinePlayer) {
        if (getTeam(player) != null) {
            val team = getTeam(player)!!
            team.removePlayer(player)
            Bukkit.getPluginManager().callEvent(TeamLeaveEvent(team, player))
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
        Bukkit.getPluginManager().callEvent(TeamJoinEvent(team, player!!))
    }

    fun deleteTeam(team: Team) {
        Log.info("Automatically deleted ${team.name} because it was empty.")
        teams.remove(team)
        team.unregister()
        teamCount--
    }

    fun resetTeams() {
        for (team in teams) {

            team.unregister()
        }
        teamCount = 0
        teams.clear()
    }

    fun createTeam(player: Player? = null): Team {
        // Create a new team with name.
        teamCount++
        var name = "UHC${teamCount}"
        var team: Team? = null
        try {
            team = sb.registerNewTeam(name)
        } catch (e: Exception) {
            while (team == null) {
                teamCount++
                name = "UHC${teamCount}"
                try {
                    team = sb.registerNewTeam(name)
                } catch (e: Exception) {
                    continue
                }
            }
        }

        // Remove randomly selected color.
        if (colors.isEmpty()) setupColors()
        val color = colors.random()
        colors.remove(color)

        // Set up color & misc.
        team!!.prefix = color
        team.suffix = "§r"
        team.displayName = color + "Team #${teamCount}"
        if (SettingsFeature.instance.data!!.getBoolean("game.friendlyFire")) team.setAllowFriendlyFire(true)
        else team.setAllowFriendlyFire(false)
        team.setCanSeeFriendlyInvisibles(true)

        // Add player.
        if (player != null) {
            team.addPlayer(player)
            Bukkit.getPluginManager().callEvent(TeamJoinEvent(team, player))
        }

        // Add to list.
        teams.add(team)
        if (player != null) Log.info("Created ${team.name} for ${player.name}.")
        else Log.info("Created ${team.name}.")
        return team
    }

    /**
     * Gets the team of a player.
     * @param player the player wanting.
     * @return The team.
     */
    fun getTeam(player: Player): Team? {
        return player.scoreboard.getPlayerTeam(player)
    }

    fun getTeam(offlinePlayer: OfflinePlayer): Team? {
        return pink.mino.kraftwerk.utils.Scoreboard.sb.getPlayerTeam(offlinePlayer)
    }

    fun getTeam(team: String): Team? {
        for (t in teams) {
            if (t.name.lowercase() == team.lowercase()) return t
        }
        return null
    }

    fun getOfflineTeam(offlinePlayer: OfflinePlayer): Team? {
        for ((team, players) in teamMap.entries) {
            if (players.contains(offlinePlayer)) {
                return getTeam("UHC${team}")
            }
        }
        return null
    }

    /**
     * Sets up all the teams.
     */
    fun setupColors() {
        for (color in ChatColor.values()) {
            if (color == ChatColor.MAGIC) continue
            if (color == ChatColor.RESET) continue
            if (color == ChatColor.STRIKETHROUGH) continue
            if (color == ChatColor.UNDERLINE) continue
            if (color == ChatColor.BOLD) continue
            if (color == ChatColor.ITALIC) continue
            colors.add(color.toString())
        }
        colors.shuffle()
        val li = ArrayList<String>()
        for (color in colors) {
            li.add(color + ChatColor.BOLD)
            li.add(color + ChatColor.ITALIC)
            li.add(color + ChatColor.UNDERLINE)
            li.add(color + ChatColor.BOLD + ChatColor.ITALIC)
            li.add(color + ChatColor.ITALIC + ChatColor.UNDERLINE)
            li.add(color + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.UNDERLINE)
            li.add(color + ChatColor.BOLD + ChatColor.UNDERLINE)
        }
        li.shuffle()
        colors.addAll(li)
        Log.info("Setup teams colors.")
    }

    companion object {
        val manager = TeamsFeature()
    }
}