package pink.mino.kraftwerk.commands

import com.google.gson.Gson
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import java.awt.Color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.floor

class ScheduleBroadcast(private val opening: String) : BukkitRunnable() {
    fun getTime(): String {
        with(URL("https://hosts.uhc.gg/api/sync").openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return "${response.toString()[12]}${response.toString()[13]}:${response.toString()[15]}${response.toString()[16]}"
            }
        }
    }

    private fun removeFifteenMinutes(time: String): String {
        val makeSureIsValid: List<String> = time.split(":")
        var hours: Any = makeSureIsValid[0].toInt()
        var minutes: Any
        if (makeSureIsValid[1] == "15") {
            minutes = 0
        } else if (makeSureIsValid[1] == "30") {
            minutes = 15
        } else if (makeSureIsValid[1] == "45") {
            minutes = 30
        } else {
            minutes = 45
            hours = if (makeSureIsValid[0].toInt() == 0) {
                23
            } else {
                makeSureIsValid[0].toInt() - 1
            }
        }
        if (hours.toString().length == 1) {
            hours = "0${hours}"
        }
        if (minutes.toString().length == 1) {
            minutes = "00"
        }
        return "${hours}:${minutes}"
    }

    override fun run() {
        print("Checking if the time corresponds with the broadcast time... ${removeFifteenMinutes(opening)} & ${getTime()}")
        if (SettingsFeature.instance.data!!.getString("matchpost.opens") == null) {
            cancel()
        }
        if (SettingsFeature.instance.data!!.getBoolean("matchpost.cancelled") == true) {
            cancel()
        }
        if (getTime() == removeFifteenMinutes(opening)) {
            val host = Bukkit.getOfflinePlayer(SettingsFeature.instance.data!!.getString("game.host"))
            var embed = EmbedBuilder()
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            val scenarios = SettingsFeature.instance.data!!.getStringList("matchpost.scenarios")
            val opening = (System.currentTimeMillis() / 1000L) + (900000L) / 1000L
            embed.addField("Teams", SettingsFeature.instance.data!!.getString("matchpost.team"), true)
            embed.addField("Opening", "<t:${opening}:t> (<t:${opening}:R>)", true)
            embed.addField("Scenarios", scenarios.joinToString(", "), true)
            var flag = ":checkered_flag:"
            if (SettingsFeature.instance.data!!.getString("server.region") == "EU") {
                flag = ":flag_de:"
            } else if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
                flag = ":flag_ca:"
            }
            embed.addField("IP", "$flag - `${SettingsFeature.instance.data!!.getString("server.region").lowercase()}.applejuice.bar`", true)
            embed.addField("Matchpost", "https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}", true)
            if (JavaPlugin.getPlugin(Kraftwerk::class.java).discord) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).discordInstance.getTextChannelById(937811305102999573)!!.sendMessage("<@&793406242013839381> (Use /togglematches to toggle matchpost alerts)").queue()
                JavaPlugin.getPlugin(Kraftwerk::class.java).discordInstance.getTextChannelById(937811305102999573)!!.sendMessageEmbeds(embed.build()).queue()
            }
            embed = EmbedBuilder()
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            embed.addField("Pre-whitelists are on!", "You are now allowed to use the command `/wl` to request to pre-whitelist yourself in the server!", false)
            JavaPlugin.getPlugin(Kraftwerk::class.java).discordInstance.getTextChannelById(937812061948346398)!!.sendMessageEmbeds(embed.build()).queue()
            SettingsFeature.instance.data!!.set("whitelist.requests", true)
            SettingsFeature.instance.data!!.set("matchpost.posted", true)
            SettingsFeature.instance.saveData()
            cancel()
        }
    }
}

class Opening(private val closing: Long) : BukkitRunnable() {
    var timer = 0

    private fun timeToString(ticks: Long): String {
        var t = ticks
        val hours = floor(t / 3600.toDouble()).toInt()
        t -= hours * 3600
        val minutes = floor(t / 60.toDouble()).toInt()
        t -= minutes * 60
        val seconds = t.toInt()
        val output = StringBuilder()
        if (hours > 0) {
            output.append(hours).append('h')
            if (minutes == 0) {
                output.append(minutes).append('m')
            }
        }
        if (minutes > 0) {
            output.append(minutes).append('m')
        }
        output.append(seconds).append('s')
        return output.toString()
    }

    private fun displayTimer(player: Player) {
        ActionBar.sendActionBarMessage(player, "&cWhitelist is enabled in ${Chat.dash} &f${timeToString(closing - timer.toLong())}")
    }

    override fun run() {
        if (timer == closing.toInt()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl on")
            cancel()
        }
        timer++
        for (player in Bukkit.getOnlinePlayers()) {
            displayTimer(player)
        }
    }
}

