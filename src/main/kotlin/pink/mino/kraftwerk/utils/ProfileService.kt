package pink.mino.kraftwerk.utils

import com.google.common.collect.Iterables
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.Profile
import me.lucko.helper.profiles.ProfileRepository
import me.lucko.helper.profiles.plugin.ImmutableProfile
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Cache
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Caffeine
import me.lucko.helper.promise.Promise
import me.lucko.helper.utils.Log
import me.lucko.helper.utils.UndashedUuids
import org.bson.Document
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class ProfileService : ProfileRepository {
    private val profilesMap: Cache<UUID, Profile> = Caffeine.newBuilder()
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
                val profile = Profile.create(event.player)
                updateCache(profile)
                Schedulers.async().run { saveProfile(profile) }
            }
        Log.info("[Profile] Now monitoring for profile data.")
    }

    fun saveProfile(profile: Profile) {
        with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("players")) {
            val filter = Filters.eq("uuid", profile.uniqueId)
            val document = Document("uuid", profile.uniqueId)
                .append("name", profile.name.get())
                .append("lastLogin", Timestamp(profile.timestamp))
            this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
        }
    }

    override fun getProfile(uniqueId: UUID): Profile {
        Objects.requireNonNull(uniqueId, "uniqueId")
        var profile: Profile? = this.profilesMap.getIfPresent(uniqueId)
        if (profile == null) {
            profile = ImmutableProfile(uniqueId, null, 0)
        }
        return profile
    }

    override fun getProfile(name: String): Optional<Profile> {
        Objects.requireNonNull(name, "name")
        for (profile in this.profilesMap.asMap().values) {
            if (profile.name.isPresent && profile.name.get().equals(name, ignoreCase = true)) {
                return Optional.of(profile)
            }
        }
        return Optional.empty()
    }

    private fun updateCache(profile: Profile) {
        val existing: Profile? = this.profilesMap.getIfPresent(profile.uniqueId)
        if (existing == null || existing.timestamp < profile.timestamp) {
            this.profilesMap.put(profile.uniqueId, profile)
        }
    }

    override fun getKnownProfiles(): Collection<Profile> {
        return Collections.unmodifiableCollection(this.profilesMap.asMap().values)
    }

    override fun lookupProfile(uniqueId: UUID): Promise<Profile> {
        Objects.requireNonNull(uniqueId, "uniqueId")
        val profile = getProfile(uniqueId)
        if (profile.name.isPresent) {
            return Promise.completed(profile)
        }

        return Schedulers.async().supply {
            try {
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("players")) {
                    val filter = Filters.eq("uuid", uniqueId)
                    val document = this.find(filter).first()
                    val p = ImmutableProfile(uniqueId, document!!["name"] as String,
                        (document["lastLogin"] as Date).time
                    )
                    updateCache(p)
                    return@supply p
                }
            } catch (e: MongoException) {
                e.printStackTrace()
            }
            return@supply null
        }
    }

    override fun lookupProfile(name: String): Promise<Optional<Profile>> {
        Objects.requireNonNull(name, "name")

        val profile = getProfile(name)
        if (profile.isPresent) {
            return Promise.completed(profile)
        }

        return Schedulers.async().supply {
            try {
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("players")) {
                    val filter = Filters.eq("name", name)
                    val document = this.find(filter).first()
                    val p = ImmutableProfile(UUID.fromString(document!!["uuid"] as String), name, document["lastLogin"] as Long)
                    updateCache(p)
                    return@supply Optional.of(p)
                }
            } catch (e: MongoException) {
                e.printStackTrace()
            }
            return@supply null
        }
    }

    override fun lookupKnownProfiles(): Promise<Collection<Profile>> {
        return Schedulers.async().supply {
            val ret = ArrayList<Profile>()
            try {
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("players")) {
                    val documents = this.find().iterator()
                    while (documents.hasNext()) {
                        val document = documents.next()
                        val p = ImmutableProfile(UUID.fromString(document["uuid"] as String), document["name"] as String, document["lastLogin"] as Long)
                        updateCache(p)
                        ret.add(p)
                    }
                }
            } catch (e: MongoException) {
                e.printStackTrace()
            }
            return@supply ret
        }
    }

    override fun lookupProfiles(uniqueIds: MutableIterable<UUID>): Promise<MutableMap<UUID, Profile>> {
        val toFind: MutableSet<UUID> = HashSet()
        Iterables.addAll(toFind, uniqueIds)
        val ret: HashMap<UUID, Profile> = HashMap<UUID, Profile>()
        val iterator = toFind.iterator()
        while (iterator.hasNext()) {
            val u = iterator.next()
            val profile = getProfile(u)
            if (profile.name.isPresent) {
                ret[u] = profile
                iterator.remove()
            }
        }
        val sb = StringBuilder("(")
        var first = true
        for (uniqueId in toFind) {
            if (uniqueId == null) {
                continue
            }
            if (!first) {
                sb.append(", ")
            }
            sb.append("UNHEX('").append(UndashedUuids.toString(uniqueId)).append("')")
            first = false
        }

        if (first) {
            return Promise.completed(ret)
        }
        sb.append(")")
        return Schedulers.async().supply {
            try {
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("players")) {
                    val filter = Filters.eq("uuid", sb.toString())
                    val documents = this.find(filter).iterator()
                    while (documents.hasNext()) {
                        val document = documents.next()
                        val uuid = UUID.fromString(document["uuid"] as String)
                        val p = ImmutableProfile(uuid, document["name"] as String, document["lastLogin"] as Long)
                        updateCache(p)
                        ret[uuid] = p
                    }
                }
            } catch (e: MongoException) {
                e.printStackTrace()
            }
            return@supply ret
        }
    }

    override fun lookupProfilesByName(names: MutableIterable<String>): Promise<MutableMap<String, Profile>> {
        val toFind: MutableSet<String> = HashSet()
        Iterables.addAll(toFind, names)
        val ret: HashMap<String, Profile> = HashMap<String, Profile>()
        val iterator = toFind.iterator()
        while (iterator.hasNext()) {
            val u = iterator.next()
            val profile = getProfile(u)
            if (profile.isPresent) {
                ret[u] = profile.get()
                iterator.remove()
            }
        }
        val sb = java.lang.StringBuilder("(")
        var first = true
        for (name in names) {
            if (name == null || !isValidMcUsername(name)) {
                continue
            }
            if (!first) {
                sb.append(", ")
            }
            sb.append("'").append(name).append("'")
            first = false
        }

        if (first) {
            return Promise.completed(ret)
        }
        sb.append(")")
        return Schedulers.async().supply {
            try {
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("stats")) {
                    val filter = Filters.eq("name", toFind)
                    val documents = this.find(filter).iterator()
                    while (documents.hasNext()) {
                        val document = documents.next()
                        val p = ImmutableProfile(UUID.fromString(document["uuid"] as String), document["name"] as String, document["lastLogin"] as Long)
                        updateCache(p)
                        ret[p.name.get()] = p
                    }
                }
            } catch (e: MongoException) {
                e.printStackTrace()
            }
            return@supply ret
        }
    }

}