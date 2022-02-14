package pink.mino.kraftwerk.utils

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk

class Stats {
    companion object {
        fun checkPlayer(p: Player) {
            var statement = "SELECT (uuid) from stats WHERE uuid = '${p.uniqueId}'"
            val result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            if (!result.isBeforeFirst) {
                statement = "INSERT INTO stats (uuid) VALUES ('${p.uniqueId}')"
                with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                    createStatement().execute(statement)
                }
            }
        }

        fun addWin(p: Player) {
            checkPlayer(p)
            var statement = "SELECT (wins) from stats WHERE uuid = '${p.uniqueId}'"
            val result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            result.next()
            val wins = result.getInt("wins")
            statement = "UPDATE stats SET wins = ${wins + 1} where uuid = '${p.uniqueId}'"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
        }

        fun addKill(p: Player) {
            checkPlayer(p)
            var statement = "SELECT (kills) from stats WHERE uuid = '${p.uniqueId}'"
            val result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            result.next()
            val kills = result.getInt("kills")
            statement = "UPDATE stats SET wins = ${kills + 1} where uuid = '${p.uniqueId}'"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
        }

        fun addDeath(p: Player) {
            checkPlayer(p)
            var statement = "SELECT (deaths) from stats WHERE uuid = '${p.uniqueId}'"
            val result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            result.next()
            val deaths = result.getInt("deaths")
            statement = "UPDATE stats SET wins = ${deaths + 1} where uuid = '${p.uniqueId}'"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
        }

        fun addGamesPlayed(p: Player) {
            checkPlayer(p)
            var statement = "SELECT (games_played) from stats WHERE uuid = '${p.uniqueId}'"
            val result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            result.next()
            val gamesPlayed = result.getInt("games_played")
            statement = "UPDATE stats SET wins = ${gamesPlayed + 1} where uuid = '${p.uniqueId}'"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
        }
    }
}