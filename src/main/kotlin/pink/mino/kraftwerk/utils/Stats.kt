package pink.mino.kraftwerk.utils

import me.lucko.helper.Schedulers
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.sql.SQLException
import java.util.*

val SELECT: String = "SELECT ? from stats where uuid = ?"
val INSERT: String = "INSERT INTO stats (uuid, ?) VALUES (?, ?)"
val SAVE: String = "UPDATE stats SET ?=? WHERE uuid=?"

class StatsPlayer(val player: Player) : Listener {
    var diamondsMined: Int = 0
    var ironMined: Int = 0
    var goldMined: Int = 0

    var gamesPlayed: Int = 0
    var kills: Int = 0
    var wins: Int = 0
    var deaths: Int = 0

    var gapplesEaten: Int = 0
    var timesCrafted: Int = 0
    var timesEnchanted: Int = 0

    fun load(value: String) = try {
        Schedulers.async().run runnable@ {
            JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.use { hikari ->
                val select = hikari.connection.prepareStatement(SELECT)
                select.setString(1, value)
                select.setString(2, player.uniqueId.toString())
                val result = select.executeQuery()
                if (result.next()) {
                    when (value) {
                        "kills" -> {
                            this.kills = result.getInt(1)
                        }
                        "wins" -> {
                            this.wins = result.getInt(1)
                        }
                        "deaths" -> {
                            this.deaths = result.getInt(1)
                        }
                        "games_played" -> {
                            this.gamesPlayed = result.getInt(1)
                        }
                        "diamonds_mined" -> {
                            this.diamondsMined = result.getInt(1)
                        }
                        "gold_mined" -> {
                            this.goldMined = result.getInt(1)
                        }
                        "iron_mined" -> {
                            this.ironMined = result.getInt(1)
                        }
                        "gapples_eaten" -> {
                            this.gapplesEaten = result.getInt(1)
                        }
                        "times_crafted" -> {
                            this.timesCrafted = result.getInt(1)
                        }
                        "times_enchanted" -> {
                            this.timesEnchanted = result.getInt(1)
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
            JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.use { hikari ->
                val save = hikari.connection.prepareStatement(SAVE)
                var value = 0
                when (obj) {
                    "kills" -> {
                        value = this.kills
                    }
                    "wins" -> {
                        value = this.wins
                    }
                    "deaths" -> {
                        value = this.deaths
                    }
                    "games_played" -> {
                        value = this.gamesPlayed
                    }
                    "diamonds_mined" -> {
                        value = this.diamondsMined
                    }
                    "gold_mined" -> {
                        value = this.goldMined
                    }
                    "iron_mined" -> {
                        value = this.ironMined
                    }
                    "gapples_eaten" -> {
                        value = this.gapplesEaten
                    }
                    "times_crafted" -> {
                        value = this.timesCrafted
                    }
                    "times_enchanted" -> {
                        value = this.timesEnchanted
                    }
                }
                save.setString(1, obj)
                save.setString(2, value.toString())
                save.setString(3, player.uniqueId.toString())
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
    }
}