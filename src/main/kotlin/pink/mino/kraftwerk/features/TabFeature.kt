package pink.mino.kraftwerk.features

import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond
import me.lucko.spark.api.statistic.types.DoubleStatistic
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat


class TabFeature : BukkitRunnable() {
    fun checkTps(tps: Double): String {
        if (tps > 19.0) {
            return "§a" + tps.toString()
        } else if (tps < 18.0) {
            return "§e" + tps.toString()
        } else if (tps < 16.0) {
            return "§c" + tps.toString()
        } else if (tps < 10.0) {
            return "§4" + tps.toString()
        }
        return "&8" + tps.toString()
    }

    fun checkPing(ping: Int): String {
        return if (ping < 50) {
            "§a" + ping.toString()
        } else if (ping < 100) {
            "§e" + ping.toString()
        } else if (ping < 200) {
            "§c" + ping.toString()
        } else if (ping < 500) {
            "§4" + ping.toString()
        } else {
            "§8" + ping.toString()
        }
    }

    fun scenarioTextWrap(text: String, width: Int): ArrayList<String> {
        val words = text.split(" ")
        val lines = ArrayList<String>()
        var currentLine = ""
        for (word in words) {
            if (currentLine.length + word.length + 1 > width) {
                lines.add(Chat.colored("&f${currentLine}"))
                currentLine = "&f$word "
            } else {
                currentLine += "&f$word "
            }
        }
        lines.add(Chat.colored("&f${currentLine}"))
        return lines
    }

    fun sendTablist(p: Player) {
        val craftplayer = p as CraftPlayer
        val connection = craftplayer.handle.playerConnection
        val tps: DoubleStatistic<TicksPerSecond>? = JavaPlugin.getPlugin(Kraftwerk::class.java).spark.tps()
        val tpsLast10Secs = tps!!.poll(TicksPerSecond.SECONDS_10)
        val scenarios = ArrayList<String>()
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            scenarios.add(scenario.name)
        }
        if (scenarios.isEmpty()) {
            scenarios.add("Vanilla+")
        }
        val header: IChatBaseComponent = if (!ConfigOptionHandler.getOption("nobranding")!!.enabled) {
            ChatSerializer.a("{\"text\": \"\n${Chat.colored(Chat.scoreboardTitle)}\n${Chat.colored("&7TPS: ${checkTps(
                Math.round(tpsLast10Secs * 100.0) / 100.0
            )} &8| &7Ping: &f${checkPing(craftplayer.handle.ping)}")}ms\n${Chat.colored(" &9/discord ")}\n\"}")
        } else {
            ChatSerializer.a("{\"text\": \"\n${Chat.colored(Chat.scoreboardTitle)}\n${Chat.colored("&7TPS: ${checkTps(
                Math.round(tpsLast10Secs * 100.0) / 100.0
            )} &8| &7Ping: &f${checkPing(craftplayer.handle.ping)}")}ms\n\"}")
        }
        var game = "${SettingsFeature.instance.data!!.getString("game.host")}'s ${SettingsFeature.instance.data!!.getString("matchpost.team")}"
        if (SettingsFeature.instance.data!!.getString("matchpost.team") == null) {
            game = "Not set"
        }
        val footer: IChatBaseComponent = if (!ConfigOptionHandler.getOption("nobranding")!!.enabled) {
            ChatSerializer.a("{\"text\": \"\n${Chat.colored(" &7Game: ${Chat.secondaryColor}${game} \n &7Scenarios: ${Chat.secondaryColor}${scenarioTextWrap(scenarios.joinToString(", "), 40).joinToString("\n")} ")}\n\"}")
        } else {
            ChatSerializer.a("{\"text\": \"\n${Chat.colored(" &7Scenarios: ${Chat.secondaryColor}${scenarioTextWrap(scenarios.joinToString(", "), 40).joinToString("\n")} ")}\n\"}")
        }
        val packet = PacketPlayOutPlayerListHeaderFooter()
        try {
            val headerField = packet.javaClass.getDeclaredField("a")
            headerField.isAccessible = true
            headerField.set(packet, header)
            headerField.isAccessible = !headerField.isAccessible
            val footerField = packet.javaClass.getDeclaredField("b")
            footerField.isAccessible = true
            footerField.set(packet, footer)
            footerField.isAccessible = !footerField.isAccessible
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
        connection.sendPacket(packet)
    }

    override fun run() {
        for (p in Bukkit.getOnlinePlayers()) {
            sendTablist(p)
        }
    }
}