package pink.mino.kraftwerk.commands


import com.lunarclient.bukkitapi.LunarClientAPI
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PerkChecker
import pink.mino.kraftwerk.utils.PlayerUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer


class SendTeamView(val team: Team) : BukkitRunnable() {
    override fun run() {
        if (team == null) {
            cancel()
        }
        if (!TeamsFeature.manager.getTeams().contains(team)) {
            cancel()
        }

        try {
            if (team.size == 0) {
                cancel()
            }
            if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles"))) {
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
        } catch (e: Exception) {
            cancel()
        }
    }
}

class TeamCommand : CommandExecutor {

    private var invites = HashMap<Player, ArrayList<Player>>()
    private val settings: SettingsFeature = SettingsFeature.instance
    val colors = listOf(
        "black",
        "dark_blue",
        "dark_green",
        "dark_aqua",
        "dark_red",
        "dark_purple",
        "gold",
        "gray",
        "dark_gray",
        "blue",
        "green",
        "aqua",
        "red",
        "light_purple",
        "yellow",
        "white"
    )
    val modifiers = listOf(
        "bold",
        "underline",
        "italic"
    )

    private fun <T> splitList(list: ArrayList<T>, size: Int): MutableList<ArrayList<T>> {
        val iterator = list.iterator()
        val returnList: MutableList<ArrayList<T>> = ArrayList()
        while (iterator.hasNext()) {
            val tempList: MutableList<T> = ArrayList()
            for (i in 0 until size) {
                if (!iterator.hasNext()) break
                tempList.add(iterator.next())
            }
            returnList.add(tempList as ArrayList<T>)
        }
        return returnList
    }

