package pink.mino.kraftwerk.commands


import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.features.Teams
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Settings
import kotlin.random.Random.Default.nextInt

class TeamCommand : CommandExecutor {

    private var invites = HashMap<Player, ArrayList<Player>>()
    private val settings: Settings = Settings.instance

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {

        if (!sender.hasPermission("uhc.staff.team")) {
            if (Settings.instance.data!!.getString("game.ffa").toBoolean()) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You can't use this command at the moment. (It's an FFA game or Random teams)")
                return false
            } else if (GameState.valueOf(Settings.instance.data!!.getString("game.state")) != GameState.LOBBY) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You can't use this command at the moment.")
                return false
            }
        }

        if (args.isEmpty()) {
            Chat.sendMessage(sender as Player, Chat.line)
            Chat.sendMessage(sender, "${Chat.prefix} &a/team create ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Creates a team.")
            Chat.sendMessage(sender, "${Chat.prefix} &a/team invite <player> ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Invites a player to your team.")
            Chat.sendMessage(sender, "${Chat.prefix} &a/team leave ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Leave your team.")
            Chat.sendMessage(sender, "${Chat.prefix} &a/team accept <player> ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Accept a player's team invite.")
            Chat.sendMessage(sender, "${Chat.prefix} &a/team list ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Brings a list of teams and their members.")
            Chat.sendMessage(sender, "${Chat.prefix} &a/pm <message> ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Talk in team chat.")
            Chat.sendMessage(sender, "${Chat.prefix} &a/pmc ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Send your coordinates.")
            Chat.sendMessage(sender, Chat.line)
            if (sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(sender, "${Chat.prefix} &a/team reset ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Reset all teams.")
                Chat.sendMessage(sender, "${Chat.prefix} &a/team management <on/off> ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Enable/disable team management.")
                Chat.sendMessage(sender, "${Chat.prefix} &a/team size <size> ${ChatColor.DARK_GRAY}-${ChatColor.WHITE} Set the size of teams.")
                Chat.sendMessage(sender, Chat.line)
            }
        } else if (args[0] == "create") {
            val player = sender as Player
            if (player.scoreboard.getPlayerTeam(player) != null) {
                player.sendMessage("${ChatColor.RED}You are already on a team.")
                return true
            }
            if (Settings.instance.data!!.getString("game.ffa").toBoolean()) {
                player.sendMessage("${ChatColor.RED}You can't use this command at the moment.")
                return true
            }
            if (GameState.valueOf(Settings.instance.data!!.getString("game.state")) !== GameState.LOBBY) {
                player.sendMessage("${ChatColor.RED}You can't use this command at the moment.")
                return true
            }
            val oTeams = ArrayList<Team>()

            for (team in Teams.manager.getTeams()) {
                if (team.size == 0) {
                    oTeams.add(team)
                }
            }

            oTeams[nextInt(oTeams.size)].addPlayer(player)
            player.sendMessage("${Chat.prefix} Team created! Use ${ChatColor.WHITE}/team invite <player>${ChatColor.GRAY} to invite a player.")
        } else if (args[0] == "invite") {
            val player = sender as Player
            val oPlayer = player as OfflinePlayer
            val team = player.scoreboard.getPlayerTeam(oPlayer)

            if (team == null) {
                player.sendMessage("${ChatColor.RED}You are not on a team.")
                return true
            }
            if (team.size >= Settings.instance.data!!.getString("game.teamSize").toInt()) {
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
                    players.sendMessage(Chat.line)
                    players.sendMessage("${Chat.prefix} ${ChatColor.WHITE}${target.name}${ChatColor.GRAY} was invited to your team.")
                    players.sendMessage(Chat.line)
                }
            }

            if (!invites.containsKey(player)) invites[player] = ArrayList()
            invites[player]!!.add(target)
            target.sendMessage(Chat.line)
            target.sendMessage("${Chat.prefix} You have been invited to ${ChatColor.WHITE}${player.name}${ChatColor.GRAY}'s team.")
            target.sendMessage("${Chat.prefix} §7To accept, type ${ChatColor.WHITE}/team accept ${player.name}${ChatColor.GRAY}.")
            target.sendMessage(Chat.line)
        } else if (args[0] == "size") {
            if (!sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
            if (args[1].isEmpty()) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You need to send a valid teamsize.")
                return false
            }
            if (args[1].toIntOrNull() == null) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You need to send a valid teamsize.")
                return false
            }
            settings.data!!.set("game.teamSize", args[1])
            settings.saveData()
            Chat.sendMessage(sender as Player, Chat.line)
            Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.GRAY}The teamsize has been set to ${ChatColor.WHITE}${args[1]}${ChatColor.GRAY}.")
            Chat.sendMessage(sender, Chat.line)
        } else if (args[0] == "accept") {
            val player = sender as Player
            val target = Bukkit.getServer().getPlayer(args[1])
            val team = target.scoreboard.getPlayerTeam(target)
            if (Settings.instance.data!!.getString("game.ffa").toBoolean()) {
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
                if (team.size >= Settings.instance.data!!.getString("game.teamSize").toInt()) {
                    player.sendMessage("${ChatColor.RED}That team has reached the max teamsize.")
                    return false
                }

                player.sendMessage("${Chat.prefix} Request accepted.")
                team.addPlayer(player)
                for (players in team.players) {
                    if (players is Player) {
                        players.sendMessage(Chat.line)
                        players.sendMessage("${Chat.prefix} ${ChatColor.WHITE}${player.name}${ChatColor.GRAY} joined your team.")
                        players.sendMessage(Chat.line)
                    }
                }

            } else {
                player.sendMessage("${ChatColor.RED}That player has not sent you a team invite.")
                return false
            }
        } else if (args[0] == "management") {
            if (!sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
            if (args[1] != "on" || args[1] != "off") {
                Chat.sendMessage(sender as Player, "${Chat.prefix} Invalid usage: ${ChatColor.WHITE}/team management on/off")
                return false
            }
            Chat.sendMessage(sender as Player, Chat.line)
            if (args[1] == "on") {
                settings.data!!.set("game.ffa", true)
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.GRAY}Team management has been set to ${ChatColor.WHITE}off${ChatColor.GRAY}.")
            } else if (args[1] == "off") {
                settings.data!!.set("game.ffa", false)
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.GRAY}Team management has been set to ${ChatColor.WHITE}on${ChatColor.GRAY}.")
            }
            settings.saveData()
            Chat.sendMessage(sender, Chat.line)
        } else if (args[0] == "reset") {
            if (!sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
            val player = sender as Player
            for (team in Teams.manager.getTeams()) {
                for (p in team.players) {
                    team.removePlayer(p)
                }
            }
            player.sendMessage(Chat.line)
            player.sendMessage("${Chat.prefix} You've reset all teams.")
            player.sendMessage(Chat.line)
        } else if (args[0] == "leave") {
            val player = sender as Player
            val team = player.scoreboard.getPlayerTeam(player)
            if (Settings.instance.data!!.getString("game.ffa").toBoolean()) {
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
            player.sendMessage("${Chat.prefix} You left your team.")
            for (players in team.players) {
                if (players is Player) {
                    players.sendMessage(Chat.line)
                    players.sendMessage("${Chat.prefix}${ChatColor.WHITE}${player.name}${ChatColor.GRAY} left your team.")
                    players.sendMessage(Chat.line)
                }
            }
        }

        return true
    }

}