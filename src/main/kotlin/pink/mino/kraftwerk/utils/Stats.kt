package pink.mino.kraftwerk.utils

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Cache
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Caffeine
import me.lucko.helper.utils.Log
import org.bson.Document
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.util.*
import java.util.concurrent.TimeUnit
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
}

class StatsHandler : Listener {
    private val statsPlayerMap: Cache<UUID, StatsPlayer> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(6, TimeUnit.HOURS)
        .build()
    init {
        Events.subscribe(PlayerLoginEvent::class.java, EventPriority.MONITOR)
            .filter { it.result == PlayerLoginEvent.Result.ALLOWED }
            .handler { event ->
                val statsPlayer = StatsPlayer(event.player)
                updateCache(statsPlayer)
                Schedulers.async().run { loadPlayerData(statsPlayer) }
            }
        Events.subscribe(PlayerQuitEvent::class.java, EventPriority.MONITOR)
            .handler { event ->
                val statsPlayer = statsPlayerMap.getIfPresent(event.player.uniqueId)
                if (statsPlayer != null) {
                    Schedulers.async().run { savePlayerData(statsPlayer) }
                }
            }
    }

    fun savePlayerData(statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("stats")) {
                val filter = Filters.eq("uuid", statsPlayer.player.uniqueId)
                val document = Document("uuid", statsPlayer.player.uniqueId)
                    .append("diamondsMined", statsPlayer.diamondsMined)
                    .append("ironMined", statsPlayer.ironMined)
                    .append("goldMined", statsPlayer.goldMined)

                    .append("gamesPlayed", statsPlayer.gamesPlayed)
                    .append("kills", statsPlayer.kills)
                    .append("wins", statsPlayer.wins)
                    .append("deaths", statsPlayer.deaths)

                    .append("gapplesEaten", statsPlayer.gapplesEaten)
                    .append("timesCrafted", statsPlayer.timesCrafted)
                    .append("timesEnchanted", statsPlayer.timesEnchanted)
                this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                Log.info("Saved stats for ${statsPlayer.player.name}")
            }
        } catch (e: MongoException) {
            Log.severe("Error saving player data for ${statsPlayer.player.name}", e)
        }
    }

    fun getStatsPlayer(player: Player): StatsPlayer? {
        Objects.requireNonNull(player, "player")
        return statsPlayerMap.getIfPresent(player.uniqueId)
    }

    fun loadPlayerData(statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("stats")) {
                val playerData = find(Filters.eq("uuid", statsPlayer.player.uniqueId)).first()
                if (playerData != null) {
                    statsPlayer.diamondsMined = playerData.getInteger("diamondsMined")
                    statsPlayer.ironMined = playerData.getInteger("ironMined")
                    statsPlayer.goldMined = playerData.getInteger("goldMined")

                    statsPlayer.deaths = playerData.getInteger("deaths")
                    statsPlayer.kills = playerData.getInteger("kills")
                    statsPlayer.gamesPlayed = playerData.getInteger("gamesPlayed")
                    statsPlayer.wins = playerData.getInteger("wins")

                    statsPlayer.gapplesEaten = playerData.getInteger("gapplesEaten")
                    statsPlayer.timesCrafted = playerData.getInteger("timesCrafted")
                    statsPlayer.timesEnchanted = playerData.getInteger("timesEnchanted")
                    Log.info("Loaded stats for ${statsPlayer.player.name}.")
                } else {
                    Log.info("Could not load stats for ${statsPlayer.player.name}.")
                }
            }
        } catch (e: MongoException) {
            e.printStackTrace()
        }
    }

    private fun updateCache(statsPlayer: StatsPlayer) {
        val existing: StatsPlayer? = this.statsPlayerMap.getIfPresent(statsPlayer.player.uniqueId)
        if (existing == null) this.statsPlayerMap.put(statsPlayer.player.uniqueId, statsPlayer)
    }
}