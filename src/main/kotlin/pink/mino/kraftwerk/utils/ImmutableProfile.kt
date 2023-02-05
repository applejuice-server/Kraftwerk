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
    var deathMessageOnScreen: Boolean = true
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