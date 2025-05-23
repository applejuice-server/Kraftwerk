package pink.mino.kraftwerk.utils

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Cache
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Caffeine
import me.lucko.helper.promise.Promise
import me.lucko.helper.utils.Log
import org.bson.Document
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class ProfileService {
    private val profilesMap: Cache<UUID, ImmutableProfile> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(6, TimeUnit.HOURS)
        .build()


    private val MINECRAFT_USERNAME_PATTERN: Pattern = Pattern.compile("^\\w{3,16}$")
    private fun isValidMcUsername(s: String): Boolean {
        return MINECRAFT_USERNAME_PATTERN.matcher(s).matches()
    }

    init {
        Events.subscribe(PlayerLoginEvent::class.java, EventPriority.MONITOR)
            .filter { it.result == PlayerLoginEvent.Result.ALLOWED }
            .handler { event ->
                Promise.start()
                    .thenApplySync {
                        lookupProfile(event.player.uniqueId)
                    }
                    .thenAcceptAsync {
                        it.get().name = event.player.name
                        saveProfile(it.get())
                        updateCache(it.get())
                    }
            }
        Events.subscribe(PlayerQuitEvent::class.java, EventPriority.MONITOR)
            .handler { event ->
                Promise.start()
                    .thenApplySync {
                        lookupProfile(event.player.uniqueId)
                    }
                    .thenAcceptAsync {
                        it.get().name = event.player.name
                        saveProfile(it.get())
                        updateCache(it.get())
                    }
            }
        Log.info("[Profile] Now monitoring for profile data.")
    }

    private fun updateCache(profile: ImmutableProfile) {
        val existing: ImmutableProfile? = this.profilesMap.getIfPresent(profile.uniqueId)
        if (existing == null || existing.timestamp < profile.timestamp) {
            this.profilesMap.put(profile.uniqueId, profile)
        }
    }

    fun saveProfile(profile: ImmutableProfile) {
        with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("players")) {
            val filter = Filters.eq("uuid", profile.uniqueId)
            val document = Document("uuid", profile.uniqueId)
                .append("name", profile.name)
                .append("lastLogin", Timestamp(profile.timestamp))
                .append("disableRedstonePickup", profile.disableRedstonePickup)
                .append("disableLapisPickup", profile.disableLapisPickup)
                .append("projectileMessages", profile.projectileMessages)
                .append("healthType", profile.healthType)
                .append("borderPreference", profile.borderPreference)
                .append("ignored", profile.ignored)
                .append("deathMessageOnScreen", profile.deathMessageOnScreen)
                .append("xpNeeded", profile.xpNeeded)
                .append("xp", profile.xp)
                .append("level", profile.level)
                .append("chatMode", profile.chatMode)
                .append("coins", profile.coins)
                .append("specSocialSpy", profile.specSocialSpy)
                .append("selectedTag", profile.selectedTag)
                .append("unlockedTags", profile.unlockedTags)
                .append("arenaBlock", profile.arenaBlock)
                .append("alts", profile.alts)
                .append("lastKnownIp", profile.lastKnownIp)
            this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
        }
    }

    fun getProfile(uniqueId: UUID): ImmutableProfile? {
        Objects.requireNonNull(uniqueId, "uniqueId")
        return profilesMap.getIfPresent(uniqueId)
    }

    fun lookupProfile(uniqueId: UUID): Promise<ImmutableProfile> {
        Objects.requireNonNull(uniqueId, "uniqueId")
        val profile = getProfile(uniqueId)
        if (profile != null) {
            return Promise.completed(profile)
        }
        return Schedulers.async().supply {
            try {
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("players")) {
                    val filter = Filters.eq("uuid", uniqueId)
                    val document = this.find(filter).first()
                    val p: ImmutableProfile
                    if (document != null) {
                        p = ImmutableProfile(
                            uniqueId,
                            document["name"] as String,
                            (document["lastLogin"] as Date).time,
                            (document["disableRedstonePickup"] as Boolean),
                            (document["disableLapisPickup"] as Boolean),
                            (document["projectileMessages"] as String),
                            (document["healthType"] as String),
                            (document["borderPreference"] as String),
                            (document["ignored"] as ArrayList<UUID>),
                            (document["deathMessageOnScreen"] as Boolean),
                            (document["xpNeeded"] as? Double ?: 150.0),
                            (document["xp"] as? Double ?: 0.0),
                            (document["level"] as? Int ?: 1),
                            (document["chatMode"] as? String ?: "PUBLIC"),
                            (document["coins"] as? Double ?: 0.0),
                            (document["specSocialSpy"] as? Int ?: 0),
                            (document["selectedTag"] as? String),
                            (document["unlockedTags"] as? ArrayList<String>) ?: arrayListOf(),
                            (document["arenaBlock"] as? String) ?: "COBBLESTONE",
                            (document["alts"] as? ArrayList<UUID> ?: arrayListOf()),
                            document["lastKnownIp"] as String?
                        )
                    } else {
                        p = ImmutableProfile(
                            uniqueId,
                            null,
                            0
                        )
                    }
                    updateCache(p)
                    return@supply p
                }
            } catch (e: MongoException) {
                e.printStackTrace()
            }
            return@supply null
        }
    }
}