package pink.mino.kraftwerk.utils

import me.lucko.helper.promise.Promise
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.sql.SQLException
import java.util.*

val INSERT: String = "INSERT INTO stats VALUES(?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE uuid=?"

/*
class UpdateLeaderboards : BukkitRunnable() {
    var timer = 1
    var firstTime = true
    override fun run() {
        timer -= 1
        if (timer == 0) {
            val plugin = JavaPlugin.getPlugin(Kraftwerk::class.java)
            if (GameState.currentState != GameState.INGAME) {
                for (hologram in HologramsAPI.getHolograms(plugin)) {
                    hologram.delete()
                }

                val gamesPlayed =
                    HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -230.5, 101.0, -131.5))
                val wins =
                    HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -230.5, 101.0, -149.5))
                val kills =
                    HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -212.5, 101.0, -149.5))
                val diamondsMined =
                    HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -212.5, 101.0, -131.5))

                gamesPlayed.appendTextLine(Chat.colored("&c&lGames Played"))
                gamesPlayed.appendTextLine(Chat.guiLine)
                wins.appendTextLine(Chat.colored("&c&lWins"))
                wins.appendTextLine(Chat.guiLine)
                kills.appendTextLine(Chat.colored("&c&lKills"))
                kills.appendTextLine(Chat.guiLine)
                diamondsMined.appendTextLine(Chat.colored("&c&lDiamonds Mined"))
                diamondsMined.appendTextLine(Chat.guiLine)


                if (firstTime) {
                    Schedulers.async().run {
                        StatsHandler.getTopValues("games_played")
                        StatsHandler.getTopValues("kills")
                        StatsHandler.getTopValues("diamonds_mined")
                        StatsHandler.getTopValues("wins")
                    }
                    firstTime = false
                    print("Retrieved top players for the first time.")
                } else {
                    Promise.start()
                        .thenApplyAsync { StatsHandler.getTopValues("games_played") }
                        .thenAcceptSync {
                            for ((index, statsPlayer) in it.withIndex()) {
                                gamesPlayed.appendTextLine(Chat.colored("&e#${index + 1} &f${statsPlayer.player.name} &8- &b${statsPlayer.gamesPlayed}"))
                            }
                            gamesPlayed.appendTextLine(Chat.guiLine)
                        }

                    Promise.start()
                        .thenApplyAsync { StatsHandler.getTopValues("wins") }
                        .thenAcceptSync {
                            for ((index, statsPlayer) in it.withIndex()) {
                                wins.appendTextLine(Chat.colored("&e#${index + 1} &f${statsPlayer.player.name} &8- &b${statsPlayer.wins}"))
                            }
                            wins.appendTextLine(Chat.guiLine)
                        }

                    Promise.start()
                        .thenApplyAsync { StatsHandler.getTopValues("kills") }
                        .thenAcceptSync {
                            for ((index, statsPlayer) in it.withIndex()) {
                                kills.appendTextLine(Chat.colored("&e#${index + 1} &f${statsPlayer.player.name} &8- &b${statsPlayer.kills}"))
                            }
                            kills.appendTextLine(Chat.guiLine)
                        }

                    Promise.start()
                        .thenApplyAsync { StatsHandler.getTopValues("diamonds_mined") }
                        .thenAcceptSync {
                            for ((index, statsPlayer) in it.withIndex()) {
                                diamondsMined.appendTextLine(Chat.colored("&e#${index + 1} &f${statsPlayer.player.name} &8- &b${statsPlayer.diamondsMined}"))
                            }
                            diamondsMined.appendTextLine(Chat.guiLine)
                        }
                    print("Updated leaderboards on the server.")
                }
            }
            timer = 20
        }

    }
}*/


class StatsPlayer(val player: OfflinePlayer) : Listener {
    var diamondsMined = 0
    var ironMined = 0
    var goldMined = 0

    var gamesPlayed = 0
    var kills = 0
    var wins = 0
    var deaths = 0

    var gapplesEaten = 0
    var timesCrafted = 0
    var timesEnchanted = 0

    fun load(value: String) = try {
        with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
            val select = this@with.prepareStatement("SELECT $value from stats where uuid = ?")
            select.setString(1, player.uniqueId.toString())
            val insert = this@with.prepareStatement(INSERT)
            insert.setString(1, player.uniqueId.toString())
            insert.setString(2, player.uniqueId.toString())
            insert.execute()
            val result = select.executeQuery()
            if (result.next()) {
                when (value) {
                    "kills" -> {
                        this@StatsPlayer.kills = result.getInt(value)
                    }
                    "wins" -> {
                        this@StatsPlayer.wins = result.getInt(value)
                    }
                    "deaths" -> {
                        this@StatsPlayer.deaths = result.getInt(value)
                    }
                    "games_played" -> {
                        this@StatsPlayer.gamesPlayed = result.getInt(value)
                    }
                    "diamonds_mined" -> {
                        this@StatsPlayer.diamondsMined = result.getInt(value)
                    }
                    "gold_mined" -> {
                        this@StatsPlayer.goldMined = result.getInt(value)
                    }
                    "iron_mined" -> {
                        this@StatsPlayer.ironMined = result.getInt(value)
                    }
                    "gapples_eaten" -> {
                        this@StatsPlayer.gapplesEaten = result.getInt(value)
                    }
                    "times_crafted" -> {
                        this@StatsPlayer.timesCrafted = result.getInt(value)
                    }
                    "times_enchanted" -> {
                        this@StatsPlayer.timesEnchanted = result.getInt(value)
                    }
                }
            }
            result.close()
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }

