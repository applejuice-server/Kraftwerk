package pink.mino.kraftwerk.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import java.util.*


class BlockUtil {
    fun getBlocks(start: Block, radius: Int): ArrayList<Block>? {
        val blocks = ArrayList<Block>()
        var x = start.location.x - radius
        while (x <= start.location.x + radius) {
            var y = start.location.y - radius
            while (y <= start.location.y + radius) {
                var z = start.location.z - radius
                while (z <= start.location.z + radius) {
                    val loc: Location = Location(start.world, x, y, z)
                    blocks.add(loc.block)
                    z++
                }
                y++
            }
            x++
        }
        return blocks
    }

    fun degradeDurability(player: Player) {
        val item = player.itemInHand

        if ((item.type == Material.AIR) || (item.type == Material.BOW) || item.type.maxDurability.toInt() == 0) {
            return
        }

        var durability = item.durability
        val rand = Random()

        if (item.containsEnchantment(Enchantment.DURABILITY)) {
            val chance = (100 / (item.getEnchantmentLevel(Enchantment.DURABILITY) + 1))

            if (rand.nextDouble() <= (chance / 100)) {
                durability++
            }
        } else {
            durability++
        }

        if (durability >= item.type.maxDurability) {
            player.world.playSound(player.location, Sound.ITEM_BREAK, 1.0F, 1.0F)
            player.itemInHand.type = Material.AIR
            return
        }

        item.durability = durability
        player.itemInHand = item
    }
}