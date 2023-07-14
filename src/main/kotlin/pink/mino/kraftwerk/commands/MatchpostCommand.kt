package pink.mino.kraftwerk.commands

import com.google.gson.Gson
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import net.dv8tion.jda.api.EmbedBuilder
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import java.awt.Color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.OffsetDateTime
import java.util.*
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
            cancel()
            val host = Bukkit.getOfflinePlayer(SettingsFeature.instance.data!!.getString("game.host"))
            var embed = EmbedBuilder()
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            val scenarios = SettingsFeature.instance.data!!.getStringList("matchpost.scenarios")
            val fr = (System.currentTimeMillis() / 1000L) + (900000L) / 1000L
            val tweet = JavaPlugin.getPlugin(Kraftwerk::class.java).twitterInstance.updateStatus(
                "\uD83E\uDDC3 applejuice | NA\n" +
                        "\n" +
                        "\uD83D\uDC65 ${SettingsFeature.instance.data!!.getString("matchpost.team")} - \uD83D\uDD79 ${scenarios.joinToString(", ")}\n" +
                        "\n" +
                        "⏰ Opening at: ${opening} UTC (in 15 minutes) | time.is/UTC\n" +
                        "⌨ Version: 1.8.x | IP: applejuice.games"
            )
            embed.addField("Teams", SettingsFeature.instance.data!!.getString("matchpost.team"), false)
            embed.addField("Scenarios", scenarios.joinToString(", "), false)
            var flag = ":flag_ca:"
            embed.addField("IP", "$flag `applejuice.games` (1.8.x)", false)
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Matchpost posted on discord & twitter! View the tweet here: &b&uhttps://twitter.com/applejuiceuhc/status/${tweet.id}"))
            embed.addField("Opening", "<t:${fr}:t> (<t:${fr}:R>)", false)
            embed.addField("Matchpost", "[uhc.gg](https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}) | [Twitter](https://twitter.com/applejuiceuhc/status/${tweet.id})", false)
            Discord.instance!!.getTextChannelById(1129309971327221760)!!.sendMessage("<@&1129405126889713692> (Use `/togglematches` to toggle matchpost alerts)").queue()
            Discord.instance!!.getTextChannelById(1129309971327221760)!!.sendMessageEmbeds(embed.build()).queue()
            embed = EmbedBuilder()
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            embed.addField("Pre-whitelists are on!", "You are now allowed to use the command `/wl` to request to pre-whitelist yourself in the server!", false)
            Discord.instance!!.getTextChannelById(1129308837187436574)!!.sendMessageEmbeds(embed.build()).queue()
            SettingsFeature.instance.data!!.set("whitelist.requests", true)
            SettingsFeature.instance.data!!.set("matchpost.posted", true)
            SettingsFeature.instance.saveData()
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
                time = 360
            } else {
                time = 180
            }
            val host = Bukkit.getOfflinePlayer(SettingsFeature.instance.data!!.getString("game.host"))
            val embed = EmbedBuilder()
            embed.addField("Matchpost", "https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}", false)
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            embed.addField("Game Open!", "The game is now open at :beverage_box: `applejuice.games`.", false)
            Discord.instance!!.getTextChannelById(1129309971327221760)!!.sendMessageEmbeds(embed.build()).queue()
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} The whitelist has been turned off automatically @ &c${opening}&7."))
            cancel()
            Opening(time).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            SettingsFeature.instance.data!!.set("whitelist.requests", false)
            SettingsFeature.instance.data!!.set("matchpost.opens", null)
            SettingsFeature.instance.saveData()
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("opened_matches")) {
                val filter = Filters.eq("id", SettingsFeature.instance.data!!.getInt("matchpost.id"))
                val document = Document("id", SettingsFeature.instance.data!!.getInt("matchpost.id"))
                    .append("server", SettingsFeature.instance.data!!.getString("matchpost.server"))
                    .append("title", SettingsFeature.instance.data!!.getString("matchpost.host"))
                    .append("teams", SettingsFeature.instance.data!!.getString("matchpost.team"))
                    .append("scenarios", SettingsFeature.instance.data!!.get("matchpost.scenarios") as List<*>)
                    .append("whitelist", SettingsFeature.instance.data!!.getBoolean("whitelist.enabled"))
                    .append("pvp", false)
                    .append("needsDelete", false)

                this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
            }
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
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
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
        var server: String? = null
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
                } else if (map["teams"] as String == "market") {
                    teamsGame = true
                    team = "Auctions"
                }
                id = map["id"] as Double
                scenarios = map["scenarios"] as List<*>
                opening = "${(map["opens"] as String)[11]}${(map["opens"] as String)[12]}:${(map["opens"] as String)[14]}${(map["opens"] as String)[15]}"
                server = if ((map["address"] as String) == "na2.applejuice.games") {
                    "uhc2"
                } else if ((map["address"] as String) == "na1.applejuice.games") {
                    "uhc1"
                } else {
                    "other"
                }
                try {
                    with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("upcoming_matches")) {
                        val filter = Filters.eq("id", id)
                        val document = Document("id", id)
                        document.append("host", host)
                        document.append("hostingName", if (map["hostingName"] != null) {
                            map["hostingName"] as String
                        } else {
                            map["author"] as String
                        })
                        document.append("teams", team)
                        document.append("scenarios", scenarios)
                        document.append("friendlyOpening", opening)
                        document.append("opening", Date(OffsetDateTime.parse(map["opens"] as String).toInstant().toEpochMilli()))
                        document.append("border", (map["mapSize"] as Double).toInt())
                        this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                        Chat.sendMessage(sender, "${Chat.dash} Successfully submitted your matchpost to the upcoming matchpost database.")
                    }
                } catch (e: MongoException) {
                    e.printStackTrace()
                    Chat.sendMessage(sender, "${Chat.prefix} An error occurred while submitting your matchpost to the upcoming matchpost database.")
                }
                for (scenario in ScenarioHandler.getActiveScenarios()){
                    scenario.toggle()
                }
                for (scenario in scenarios) {
                    scenarioList.add((scenario as String).lowercase().replace(" ", ""))
                    try { ScenarioHandler.getScenario(scenario.lowercase().replace(" ", ""))!!.toggle()
                    } catch (_: Exception) {}
                }
            }
        }
        SettingsFeature.instance.data!!.set("matchpost.team", team)
        SettingsFeature.instance.data!!.set("matchpost.teamsGame", teamsGame)
        SettingsFeature.instance.data!!.set("matchpost.host", host)
        SettingsFeature.instance.data!!.set("matchpost.id", id.toInt())
        SettingsFeature.instance.data!!.set("matchpost.scenarioIds", scenarioList)
        SettingsFeature.instance.data!!.set("matchpost.scenarios", scenarios)
        SettingsFeature.instance.data!!.set("matchpost.opens", opening)
        SettingsFeature.instance.data!!.set("matchpost.server", server)
        ScheduleOpening(opening).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, (5 * 20).toLong())
        ScheduleBroadcast(opening).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 300L)
        Chat.sendMessage(sender, "${Chat.prefix} Set the matchpost to &fhttps://hosts.uhc.gg/m/${id.toInt()}")
        Chat.sendMessage(sender, "${Chat.prefix} The server will now begin to check when the matchpost opens.")
        SettingsFeature.instance.saveData()
        return true
    }

}