    fun saveAll() {
        this.save("kills")
        this.save("wins")
        this.save("deaths")
        this.save("games_played")
        this.save("diamonds_mined")
        this.save("gold_mined")
        this.save("iron_mined")
        this.save("gapples_eaten")
        this.save("times_crafted")
        this.save("times_enchanted")
    }

    fun save(obj: String) = try {
        with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
            val save = this@with.prepareStatement("UPDATE stats SET $obj=? WHERE uuid=?")
            var value = 0
            when (obj) {
                "kills" -> {
                    value = this@StatsPlayer.kills
                }
                "wins" -> {
                    value = this@StatsPlayer.wins
                }
                "deaths" -> {
                    value = this@StatsPlayer.deaths
                }
                "games_played" -> {
                    value = this@StatsPlayer.gamesPlayed
                }
                "diamonds_mined" -> {
                    value = this@StatsPlayer.diamondsMined
                }
                "gold_mined" -> {
                    value = this@StatsPlayer.goldMined
                }
                "iron_mined" -> {
                    value = this@StatsPlayer.ironMined
                }
                "gapples_eaten" -> {
                    value = this@StatsPlayer.gapplesEaten
                }
                "times_crafted" -> {
                    value = this@StatsPlayer.timesCrafted
                }
                "times_enchanted" -> {
                    value = this@StatsPlayer.timesEnchanted
                }
            }
            save.setInt(1, value)
            save.setString(2, player.uniqueId.toString())
            save.execute()
            print("Set $obj for ${player.name} to $value")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }

    fun add(obj: String, adder: Int = 1) {
        when (obj) {
            "kills" -> {
                this.kills += adder
            }
            "wins" -> {
                this.wins += adder
            }
            "deaths" -> {
                this.deaths += adder
            }
            "games_played" -> {
                this.gamesPlayed += adder
            }
            "diamonds_mined" -> {
                this.diamondsMined += adder
            }
            "gold_mined" -> {
                this.goldMined += adder
            }
            "iron_mined" -> {
                this.ironMined += adder
            }
            "gapples_eaten" -> {
                this.gapplesEaten += adder
            }
            "times_crafted" -> {
                this.timesCrafted += adder
            }
            "times_enchanted" -> {
                this.timesEnchanted += adder
            }
        }
    }
}

class StatsHandler : Listener {
    companion object {
        val statsPlayers: HashMap<UUID, StatsPlayer> = HashMap()
        fun addStatsPlayer(player: Player) {
            if (statsPlayers[player.uniqueId] == null) {
                statsPlayers[player.uniqueId] = StatsPlayer(player)
                statsPlayers[player.uniqueId]!!.load("kills")
                statsPlayers[player.uniqueId]!!.load("wins")
                statsPlayers[player.uniqueId]!!.load("deaths")
                statsPlayers[player.uniqueId]!!.load("games_played")
                statsPlayers[player.uniqueId]!!.load("diamonds_mined")
                statsPlayers[player.uniqueId]!!.load("gold_mined")
                statsPlayers[player.uniqueId]!!.load("iron_mined")
                statsPlayers[player.uniqueId]!!.load("gapples_eaten")
                statsPlayers[player.uniqueId]!!.load("times_crafted")
                statsPlayers[player.uniqueId]!!.load("times_enchanted")
                print("Loaded stats for ${player.name}.")
            }
        }
        fun getStatsPlayer(player: OfflinePlayer) : StatsPlayer {
            if (statsPlayers[player.uniqueId] == null) {
                statsPlayers[player.uniqueId] = StatsPlayer(player)
                statsPlayers[player.uniqueId]!!.load("kills")
                statsPlayers[player.uniqueId]!!.load("wins")
                statsPlayers[player.uniqueId]!!.load("deaths")
                statsPlayers[player.uniqueId]!!.load("games_played")
                statsPlayers[player.uniqueId]!!.load("diamonds_mined")
                statsPlayers[player.uniqueId]!!.load("gold_mined")
                statsPlayers[player.uniqueId]!!.load("iron_mined")
                statsPlayers[player.uniqueId]!!.load("gapples_eaten")
                statsPlayers[player.uniqueId]!!.load("times_crafted")
                statsPlayers[player.uniqueId]!!.load("times_enchanted")
                print("Loaded stats for ${player.name}")
            }
            return statsPlayers[player.uniqueId]!!
        }

        fun getTopValues(value: String): ArrayList<StatsPlayer> {
            val top: ArrayList<StatsPlayer> = arrayListOf()
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                val statement = this@with.prepareStatement("SELECT uuid FROM stats ORDER BY $value DESC LIMIT 10")
                val result = statement.executeQuery()
                while (result.next()) {
                    val player = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("uuid")))
                    Promise.start()
                        .thenApplyAsync {
                            getStatsPlayer(player)
                        }
                        .thenApplySync {
                            top.add(it)
                        }
                }
                return top
            }
        }
    }
}