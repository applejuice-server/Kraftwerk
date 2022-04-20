package pink.mino.kraftwerk.utils

import me.lucko.helper.Schedulers
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.sql.SQLException
import java.util.*

val INSERT: String = "INSERT INTO stats VALUES(?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE uuid=?"
val INSERT_HOTBAR: String = "INSERT INTO hotbar VALUES(?, 1, 2, 3, 4, 5, 6, 7, 8, 9) ON DUPLICATE KEY UPDATE uuid=?"

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

    var slot1 = 1
    var slot2 = 2
    var slot3 = 3
    var slot4 = 4
    var slot5 = 5
    var slot6 = 6
    var slot7 = 7
    var slot8 = 8
    var slot9 = 9

    fun loadHotbar() = try {
        Schedulers.async().run runnable@ {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                val insert = this@with.prepareStatement(INSERT_HOTBAR)
                insert.setString(1, player.uniqueId.toString())
                insert.setString(2, player.uniqueId.toString())
                insert.execute()
                val select = this.prepareStatement("SELECT * FROM hotbar WHERE uuid='${player.uniqueId}'")
                val result = select.executeQuery()
                if (result.next()) {
                    slot1 = result.getInt("slot1")
                    slot2 = result.getInt("slot2")
                    slot3 = result.getInt("slot3")
                    slot4 = result.getInt("slot4")
                    slot5 = result.getInt("slot5")
                    slot6 = result.getInt("slot6")
                    slot7 = result.getInt("slot7")
                    slot8 = result.getInt("slot8")
                    slot9 = result.getInt("slot9")
                }
            }
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }

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
            this.saveHotbar()
        }
    }

    fun saveHotbar() = try {
        Schedulers.async().run runnable@{
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                val save = this@with.prepareStatement("UPDATE hotbar SET slot1 = ?, slot2 = ?, slot3 = ?, slot4 = ?, slot5 = ?, slot6 = ?, slot7 = ?, slot8 = ?, slot9 = ? WHERE uuid = ?")
                save.setInt(1, slot1)
                save.setInt(2, slot2)
                save.setInt(3, slot3)
                save.setInt(4, slot4)
                save.setInt(5, slot5)
                save.setInt(6, slot6)
                save.setInt(7, slot7)
                save.setInt(8, slot8)
                save.setInt(9, slot9)
                save.setString(10, player.uniqueId.toString())
                save.execute()
            }
        }
    } catch (e: SQLException) {
        e.printStackTrace()
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
                statsPlayers[player.uniqueId]!!.loadHotbar()
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
                statsPlayers[player.uniqueId]!!.loadHotbar()
                print("Loaded stats for ${player.name}")
            }
            return statsPlayers[player.uniqueId]!!
        }
    }
}