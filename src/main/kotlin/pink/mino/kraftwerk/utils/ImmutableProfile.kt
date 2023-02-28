package pink.mino.kraftwerk.utils

import me.lucko.helper.profiles.Profile
import java.util.*

class ImmutableProfile(
    @JvmField val uniqueId: UUID,
    @JvmField var name: String?,
    @JvmField val timestamp: Long,
    var disableRedstonePickup: Boolean = false,
    var disableLapisPickup: Boolean = false,
    var projectileMessages: String = "CHAT",
    val healthType: String = "PERCENTAGE",
    var borderPreference: String = "RADIUS",
    var ignored: ArrayList<UUID> = arrayListOf(),
    var deathMessageOnScreen: Boolean = true,
    var xpNeeded: Double = 150.0,
    var xp: Double = 0.0,
    var level: Int = 1,
    var chatMode: String = "PUBLIC",
    var coins: Double = 0.0
) : Profile {
    override fun getUniqueId(): UUID {
        return this.uniqueId
    }

    override fun getName(): Optional<String> {
        return Optional.ofNullable(this.name)
    }

    override fun getTimestamp(): Long {
        return timestamp
    }

}