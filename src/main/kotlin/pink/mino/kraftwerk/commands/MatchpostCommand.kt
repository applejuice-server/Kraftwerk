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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 360 &cWhitelist is enabled in ${Chat.dash}&f")
            } else {
                time = 180
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 180 &cWhitelist is enabled in ${Chat.dash}&f")
            }
            val host = Bukkit.getOfflinePlayer(SettingsFeature.instance.data!!.getString("game.host"))
            val embed = EmbedBuilder()
            embed.addField("Matchpost", "https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}", false)
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
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
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("upcoming_matches")) {
                val filter = Filters.eq("id", SettingsFeature.instance.data!!.getInt("matchpost.id"))
                this.deleteOne(filter)
            }
        } catch (e: MongoException) {
            Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}An error occurred deleting the old matchpost. Don't worry too much about this unless your game didn't setup.")
            e.printStackTrace()
        }
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
                        if ((map["address"] as String) == "na2.applejuice.bar") {
                            document.append("server", "uhc2")
                        } else if ((map["address"] as String) == "na1.applejuice.bar") {
                            document.append("server", "uhc1")
                        } else {
                            document.append("server", "other")
                        }
                        this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                        Chat.sendMessage(sender, "${Chat.prefix} Successfully submitted your matchpost to the upcoming matchpost database.")
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
        ScheduleOpening(opening).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, (5 * 20).toLong())
        Chat.sendMessage(sender, "${Chat.prefix} Set the matchpost to &chttps://hosts.uhc.gg/m/${id.toInt()}")
        SettingsFeature.instance.saveData()
        return true
    }

}