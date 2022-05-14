package pink.mino.kraftwerk.utils

import me.lucko.helper.Schedulers
import me.lucko.helper.promise.Promise
import me.lucko.helper.utils.Log
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.sql.SQLException
import java.util.*

val INSERT: String = "INSERT INTO stats VALUES(?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE uuid=?"

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
        if (this.kills != 0) this.save("kills")
        if (this.wins != 0) this.save("wins")
        if (this.deaths != 0) this.save("deaths")
        if (this.gamesPlayed != 0) this.save("games_played")
        if (this.diamondsMined != 0) this.save("diamonds_mined")
        if (this.goldMined != 0) this.save("gold_mined")
        if (this.ironMined != 0) this.save("iron_mined")
        if (this.gapplesEaten != 0) this.save("gapples_eaten")
        if (this.timesCrafted != 0) this.save("times_crafted")
        if (this.timesEnchanted != 0) this.save("times_enchanted")
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
                Log.info("Loaded player stats for ${player.name}.")
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
                Log.info("Loaded player stats for ${player.name}.")
            }
            return statsPlayers[player.uniqueId]!!
        }

        fun getTopValues(value: String): Promise<ArrayList<UUID>> {
            return Schedulers.async().supply {
                val topValues = ArrayList<UUID>()
                try {
                    with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                        val statement = prepareStatement("SELECT uuid FROM stats ORDER BY $value DESC LIMIT 10")
                        val result = statement.executeQuery()
                        while (result.next()) {
                            topValues.add(UUID.fromString(result.getString("uuid")))
                        }
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                return@supply topValues
            }
        }
    }
}