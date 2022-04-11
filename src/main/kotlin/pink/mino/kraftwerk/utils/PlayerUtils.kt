package pink.mino.kraftwerk.utils

import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

        fun inventoryFull(player: Player): Boolean {
            return player.inventory.firstEmpty() == -1
        }

        fun bulkItems(player: Player, bulk: ArrayList<ItemStack>) {
            for (item in bulk) {
                if (!inventoryFull(player)) {
                    player.inventory.addItem(item)
                } else {
                    player.world.dropItemNaturally(player.location, item)
                }
            }
        }
    }
}