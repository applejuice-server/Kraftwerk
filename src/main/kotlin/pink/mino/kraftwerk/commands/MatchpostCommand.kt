package pink.mino.kraftwerk.commands

import com.google.gson.Gson
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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
                host = "${map["hostingName"]}'s #${(map["count"] as Double).toInt()}"
                id = map["id"] as Double
            }
        }
        SettingsFeature.instance.data!!.set("matchpost.host", host)
        SettingsFeature.instance.data!!.set("matchpost.id", id.toInt())
        Discord.instance!!.presence.activity = Activity.playing(host)
        Chat.sendMessage(sender, "${Chat.prefix} Set the matchpost to &chttps://hosts.uhc.gg/m/${id}")
        SettingsFeature.instance.saveData()
        return true
    }

}