class ScheduleOpening(private val opening: String) : BukkitRunnable() {
    fun getTime(): String {
        with(URL("https://hosts.uhc.gg/api/sync").openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return "${response.toString()[12]}${response.toString()[13]}:${response.toString()[15]}${response.toString()[16]}"
            }
        }
    }

    override fun run() {
        print("Checking if the time corresponds with the opening...")
        if (SettingsFeature.instance.data!!.getString("matchpost.opens") == null) {
            cancel()
        }
        if (SettingsFeature.instance.data!!.getBoolean("matchpost.cancelled") == true) {
            cancel()
        }
        if (getTime() == opening) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl off")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel")
            val time: Long
            if (SettingsFeature.instance.data!!.getBoolean("matchpost.teamsGame")) {
                time = 600
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 600 &cWhitelist is enabled in ${Chat.dash}&f")
            } else {
                time = 300
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 300 &cWhitelist is enabled in ${Chat.dash}&f")
            }
            val host = Bukkit.getPlayer(SettingsFeature.instance.data!!.getString("game.host"))
            val embed = EmbedBuilder()
            embed.addField("Matchpost", "https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}", false)
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            if (SettingsFeature.instance.data!!.getString("server.region") == "EU") {
                embed.addField("Game Open!", "The game is now open @ :beverage_box: `eu.applejuice.bar`.", false)
            } else if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
                embed.addField("Game Open!", "The game is now open @ :beverage_box: `na.applejuice.bar`.", false)
            }
            if (JavaPlugin.getPlugin(Kraftwerk::class.java).discord) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).discordInstance.getTextChannelById(937811678735765554)!!.sendMessageEmbeds(embed.build()).queue()
            }
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The whitelist has been turned off automatically @ &c${opening}&7."))
            cancel()
            Opening(time).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            SettingsFeature.instance.data!!.set("whitelist.requests", false)
            SettingsFeature.instance.data!!.set("matchpost.opens", null)
            SettingsFeature.instance.saveData()
        }
    }
}

class MatchpostCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.matchpost")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            if (sender.hasPermission("uhc.staff.matchpost")) {
                Chat.sendMessage(sender, "&cYou must provide a valid matchpost ID.")
                return false
            } else {
                Chat.sendMessage(sender, "${Chat.prefix} Matchpost: &chttps://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}")
                return false
            }
        }
        if (args[0].toIntOrNull() == null) {
            Chat.sendMessage(sender, "&cYou must provide a &ovalid&c matchpost ID.")
            return false
        }
        val host: String
        val id: Double
        val opening: String
        val scenarios: Any
        var team: String? = null
        var teamsGame: Boolean = false
        val scenarioList = ArrayList<String>()
        with(URL("https://hosts.uhc.gg/api/matches/${args[0]}").openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")

            if (responseCode == 404) {
                Chat.sendMessage(sender, "&cInvalid match.")
                return false
            }
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                var map: Map<String, Any> = HashMap()
                map = Gson().fromJson(response.toString(), map.javaClass)
                host = if (map["hostingName"] != null) {
                    "${map["hostingName"]}'s #${(map["count"] as Double).toInt()}"
                } else {
                    "${map["author"]}'s #${(map["count"] as Double).toInt()}"
                }
                if (map["teams"] as String == "ffa") {
                    teamsGame = false
                    team = "FFA"
                } else if (map["teams"] as String == "chosen") {
                    teamsGame = true
                    team = "Chosen To${(map["size"] as Double).toInt()}"
                } else if (map["teams"] as String == "rvb") {
                    teamsGame = true
                    team = "Red vs. Blue"
                } else if (map["teams"] as String == "random") {
                    teamsGame = true
                    team = "Random To${(map["size"] as Double).toInt()}"
                }
                id = map["id"] as Double
                scenarios = map["scenarios"] as List<*>
                for (scenario in ScenarioHandler.getActiveScenarios()){
                    scenario.toggle()
                }
                for (scenario in scenarios) {
                    scenarioList.add((scenario as String).lowercase().replace(" ", ""))
                    try { ScenarioHandler.getScenario(scenario.lowercase().replace(" ", ""))!!.toggle()
                    } catch (_: Exception) {}
                }
                opening = "${(map["opens"] as String)[11]}${(map["opens"] as String)[12]}:${(map["opens"] as String)[14]}${(map["opens"] as String)[15]}"
            }
        }
        SettingsFeature.instance.data!!.set("matchpost.team", team)
        SettingsFeature.instance.data!!.set("matchpost.teamsGame", teamsGame)
        SettingsFeature.instance.data!!.set("matchpost.host", host)
        SettingsFeature.instance.data!!.set("matchpost.id", id.toInt())
        SettingsFeature.instance.data!!.set("matchpost.scenarioIds", scenarioList)
        SettingsFeature.instance.data!!.set("matchpost.scenarios", scenarios)
        SettingsFeature.instance.data!!.set("matchpost.opens", opening)
        ScheduleOpening(opening).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 300L)
        ScheduleBroadcast(opening).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 300L)
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).discord) JavaPlugin.getPlugin(Kraftwerk::class.java).discordInstance.presence.activity = Activity.playing(host)
        Chat.sendMessage(sender, "${Chat.prefix} Set the matchpost to &chttps://hosts.uhc.gg/m/${id.toInt()}")
        SettingsFeature.instance.saveData()
        return true
    }

}