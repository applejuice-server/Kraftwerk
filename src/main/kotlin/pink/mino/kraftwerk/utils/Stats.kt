package pink.mino.kraftwerk.utils

import me.lucko.helper.Schedulers
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.HologramFeature
import java.sql.SQLException
import java.util.*

val INSERT: String = "INSERT INTO stats VALUES(?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE uuid=?"

class UpdateStats : BukkitRunnable() {
    var timer = 0
    override fun run() {
        if (timer == 0) {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).topGamesPlayed = arrayListOf()
                JavaPlugin.getPlugin(Kraftwerk::class.java).topDiamondsMined = arrayListOf()
                JavaPlugin.getPlugin(Kraftwerk::class.java).topKills = arrayListOf()
                JavaPlugin.getPlugin(Kraftwerk::class.java).topWins = arrayListOf()

                val values = arrayListOf("games_played", "diamonds_mined", "wins", "kills")
                with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                    for (value in values) {
                        val statement = this@with.prepareStatement("SELECT uuid FROM stats ORDER BY $value DESC LIMIT 10")
                        val result = statement.executeQuery()
                        var index = 0

                        while (result.next()) {
                            index++
                            val player = StatsPlayer(Bukkit.getOfflinePlayer(UUID.fromString(result.getString(1))))
                            when (value) {
                                "games_played" -> {
                                    JavaPlugin.getPlugin(Kraftwerk::class.java).topGamesPlayed!!.add(player)
                                }
                                "diamonds_mined" -> {
                                    JavaPlugin.getPlugin(Kraftwerk::class.java).topDiamondsMined!!.add(player)
                                }
                                "wins" -> {
                                    JavaPlugin.getPlugin(Kraftwerk::class.java).topWins!!.add(player)
                                }
                                "kills" -> {
                                    JavaPlugin.getPlugin(Kraftwerk::class.java).topKills!!.add(player)
                                }
                            }
                        }
                    }
                }
                HologramFeature.instance.update()
                print("Updated stats.")
                timer = 10
            }
        }
        timer--
    }
}

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
        Schedulers.async().run runnable@ {
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
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }

    fun saveAll() {
        Schedulers.async().run runnable@ {
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
    }

    fun save(obj: String) = try {
        Schedulers.async().run runnable@ {
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
    }
}