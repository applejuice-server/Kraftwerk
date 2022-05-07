package pink.mino.kraftwerk.utils

import com.google.common.collect.Iterables
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
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class ProfileService : ProfileRepository {

    private val profilesMap: Cache<UUID, Profile> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(6, TimeUnit.HOURS)
        .build()

    private val CREATE = "CREATE TABLE IF NOT EXISTS {table} (" +
            "`uniqueid` BINARY(16) NOT NULL PRIMARY KEY, " +
            "`name` VARCHAR(16) NOT NULL, " +
            "`lastupdate` TIMESTAMP NOT NULL)"
    private val INSERT =
        "INSERT INTO {table} VALUES(UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE `name` = ?, `lastupdate` = ?"
    private val SELECT_UID = "SELECT `name`, `lastupdate` FROM {table} WHERE `uniqueid` = UNHEX(?)"
    private val SELECT_NAME =
        "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `name` = ? ORDER BY `lastupdate` DESC LIMIT 1"
    private val SELECT_ALL = "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table}"
    private val SELECT_ALL_RECENT =
        "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} ORDER BY `lastupdate` DESC LIMIT ?"
    private val SELECT_ALL_UIDS =
        "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `uniqueid` IN %s"
    private val SELECT_ALL_NAMES =
        "SELECT HEX(`uniqueid`) AS `canonicalid`, `name`, `lastupdate` FROM {table} WHERE `name` IN %s GROUP BY `name` ORDER BY `lastupdate` DESC"


    private fun preload(numEntries: Int): Int {
        var i = 0
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                val stmt = prepareStatement(replaceTableName(SELECT_ALL_RECENT))
                stmt.setInt(1, numEntries)
                stmt.executeQuery()
                val result = stmt.resultSet
                while (result.next()) {
                    val name: String = result.getString("name")
                    val lastUpdate: Timestamp = result.getTimestamp("lastupdate")
                    val uuidString: String = result.getString("canonicalid")
                    val uuid = UndashedUuids.fromString(uuidString)
                    val p = ImmutableProfile(uuid, name, lastUpdate.time)
                    updateCache(p)
                    i++
                }

            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return i
    }

    init {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection){
                val statement = createStatement()
                statement.execute(CREATE.replace("{table}", "kraftwerk_profiles"))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        Log.info("[Profiles] Preloading the most recent " + 100.toString() + " entries...")
        val start = System.currentTimeMillis()
        val found = preload(100)
        val time = System.currentTimeMillis() - start
        Log.info("[Profiles] Preloaded " + found + " profiles into the cache! - took " + time + "ms")

        Events.subscribe(PlayerLoginEvent::class.java, EventPriority.MONITOR)
            .filter { it.result == PlayerLoginEvent.Result.ALLOWED }
            .handler { event ->
                val profile = Profile.create(event.player)
                updateCache(profile)
                Schedulers.async().run { saveProfile(profile) }
            }
    }

    private fun updateCache(profile: Profile) {
        val existing: Profile? = this.profilesMap.getIfPresent(profile.uniqueId)
        if (existing == null || existing.timestamp < profile.timestamp) {
            this.profilesMap.put(profile.uniqueId, profile)
        }
    }

    private fun replaceTableName(s: String): String {
        return s.replace("{table}", "kraftwerk_profiles")
    }

    fun saveProfile(profile: Profile) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                val statement = prepareStatement(replaceTableName(INSERT))
                statement.setString(1, UndashedUuids.toString(profile.uniqueId))
                statement.setString(2, profile.name.get())
                statement.setTimestamp(3, Timestamp(profile.timestamp))
                statement.setString(4, profile.name.get())
                statement.setTimestamp(5, Timestamp(profile.timestamp))
                statement.execute()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
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
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                    val statement = prepareStatement(replaceTableName(SELECT_UID))
                    statement.setString(1, UndashedUuids.toString(uniqueId))
                    val rs = statement.executeQuery()
                    if (rs.next()) {
                        val name: String = rs.getString("name")
                        val lastUpdate: Timestamp = rs.getTimestamp("lastupdate")
                        val p = ImmutableProfile(uniqueId, name, lastUpdate.time)
                        updateCache(p)
                        return@supply p
                    }

                }
            } catch (e: SQLException) {
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
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                    val statement = prepareStatement(replaceTableName(SELECT_NAME))
                    val result = statement.executeQuery()
                    if (result.next()) {
                        val remoteName: String = result.getString("name")
                        val lastUpdate: Timestamp = result.getTimestamp("lastupdate")
                        val uuidString: String = result.getString("canonicalid")
                        val uuid = UndashedUuids.fromString(uuidString)
                        val p = ImmutableProfile(uuid, remoteName, lastUpdate.time)
                        updateCache(p)
                        return@supply Optional.of(p)
                    }

                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return@supply Optional.empty()
        }
    }

    override fun lookupKnownProfiles(): Promise<Collection<Profile>> {
        return Schedulers.async().supply {
            val ret = ArrayList<Profile>()
            try {
                with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                    val statement = prepareStatement(replaceTableName(SELECT_ALL))
                    val result = statement.executeQuery()
                    while (result.next()) {
                        val name: String = result.getString("name")
                        val lastUpdate: Timestamp = result.getTimestamp("lastupdate")
                        val uuidString: String = result.getString("canonicalid")
                        val uuid = UndashedUuids.fromString(uuidString)
                        val p = ImmutableProfile(uuid, name, lastUpdate.time)
                        updateCache(p)
                        ret.add(p)
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return@supply ret
        }
    }

    override fun lookupProfiles(uniqueIds: MutableIterable<UUID>): Promise<Map<UUID, Profile>> {
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
                with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                    val statement = prepareStatement(replaceTableName(String.format(SELECT_ALL_UIDS, sb.toString())))
                    val rs = statement.executeQuery()
                    while (rs.next()) {
                        val name: String = rs.getString("name")
                        val lastUpdate: Timestamp = rs.getTimestamp("lastupdate")
                        val uuidString: String = rs.getString("canonicalid")
                        val uuid = UndashedUuids.fromString(uuidString)
                        val p = ImmutableProfile(uuid, name, lastUpdate.time)
                        updateCache(p)
                        ret[uuid] = p
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return@supply ret
        }
    }

    private val MINECRAFT_USERNAME_PATTERN: Pattern = Pattern.compile("^\\w{3,16}$")

    private fun isValidMcUsername(s: String): Boolean {
        return MINECRAFT_USERNAME_PATTERN.matcher(s).matches()
    }

    override fun lookupProfilesByName(names: MutableIterable<String>): Promise<Map<String, Profile>> {
        val toFind: MutableSet<String> = HashSet()
        Iterables.addAll(toFind, names)
        val ret: HashMap<String, Profile> = HashMap<String, Profile>()
        val iterator = toFind.iterator()
        while (iterator.hasNext()) {
            val u = iterator.next()
            val profile = getProfile(u)
            if (profile.isPresent) {
                ret.put(u, profile.get())
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
                with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                    val statement = prepareStatement(replaceTableName(String.format(SELECT_ALL_NAMES, sb.toString())))
                    val rs = statement.executeQuery()
                    while (rs.next()) {
                        val name = rs.getString("name")
                        val lastUpdate = rs.getTimestamp("lastupdate")
                        val uuidString = rs.getString("canonicalid")
                        val uuid = UndashedUuids.fromString(uuidString)
                        val p = ImmutableProfile(uuid, name, lastUpdate.time)
                        updateCache(p)
                        ret[name] = p
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return@supply ret
        }
    }
}