package pink.mino.kraftwerk.commands


import com.lunarclient.bukkitapi.LunarClientAPI
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer


class SendTeamView(val team: Team) : BukkitRunnable() {
    override fun run() {
        if (team.size == 0) {
            cancel()
        }
        val list: ArrayList<Player> = arrayListOf()
        for (player in team.players) {
            if (player.isOnline) {
                list.add(player as Player)
            }
        }
        for (player in team.players) {
            if (player.isOnline) {
                if (LunarClientAPI.getInstance().isRunningLunarClient(player as Player)) {
                    TeamCommand().addTeamInfo(player, list)
                }
            }
        }
    }
}

class TeamCommand : CommandExecutor {

    private var invites = HashMap<Player, ArrayList<Player>>()
    private val settings: SettingsFeature = SettingsFeature.instance

    private fun <T> splitList(list: List<T>, size: Int): List<List<T>>? {
        val iterator = list.iterator()
        val returnList: MutableList<List<T>> = ArrayList()
        while (iterator.hasNext()) {
            val tempList: MutableList<T> = ArrayList()
            for (i in 0 until size) {
                if (!iterator.hasNext()) break
                tempList.add(iterator.next())
            }
            returnList.add(tempList)
        }
        return returnList
    }

    fun addTeamInfo(player: Player, playersToAdd: List<Player>) {
        val map: MutableMap<UUID, Map<String, Double>> = ConcurrentHashMap()
        playersToAdd.forEach(Consumer { members: Player ->
            val position: MutableMap<String, Double> =
                HashMap()
            if (members.player == null) return@Consumer
            position["x"] = members.player.location.x
            position["y"] = members.player.location.y
            position["z"] = members.player.location.z
            map[members.uniqueId] = position
        })
        val teammates = LCPacketTeammates(player.uniqueId, 0L, map)
        LunarClientAPI.getInstance().sendTeammates(player, teammates)
    }

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {

        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.team")) {
                if (SettingsFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You can't use this command at the moment. (It's an FFA game or Random teams)")
                    return false
                } else if (GameState.valueOf(SettingsFeature.instance.data!!.getString("game.state")) != GameState.LOBBY) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You can't use this command at the moment.")
                    return false
                }
            }
        }

        if (args.isEmpty()) {
            Chat.sendMessage(sender as Player, Chat.line)
            Chat.sendMessage(sender, "${Chat.prefix} &f/team create ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Creates a team.")
            Chat.sendMessage(sender, "${Chat.prefix} &f/team invite <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Invites a player to your team.")
            Chat.sendMessage(sender, "${Chat.prefix} &f/team leave ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Leave your team.")
            Chat.sendMessage(sender, "${Chat.prefix} &f/team accept <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Accept a player's team invite.")
            Chat.sendMessage(sender, "${Chat.prefix} &f/team list ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Brings a list of teams and their members.")
            Chat.sendMessage(sender, "${Chat.prefix} &f/pm <message> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Talk in team chat.")
            Chat.sendMessage(sender, "${Chat.prefix} &f/pmc ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Send your coordinates.")
            Chat.sendMessage(sender, Chat.line)
            if (sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(sender, "${Chat.prefix} &f/team reset ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Reset all teams.")
                Chat.sendMessage(sender, "${Chat.prefix} &f/team management <on/off> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Enable/disable team management.")
                Chat.sendMessage(sender, "${Chat.prefix} &f/team size <size> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Set the size of teams.")
                Chat.sendMessage(sender, "${Chat.prefix} &f/team set <player1> <player2> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Sets Player 1 to the Player 2's team.")
                Chat.sendMessage(sender, "${Chat.prefix} &f/team delete <team name> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Deletes the provided team.")
                Chat.sendMessage(sender, "${Chat.prefix} &f/team friendlyfire <on/off> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Toggles friendly fire.")
                Chat.sendMessage(sender, "${Chat.prefix} &f/team kickunder <number> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Kicks all solos/teams under a certain threshold.")
                Chat.sendMessage(sender, "${Chat.prefix} &f/team randomize ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Randomizes all players that aren't Spectators into a team.")
                Chat.sendMessage(sender, Chat.line)
            }
        } else if (args[0] == "create") {
            val player = sender as Player
            if (player.scoreboard.getPlayerTeam(player) != null) {
                Chat.sendMessage(player, "&cYou are already on a team.")
                return true
            }
            if (SettingsFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                Chat.sendMessage(player, "&cYou can't use this command at the moment.")
                return true
            }
            if (GameState.valueOf(SettingsFeature.instance.data!!.getString("game.state")) != GameState.LOBBY) {
                Chat.sendMessage(player, "&cYou can't use this command at the moment.")
                return true
            }

            for (team in TeamsFeature.manager.getTeams()) {
                if (team.size == 0) {
                    team.addPlayer(player)
                    SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                    Chat.sendMessage(player, "${Chat.prefix} Team created! Use ${ChatColor.WHITE}/team invite <player>${ChatColor.GRAY} to invite a player.")
                    return true
                }
            }
        } else if (args[0] == "invite") {
            val player = sender as Player
            val oPlayer = player as OfflinePlayer
            var team = player.scoreboard.getPlayerTeam(oPlayer)

            if (args.size == 1) {
                Chat.sendMessage(player, "&cInvalid usage: /team invite <player>")
                return false
            }
            if (team == null) {
                Bukkit.dispatchCommand(player, "team create")
                team = player.scoreboard.getPlayerTeam(oPlayer)
            }
            if (team.size >= SettingsFeature.instance.data!!.getString("game.teamSize").toInt()) {
                player.sendMessage("${ChatColor.RED}Your team has reached the max teamsize.")
                return true
            }
            val target = Bukkit.getServer().getPlayer(args[1])
            if (target == null) {
                player.sendMessage("${ChatColor.RED}That player is not online at the moment.")
                return false
            }
            val oTarget = target as OfflinePlayer
            val team1 = player.scoreboard.getPlayerTeam(oTarget)

            if (team1 != null) {
                player.sendMessage("${ChatColor.RED}That player is already on a team.")
                return true
            }
            if (target == player) {
                player.sendMessage("${ChatColor.RED}You can't send a invite request to yourself.")
                return true
            }

            for (players in team.players) {
                if (players is Player) {
                    Chat.sendMessage(players, Chat.line)
                    Chat.sendMessage(player, "${Chat.prefix} ${ChatColor.WHITE}${target.name}${ChatColor.GRAY} was invited to your team.")
                    Chat.sendMessage(players, Chat.line)
                }
            }

            if (!invites.containsKey(player)) invites[player] = ArrayList()
            invites[player]!!.add(target)
            val text = TextComponent(Chat.colored("${Chat.prefix} §7To accept, type ${ChatColor.WHITE}/team accept ${player.name}${ChatColor.GRAY} or &f&nclick here&7."))
            text.clickEvent = ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/team accept ${player.name}"
            )
            Chat.sendMessage(target, Chat.line)
            Chat.sendMessage(target, "${Chat.prefix} You have been invited to ${ChatColor.WHITE}${player.name}${ChatColor.GRAY}'s team.")
            target.spigot().sendMessage(text)
            Chat.sendMessage(target, Chat.line)
        } else if (args[0] == "size") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 2) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You need to send a valid teamsize.")
                return false
            }
            if (args[1].toIntOrNull() == null) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You need to send a valid teamsize.")
                return false
            }
            settings.data!!.set("game.teamSize", args[1].toInt())
            settings.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.GRAY}The teamsize has been set to ${ChatColor.WHITE}${args[1]}${ChatColor.GRAY}.")
        } else if (args[0] == "accept") {
            val player = sender as Player
            if (args.size == 1) {
                player.sendMessage(Chat.colored("&cInvalid usage: /team accept <player>"))
                return false
            }
            val target = Bukkit.getServer().getPlayer(args[1])
            val team = target.scoreboard.getPlayerTeam(target)
            if (SettingsFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                player.sendMessage("${ChatColor.RED}This is an FFA game.")
                return true
            }
            if (GameState.currentState != GameState.LOBBY) {
                player.sendMessage("${ChatColor.RED}You cannot do this command at the moment.")
                return true
            }
            if (target == null) {
                Chat.sendMessage(sender, "${ChatColor.RED}That player is not online.")
                return false
            }
            if (invites.containsKey(target) && invites[target]!!.contains(player)) {
                if (team.size >= SettingsFeature.instance.data!!.getString("game.teamSize").toInt()) {
                    player.sendMessage("${ChatColor.RED}That team has reached the max teamsize.")
                    return false
                }

                Chat.sendMessage(player, "${Chat.prefix} Request accepted.")
                team.addPlayer(player)
                for (players in team.players) {
                    if (players is Player) {
                        players.sendMessage(Chat.line)
                        Chat.sendMessage(players, "${Chat.prefix} ${ChatColor.WHITE}${player.name}${ChatColor.GRAY} joined your team.")
                        players.sendMessage(Chat.line)
                    }
                }

            } else {
                player.sendMessage("${ChatColor.RED}That player has not sent you a team invite.")
                return false
            }
        } else if (args[0] == "management") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender as Player, "${Chat.prefix} Invalid usage: ${ChatColor.WHITE}/team management on/off")
                return false
            }
            if (args[1] != "on" && args[1] != "off") {
                Chat.sendMessage(sender as Player, "${Chat.prefix} Invalid usage: ${ChatColor.WHITE}/team management on/off")
                return false
            }
            Chat.sendMessage(sender, Chat.line)
            if (args[1] == "on") {
                settings.data!!.set("game.ffa", false)
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.GRAY}Team management has been &aenabled&7."))
            } else if (args[1] == "off") {
                settings.data!!.set("game.ffa", true)
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.GRAY}Team management has been &cdisabled&7."))
            }
            settings.saveData()
            Chat.sendMessage(sender, Chat.line)
        } else if (args[0] == "reset") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            val player = sender as Player
            for (team in TeamsFeature.manager.getTeams()) {
                for (p in team.players) {
                    team.removePlayer(p)
                }
            }
            Chat.sendMessage(player, "${Chat.prefix} You've reset all teams.")
        } else if (args[0] == "leave") {
            val player = sender as Player
            val team = player.scoreboard.getPlayerTeam(player)
            if (SettingsFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                player.sendMessage("${ChatColor.RED}You can't do this command at the moment.")
                return true
            }

            if (GameState.currentState != GameState.LOBBY) {
                player.sendMessage("${ChatColor.RED}You can't do this command at the moment.")
                return true
            }

            if (team == null) {
                player.sendMessage(ChatColor.RED.toString() + "You are not on a team.")
                return true
            }
            team.removePlayer(player)
            LunarClientAPI.getInstance().sendTeammates(player, LCPacketTeammates(player.uniqueId, 100L, HashMap()))
            Chat.sendMessage(player, "${Chat.prefix} You left your team.")
            for (players in team.players) {
                if (players is Player) {
                    players.sendMessage(Chat.line)
                    Chat.sendMessage(players, "${Chat.prefix}${ChatColor.WHITE}${player.name}${ChatColor.GRAY} left your team.")
                    players.sendMessage(Chat.line)
                }
            }
        } else if (args[0] == "list") {
            Chat.sendMessage(sender, Chat.line)
            Chat.sendCenteredMessage(sender, "&c&lTeams List")
            Chat.sendMessage(sender, " ")
            val teamList = ArrayList<Team>()
            for ((index, team) in TeamsFeature.manager.getTeams().withIndex()) {
                if (team.players.size != 0) {
                    teamList.add(team)
                    val list = ArrayList<String>()
                    for (player in team.players) {
                        list.add(player.name)
                    }
                    Chat.sendMessage(sender, "${team.prefix}${team.name} &8(&f${list.size}&8) ${Chat.dash} &f${list.joinToString(", ")}")
                }
            }
            if (teamList.isEmpty()) {
                Chat.sendCenteredMessage(sender, "&7&lThere are no teams right now!")
            }
            Chat.sendMessage(sender, Chat.line)
        } else if (args[0] == "delete") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "&cYou need to provide a team to delete.")
                return false
            }
            var selectedTeam: Team? = null
            for (team in TeamsFeature.manager.getTeams()) {
                if (team.name == args[1]) {
                    selectedTeam = team
                }
            }
            if (selectedTeam == null) {
                Chat.sendMessage(sender, "&cYou need to provide a team to delete. (typically a team name has \"UHC\" and then the numerical ID next to it)")
                return false
            }
            for (player in selectedTeam.players) {
                selectedTeam.removePlayer(player)
                if (player.isOnline) {
                    LunarClientAPI.getInstance().sendTeammates(player as Player, LCPacketTeammates(player.uniqueId, 100L, HashMap()))
                }
            }
            Chat.sendMessage(sender, "${Chat.prefix} ${selectedTeam.prefix}${selectedTeam.name}&7 has been deleted & all members kicked.")
        } else if (args[0] == "set") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 3) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: &f/team set <Player1> <Player2>&7. &8(&fPlayer 2 has to be the one with the team.&8)")
                return false
            }
            val target = Bukkit.getPlayer(args[1])
            val target2 = Bukkit.getPlayer(args[2])
            if (target2 == null || target == null) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid players: &f/team set <Player1> <Player2>&7. &8(&fPlayer 2 has to be the one with the team.&8)")
                return false
            }
            val team = TeamsFeature.manager.getTeam(target2)
            if (team == null) {
                Chat.sendMessage(sender, "&cThat player is currently not in a team right now.")
                return false
            }
            team.addPlayer(target)
            Chat.sendMessage(sender, "${Chat.prefix} Successfully added &f${target2.name}&7 to &f${target.name}&7's team")
            Chat.sendMessage(target, "${Chat.prefix} You've been added to &f${target2.name}&7's team")
            for (player in team.players) {
                if (player.isOnline) {
                    Chat.sendMessage(player as Player, "${Chat.prefix} &f${target.name}&7 has been added to your team.")
                }
            }
        } else if (args[0] == "friendlyfire") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: &f/team friendlyfire <on/off>&7.")
                return false
            }
            if (args[1] != "on" && args[1] != "off") {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid arguments: &f/team friendlyfire <on/off>&7.")
                return false
            }
            if (args[1] == "on") {
                for (team in TeamsFeature.manager.getTeams()) {
                    team.setAllowFriendlyFire(true)
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Friendly fire has been enabled by &f${sender.name}&7."))
            }
            if (args[1] == "off") {
                for (team in TeamsFeature.manager.getTeams()) {
                    team.setAllowFriendlyFire(false)
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Friendly fire has been disabled by &f${sender.name}&7."))
            }
        } else if (args[0] == "kickunder") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: &f/team kickunder <number>&7.")
                return false
            }
            if (args[1].toIntOrNull() == null) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid number: &f/team kickunder <number>&7.")
                return false
            }
            for (player in Bukkit.getOnlinePlayers()) {
                val team = TeamsFeature.manager.getTeam(player)
                if (team == null) {
                    player.kickPlayer(Chat.colored("&cYou've been kicked as you are not on a team."))
                } else {
                    if (team.players.size < args[1].toInt()) {
                        player.kickPlayer(Chat.colored("&cYou've been kicked as your team is undersized."))
                    }
                }
            }
        } else if (args[0] == "randomize") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            for (team in TeamsFeature.manager.getTeams()) {
                for (p in team.players) {
                    team.removePlayer(p)
                }
            }
            Bukkit.broadcastMessage("${Chat.prefix} Randomizing all players into teams of &c${SettingsFeature.instance.data!!.getInt("game.teamSize")}&7.")
            val valid: MutableList<Player> = ArrayList()
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    valid.add(player)
                }
            }
            valid.shuffle()
            val teams: List<List<Player>> = splitList(valid, SettingsFeature.instance.data!!.getInt("game.teamSize"))!!
            for (team in teams) {
                for (t in TeamsFeature.manager.getTeams()) {
                    if (t.size == 0) {
                        for (player in team) {
                            t.addPlayer(player)
                            Chat.sendMessage(player, "${Chat.prefix} You've been added to ${t.prefix}${t.name}&7, check &f/team list&7 for the members of your team.")
                        }
                        continue
                    }
                }
            }
        }

        return true
    }

}