    private fun getEmptyTeam(): Team? {
        val teams = TeamsFeature.manager.getTeams()
        for (team in teams) {
            if (team.size == 0) {
                return team
            }
        }
        return null
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
                    Chat.sendMessage(
                        sender,
                        "${ChatColor.RED}You can't use this command at the moment. (It's an FFA game or Random teams)"
                    )
                    return false
                } else if (GameState.valueOf(SettingsFeature.instance.data!!.getString("game.state")) != GameState.LOBBY) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You can't use this command at the moment.")
                    return false
                }
            }
        }

        if (args.isEmpty()) {
            Chat.sendMessage(sender as Player, Chat.line)
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/team create ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Creates a team."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/team invite <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Invites a player to your team."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/team leave ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Leave your team."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/team accept <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Accept a player's team invite."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/team list ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Brings a list of teams and their members."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/pm <message> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Talk in team chat."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/team color <color> [bold] [italic] [underline] &8(&6GOLD AND ABOVE&8) ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Changes your team color."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} &f/pmc ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Send your coordinates."
            )
            Chat.sendMessage(sender, Chat.line)
            if (sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team reset ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Reset all teams."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team management <on/off> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Enable/disable team management."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team size <size> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Set the size of teams."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team set <player1> <player2> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Sets Player 1 to the Player 2's team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team bulk <list of players> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Adds a list of players to a team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team remove <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Removes a player from a team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team delete <team name> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Deletes the provided team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team friendlyfire <on/off> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Toggles friendly fire."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team kickunder <number> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Kicks all solos/teams under a certain threshold."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team randomize ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Randomizes all players that aren't Spectators into a team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} &f/team rvb ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Randomizes all players that aren't Spectators into a two teams together."
                )
                Chat.sendMessage(sender, Chat.line)
            }
        } else if (args[0] == "create") {
            val player = sender as Player
            if (player.scoreboard.getPlayerTeam(player) != null) {
                Chat.sendMessage(player, "&cYou're already on a team.")
                return true
            }
            if (SettingsFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                Chat.sendMessage(player, "&cTeam management is disabled at the moment.")
                return true
            }
            if (GameState.valueOf(SettingsFeature.instance.data!!.getString("game.state")) != GameState.LOBBY) {
                Chat.sendMessage(player, "&cYou can't manage teams while the game is running!")
                return true
            }

            val team = TeamsFeature.manager.createTeam(player)

            SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            Chat.sendMessage(sender, "${Chat.dash} Successfully created &f${team.displayName}&7!")
        } else if (args[0] == "invite") {
            val player = sender as Player
            var team = TeamsFeature.manager.getTeam(player)
            if (SettingsFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                Chat.sendMessage(player, "${Chat.dash} Team management is disabled at the moment.")
                return true
            }
            if (args.size == 1) {
                Chat.sendMessage(player, "${Chat.dash} Usage: &f/team invite <player>")
                return false
            }
            if (team == null) {
                team = TeamsFeature.manager.createTeam(player)
                SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            }
            if (team.size >= SettingsFeature.instance.data!!.getString("game.teamSize").toInt()) {
                Chat.sendMessage(player, "&cYour team is too full to invite anyone!")
                return true
            }
            val target = Bukkit.getServer().getPlayer(args[1])
            if (target == null) {
                Chat.sendMessage(player, "&cThat player is not online!")
                return false
            }
            val targetTeam = TeamsFeature.manager.getTeam(target)

            if (targetTeam != null) {
                Chat.sendMessage(player, "&cThat player is already on a team.")
                return true
            }
            if (target == player) {
                Chat.sendMessage(player, "&cYou can't send a invite request to yourself.")
                return true
            }

            for (players in team.players) {
                if (players is Player) {
                    Chat.sendMessage(
                        player,
                        "${Chat.dash} ${ChatColor.WHITE}${target.name}${ChatColor.GRAY} was invited to your team."
                    )
                }
            }

            if (!invites.containsKey(player)) invites[player] = ArrayList()
            invites[player]!!.add(target)
            val text =
                TextComponent(Chat.colored("${Chat.dash} §7To accept, type ${ChatColor.WHITE}/team accept ${player.name}${ChatColor.GRAY} or &f&nclick here&7."))
            text.clickEvent = ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/team accept ${player.name}"
            )
            Chat.sendMessage(target, Chat.line)
            Chat.sendMessage(
                target,
                "${Chat.dash} You have been invited to ${ChatColor.WHITE}${player.name}${ChatColor.GRAY}'s team."
            )
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
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${ChatColor.GRAY}The teamsize has been set to ${ChatColor.WHITE}${args[1]}${ChatColor.GRAY}."
            )
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
                    player.sendMessage("${ChatColor.RED}That team is too full to join!")
                    return false
                }
                Chat.sendMessage(player, "${Chat.dash} &7You have joined &f${team.displayName}&7!")
                team.addPlayer(player)
                for (players in team.players) {
                    if (players is Player && players != player) {
                        Chat.sendMessage(
                            players,
                            "${Chat.dash} ${ChatColor.WHITE}${player.name}${ChatColor.GRAY} joined your team."
                        )
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
                Chat.sendMessage(
                    sender as Player,
                    "${Chat.dash} Invalid usage: ${ChatColor.WHITE}/team management on/off"
                )
                return false
            }
            if (args[1] != "on" && args[1] != "off") {
                Chat.sendMessage(
                    sender as Player,
                    "${Chat.dash} Invalid usage: ${ChatColor.WHITE}/team management on/off"
                )
                return false
            }
            Chat.sendMessage(sender, Chat.line)
            if (args[1] == "on") {
                settings.data!!.set("game.ffa", false)
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} ${ChatColor.GRAY}Team management has been &aenabled&7."))
            } else if (args[1] == "off") {
                settings.data!!.set("game.ffa", true)
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} ${ChatColor.GRAY}Team management has been &cdisabled&7."))
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
            TeamsFeature.manager.resetTeams()
            Chat.sendMessage(player, "${Chat.dash} You've reset all teams.")
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
            Chat.sendMessage(player, "${Chat.dash} You left your team.")
            for (players in team.players) {
                if (players is Player) {
                    Chat.sendMessage(
                        players,
                        "${Chat.dash}${ChatColor.WHITE}${player.name}${ChatColor.GRAY} left your team."
                    )
                }
            }
            if (team.players.size == 0) {
                TeamsFeature.manager.deleteTeam(team)
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
                    Chat.sendMessage(
                        sender,
                        "${team.displayName} &8(&f${list.size}&8) ${Chat.dash} &f${list.joinToString(", ")}"
                    )
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
                Chat.sendMessage(
                    sender,
                    "&cYou need to provide a team to delete. (typically a team name has \"UHC\" and then the numerical ID next to it)"
                )
                return false
            }
            for (player in selectedTeam.players) {
                selectedTeam.removePlayer(player)
                if (player.isOnline) {
                    LunarClientAPI.getInstance()
                        .sendTeammates(player as Player, LCPacketTeammates(player.uniqueId, 100L, HashMap()))
                }
            }
            TeamsFeature.manager.deleteTeam(selectedTeam)
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${selectedTeam.prefix}${selectedTeam.name}&7 has been deleted & all members kicked."
            )
        } else if (args[0] == "set") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 3) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team set <Player1> <Player2>&7. &8(&fPlayer 2 has to be the one with the team.&8)"
                )
                return false
            }
            val target = Bukkit.getPlayer(args[1])
            val target2 = Bukkit.getPlayer(args[2])
            if (target2 == null || target == null) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid players: &f/team set <Player1> <Player2>&7. &8(&fPlayer 2 has to be the one with the team.&8)"
                )
                return false
            }
            val team = TeamsFeature.manager.getTeam(target2)
            if (team == null) {
                Chat.sendMessage(sender, "&cThat player is currently not in a team right now.")
                return false
            }
            val targetTeam = TeamsFeature.manager.getTeam(target)
            team.addPlayer(target)
            if (targetTeam != null) {
                if (targetTeam.size == 0) {
                    TeamsFeature.manager.deleteTeam(targetTeam)
                }
            }
            Chat.sendMessage(
                sender,
                "${Chat.dash} Successfully added &f${target2.name}&7 to &f${target.name}&7's team"
            )
            Chat.sendMessage(target, "${Chat.dash} You've been added to &f${target2.name}&7's team")
            for (player in team.players) {
                if (player.isOnline) {
                    Chat.sendMessage(player as Player, "${Chat.dash} &f${target.name}&7 has been added to your team.")
                }
            }
        } else if (args[0] == "bulk" || args[0] == "ct") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/team bulk <list of players>&7.")
                return false
            }
            val t: Team = TeamsFeature.manager.createTeam()
            for ((index, element) in args.withIndex()) {
                if (index == 0) continue
                val target = Bukkit.getPlayer(element)
                if (target == null) {
                    Chat.sendMessage(sender, "&c${element} is not online.")
                    continue
                }
                val team = TeamsFeature.manager.getTeam(target)
                if (team != null) {
                    Chat.sendMessage(sender, "&c${element} is already in a team.")
                    continue
                }
                t.addPlayer(target)
            }
            Chat.sendMessage(sender, "${Chat.dash} Successfully added all players to the team.")
        } else if (args[0] == "remove" || args[0] == "kick") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/team remove <Player>&7.")
                return false
            }
            val target = Bukkit.getPlayer(args[1])
            if (target == null) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid player: &f${args[1]}&7.")
                return false
            }
            val team = TeamsFeature.manager.getTeam(target)
            if (team == null) {
                Chat.sendMessage(sender, "${Chat.dash} That player is currently not in a team right now.")
                return false
            }
            team.removePlayer(target)
            Chat.sendMessage(
                sender,
                "${Chat.dash} Successfully removed &f${target.name}&7 from &f${team.name}&7's team"
            )
            if (team.size == 0) {
                TeamsFeature.manager.deleteTeam(team)
            }
        } else if (args[0] == "friendlyfire") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/team friendlyfire <on/off>&7.")
                return false
            }
            if (args[1] != "on" && args[1] != "off") {
                Chat.sendMessage(sender, "${Chat.dash} Invalid arguments: &f/team friendlyfire <on/off>&7.")
                return false
            }
            if (args[1] == "on") {
                for (team in TeamsFeature.manager.getTeams()) {
                    team.setAllowFriendlyFire(true)
                    SettingsFeature.instance.data!!.set("game.friendlyFire", true)
                    SettingsFeature.instance.saveData()
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Friendly fire has been enabled by &f${sender.name}&7."))
            }
            if (args[1] == "off") {
                for (team in TeamsFeature.manager.getTeams()) {
                    team.setAllowFriendlyFire(false)
                    SettingsFeature.instance.data!!.set("game.friendlyFire", false)
                    SettingsFeature.instance.saveData()
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Friendly fire has been disabled by &f${sender.name}&7."))
            }
        } else if (args[0] == "kickunder") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/team kickunder <number>&7.")
                return false
            }
            if (args[1].toIntOrNull() == null) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid number: &f/team kickunder <number>&7.")
                return false
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (SpecFeature.instance.isSpec(player)) continue
                val team = TeamsFeature.manager.getTeam(player)
                if (team == null) {
                    player.kickPlayer(Chat.colored("&cYou've been kicked as you are not on a team."))
                } else {
                    if (team.players.size < args[1].toInt()) {
                        player.kickPlayer(Chat.colored("&cYou've been kicked as your team is undersized."))
                    }
                    TeamsFeature.manager.deleteTeam(team)
                }
            }
        } else if (args[0] == "randomize") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            TeamsFeature.manager.resetTeams()
            Bukkit.broadcastMessage(
                Chat.colored(
                    "${Chat.dash} Randomizing all players into teams of &c${
                        SettingsFeature.instance.data!!.getInt(
                            "game.teamSize"
                        )
                    }&7."
                )
            )
            val valid: ArrayList<Player> = ArrayList()
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    valid.add(player)
                }
            }
            valid.shuffle()
            val teams = splitList(valid, SettingsFeature.instance.data!!.getInt("game.teamSize"))
            var templist: ArrayList<Player>
            for (list in teams) {
                templist = ArrayList()
                for (player in list) {
                    templist.add(player)
                }
                val team: Team = TeamsFeature.manager.createTeam()
                for (player in templist) {
                    Chat.sendMessage(
                        player,
                        "${Chat.dash} You've been added to ${team.prefix}${team.name}&7, check &f/team list&7 for the members of your team."
                    )
                    team.addPlayer(player)
                }
            }
        } else if (args[0] == "rvb") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            TeamsFeature.manager.resetTeams()
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Randomizing all players into &cRed&7 vs &9Blue&7."))
            val valid: ArrayList<Player> = ArrayList()
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    valid.add(player)
                }
            }
            valid.shuffle()
            val red = TeamsFeature.manager.createTeam()
            val blue = TeamsFeature.manager.createTeam()
            red.prefix = "${ChatColor.RED}"
            blue.prefix = "${ChatColor.BLUE}"
            for ((index, player) in valid.withIndex()) {
                if (index % 2 == 0) {
                    red.addPlayer(player)
                } else {
                    blue.addPlayer(player)
                }
            }
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} &cRed&7 vs &9Blue&7 teams have been randomized."))
        } else if (args[0] == "color") {
            if (!PerkChecker.checkPerk(sender as Player, "uhc.donator.teamColors")) {
                Chat.sendMessage(sender, "&cBuy &6Gold&c to use this perk. &eapplejuice.tebex.io")
                return false
            }
            if (TeamsFeature.manager.getTeam(sender) == null) {
                Chat.sendMessage(sender, "${Chat.dash} You are not on a team.")
                return false
            }
            if (args.size == 1) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team color <color> [bold] [italic] [underline]&7."
                )
                return false
            }
            if (args.size > 5) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team color <color> [bold] [italic] [underline]&7."
                )
                return false
            }
            val colors = arrayListOf<ChatColor>()
            for ((index, arg) in args.withIndex()) {
                if (index == 0) continue
                try {
                    colors.add(ChatColor.valueOf(arg.uppercase()))
                } catch (e: Exception) {
                    Chat.sendMessage(sender, "${Chat.dash} Invalid color: &f$arg&7.")
                    Chat.sendMessage(
                        sender,
                        "${Chat.dash} Valid colors: &0black&7, &1dark_blue&7, &2dark_green&7, &3dark_aqua&7, &4dark_red&7, &5dark_purple&7, &6gold&7, &7gray&7, &8dark_gray&7, &9blue&7, &agreen&7, &baqua&7, &cred&7, &dlight_purple&7, &eyellow&7, &fwhite&7."
                    )
                    return false
                }
                colors.add(ChatColor.valueOf(arg.uppercase()))
            }
            if (colors.size == 0) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team color <color> [bold] [italic] [underline]&7."
                )
                return false
            }
            var selectedColor = ""
            if (colors.contains(ChatColor.BOLD)) {
                selectedColor += ChatColor.BOLD.toString()
                colors.removeAll(listOf(ChatColor.BOLD))
            }
            if (colors.contains(ChatColor.ITALIC)) {
                selectedColor += ChatColor.ITALIC.toString()
                colors.removeAll(listOf(ChatColor.ITALIC))
            }
            if (colors.contains(ChatColor.UNDERLINE)) {
                selectedColor += ChatColor.UNDERLINE.toString()
                colors.removeAll(listOf(ChatColor.UNDERLINE))
            }
            if (colors.isEmpty()) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team color <color> [bold] [italic] [underline]&7."
                )
                return false
            }
            selectedColor = colors[0].toString() + selectedColor
            print(selectedColor)
            if (!TeamsFeature.manager.colors.contains(selectedColor)) {
                Chat.sendMessage(sender, "${Chat.dash} This color is already in use.")
                return false
            } else {
                TeamsFeature.manager.colors.add(TeamsFeature.manager.getTeam(sender)!!.prefix)
                TeamsFeature.manager.colors.remove(selectedColor)
                TeamsFeature.manager.getTeam(sender)!!.prefix = selectedColor
                Bukkit.broadcastMessage(Chat.colored("&8[&2$$$&8] &f${PlayerUtils.getPrefix(sender)}${sender.name} &7has selected &f$selectedColor${args[1]}&7 as their team color."))
            }
        } else if (args[0] == "recolor") {
            if (!sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
            if (args.size == 2) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team recolor <team> <color> [bold] [italic] [underline]&7."
                )
                return false
            }
            if (args.size > 6) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team recolor <team> <color> [bold] [italic] [underline]&7."
                )
                return false
            }
            val colors = arrayListOf<ChatColor>()
            for ((index, arg) in args.withIndex()) {
                if (index == 0 || index == 1) continue
                try {
                    colors.add(ChatColor.valueOf(arg.uppercase()))
                } catch (e: Exception) {
                    Chat.sendMessage(sender, "${Chat.dash} Invalid color: &f$arg&7.")
                    Chat.sendMessage(
                        sender,
                        "${Chat.dash} Valid colors: &0black&7, &1dark_blue&7, &2dark_green&7, &3dark_aqua&7, &4dark_red&7, &5dark_purple&7, &6gold&7, &7gray&7, &8dark_gray&7, &9blue&7, &agreen&7, &baqua&7, &cred&7, &dlight_purple&7, &eyellow&7, &fwhite&7."
                    )
                    return false
                }
                colors.add(ChatColor.valueOf(arg.uppercase()))
            }
            if (colors.size == 0) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team recolor <team> <color> [bold] [italic] [underline]&7."
                )
                return false
            }
            var selectedColor = ""
            if (colors.contains(ChatColor.BOLD)) {
                selectedColor += ChatColor.BOLD.toString()
                colors.removeAll(listOf(ChatColor.BOLD))
            }
            if (colors.contains(ChatColor.ITALIC)) {
                selectedColor += ChatColor.ITALIC.toString()
                colors.removeAll(listOf(ChatColor.ITALIC))
            }
            if (colors.contains(ChatColor.UNDERLINE)) {
                selectedColor += ChatColor.UNDERLINE.toString()
                colors.removeAll(listOf(ChatColor.UNDERLINE))
            }
            if (colors.isEmpty()) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} Invalid usage: &f/team recolor <team> <color> [bold] [italic] [underline]&7."
                )
                return false
            }

            val team = TeamsFeature.manager.getTeam(args[1])
            if (team == null) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid team: &f${args[1]}&7.")
                return false
            }

            selectedColor = colors[0].toString() + selectedColor
            print(selectedColor)
            if (!TeamsFeature.manager.colors.contains(selectedColor)) {
                Chat.sendMessage(sender, "${Chat.dash} This color is already in use.")
                return false
            } else {
                TeamsFeature.manager.colors.add(team.prefix)
                TeamsFeature.manager.colors.remove(selectedColor)
                team.prefix = selectedColor
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} You have changed the team color of ${team.prefix}${team.name}&7 to &f$selectedColor${args[2]}&7."
                )
                for (entry in team.entries) {
                    val player = Bukkit.getPlayer(entry)
                    if (player != null) {
                        Chat.sendMessage(
                            player,
                            "${Chat.dash} Your team color has been changed to &f$selectedColor${args[2]}&7."
                        )
                    }
                }
            }
        }
        return true
    }
}