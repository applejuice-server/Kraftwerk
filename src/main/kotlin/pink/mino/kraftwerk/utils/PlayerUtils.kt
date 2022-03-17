package pink.mino.kraftwerk.utils

import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.TeamsFeature
import kotlin.math.floor

class PlayerUtils {
    companion object {
        fun getPrefix(player: Player): String {
            return if (TeamsFeature.manager.getTeam(player) != null) {
                TeamsFeature.manager.getTeam(player)!!.prefix
            } else {
                "&f"
            }
        }

        fun getHealth(player: Player): String {
            val el: EntityLiving = (player as CraftPlayer).handle
            val health = floor(player.health / 2 * 10 + el.absorptionHearts / 2 * 10)
            val color = HealthChatColorer.returnHealth(health)
            return "${color}${health}%"
        }
    }
}