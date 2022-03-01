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
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat
import java.awt.Color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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
        var hours = makeSureIsValid[0].toInt()
        val minutes: Int
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
        return "${hours}:${minutes}"
    }

    override fun run() {
        print("Checking if the time corresponds with the broadcast time...")
        if (SettingsFeature.instance.data!!.getString("matchpost.opens") == null) {
            cancel()
        }
        if (removeFifteenMinutes(opening) === getTime()) {
            val host = Bukkit.getPlayer(SettingsFeature.instance.data!!.getString("game.host"))
            var embed = EmbedBuilder()
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            val scenarios = SettingsFeature.instance.data!!.getStringList("matchpost.scenarios")
            val opening = (System.currentTimeMillis() / 1000L) + 15000L
            embed.addField("Opening", "<t:${opening}:t> (<t:${opening}:R>)", true)
            embed.addField("Scenarios", scenarios.joinToString(", "), true)
            embed.addField("Matchpost", "https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}", true)
            Discord.instance!!.getTextChannelById(937811305102999573)!!.sendMessage("<@&793406242013839381> (Use /togglematches to toggle matchpost alerts)").queue()
            Discord.instance!!.getTextChannelById(937811305102999573)!!.sendMessageEmbeds(embed.build()).queue()
            embed = EmbedBuilder()
            embed.setColor(Color(255, 61, 61))
            embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            embed.addField("Pre-whitelists are on!", "You are now allowed to use the command `/wl` to request to pre-whitelist yourself in the server!", false)
            Discord.instance!!.getTextChannelById(937812061948346398)!!.sendMessageEmbeds(embed.build()).queue()
            SettingsFeature.instance.data!!.set("whitelist.requests", true)
            SettingsFeature.instance.saveData()
            cancel()
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
        if (getTime() == opening) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl off")
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The whitelist has been turned off automatically @ &c${opening}&7."))
            cancel()
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
            Chat.sendMessage(sender, "&cYou must provide a valid matchpost ID.")
            return false
        }
        if (args[0].toIntOrNull() == null) {
            Chat.sendMessage(sender, "&cYou must provide a &ovalid&c matchpost ID.")
            return false
        }
        val host: String
        val id: Double
        val opening: String
        val scenarios: Any
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
                id = map["id"] as Double
                scenarios = map["scenarios"] as Any
                opening = "${(map["opens"] as String)[11]}${(map["opens"] as String)[12]}:${(map["opens"] as String)[14]}${(map["opens"] as String)[15]}"
            }
        }
        SettingsFeature.instance.data!!.set("matchpost.host", host)
        SettingsFeature.instance.data!!.set("matchpost.id", id.toInt())
        SettingsFeature.instance.data!!.set("matchpost.scenarios", scenarios)
        SettingsFeature.instance.data!!.set("matchpost.opens", opening)
        ScheduleOpening(opening).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 300L)
        Discord.instance!!.presence.activity = Activity.playing(host)
        Chat.sendMessage(sender, "${Chat.prefix} Set the matchpost to &chttps://hosts.uhc.gg/m/${id.toInt()}")
        SettingsFeature.instance.saveData()
        return true
    }

}