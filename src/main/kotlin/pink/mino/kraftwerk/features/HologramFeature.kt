package pink.mino.kraftwerk.features

import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class HologramFeature {
    companion object {
        val instance = HologramFeature()
    }
    lateinit var gamesPlayed: Hologram
    lateinit var wins: Hologram
    lateinit var kills: Hologram
    lateinit var diamondsMined: Hologram

    fun setup() {
        for (hologram in HologramsAPI.getHolograms(JavaPlugin.getPlugin(Kraftwerk::class.java))) {
            hologram.delete()
        }
        gamesPlayed = HologramsAPI.createHologram(JavaPlugin.getPlugin(Kraftwerk::class.java), Location(Bukkit.getWorld("Spawn"), -230.5, 101.0, -131.5))
        wins = HologramsAPI.createHologram(JavaPlugin.getPlugin(Kraftwerk::class.java), Location(Bukkit.getWorld("Spawn"), -230.5, 101.0, -149.5))
        kills = HologramsAPI.createHologram(JavaPlugin.getPlugin(Kraftwerk::class.java), Location(Bukkit.getWorld("Spawn"), -212.5, 101.0, -149.5))
        diamondsMined = HologramsAPI.createHologram(JavaPlugin.getPlugin(Kraftwerk::class.java), Location(Bukkit.getWorld("Spawn"), -212.5, 101.0, -131.5))

        gamesPlayed.appendTextLine(Chat.colored("&c&lGames Played"))
        gamesPlayed.appendTextLine(Chat.guiLine)

        wins.appendTextLine(Chat.colored("&c&lWins"))
        wins.appendTextLine(Chat.guiLine)

        kills.appendTextLine(Chat.colored("&c&lKills"))
        kills.appendTextLine(Chat.guiLine)

        diamondsMined.appendTextLine(Chat.colored("&c&lDiamonds Mined"))
        diamondsMined.appendTextLine(Chat.guiLine)
    }

    fun update() {
        setup()
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).topWins!!.isNotEmpty()) {
            for ((index, player) in JavaPlugin.getPlugin(Kraftwerk::class.java).topWins!!.withIndex()) {
                wins.appendTextLine(Chat.colored("&e${index + 1}. &f${player.player.name} &8- &c${player.wins}"))
            }
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).topKills!!.isNotEmpty()) {
            for ((index, player) in JavaPlugin.getPlugin(Kraftwerk::class.java).topKills!!.withIndex()) {
                kills.appendTextLine(Chat.colored("&e${index + 1}. &f${player.player.name} &8- &c${player.kills}"))
            }
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).topGamesPlayed!!.isNotEmpty()) {
            for ((index, player) in JavaPlugin.getPlugin(Kraftwerk::class.java).topGamesPlayed!!.withIndex()) {
                gamesPlayed.appendTextLine(Chat.colored("&e${index + 1}. &f${player.player.name} &8- &c${player.gamesPlayed}"))
            }
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).topDiamondsMined!!.isNotEmpty()) {
            for ((index, player) in JavaPlugin.getPlugin(Kraftwerk::class.java).topDiamondsMined!!.withIndex()) {
                diamondsMined.appendTextLine(Chat.colored("&e${index + 1}. &f${player.player.name} &8- &c${player.diamondsMined}"))
            }
        }
        gamesPlayed.appendTextLine(Chat.guiLine)
        wins.appendTextLine(Chat.guiLine)
        kills.appendTextLine(Chat.guiLine)
        diamondsMined.appendTextLine(Chat.guiLine)
    }

}