package pink.mino.kraftwerk.utils

import com.google.common.collect.Lists
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import java.util.*


class BlockUtil {
    fun getBlocks(start: Block, radius: Int): ArrayList<Block> {
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

        if ((item.type == Material.AIR) || (item.type == Material.BOW) || item.type.maxDurability.toInt() == 0 || item.itemMeta.spigot().isUnbreakable) {
            return
        }

        var durability = item.durability
        val rand = Random()

        if (item.containsEnchantment(Enchantment.DURABILITY)) {
            val chance = (100 / (item.getEnchantmentLevel(Enchantment.DURABILITY) + 1))

            if (rand.nextDouble() <= (chance / 100)) {
                durability--
            }
        } else {
            durability--
        }

        if (durability >= item.type.maxDurability) {
            player.world.playSound(player.location, Sound.ITEM_BREAK, 1.0F, 1.0F)
            player.itemInHand.type = Material.AIR
            return
        }

        item.durability = durability
        player.itemInHand = item
    }

    companion object {
        private val DEFAULT_VEIN_LIMIT = 100
    }

    fun getVein(start: Block): List<Block> {
        var type = start.type
        if (start.type == Material.GLOWING_REDSTONE_ORE) {
            type = Material.REDSTONE_ORE
        }
        return getVein(start, DEFAULT_VEIN_LIMIT)
    }

    fun getNearby(block: Block): List<Block> {
        val nearby: ArrayList<Block> = arrayListOf()

        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue
                    }
                    nearby.add(block.getRelative(dx, dy, dz))
                }
            }
        }
        return nearby
    }

    fun getVein(start: Block, maxVeinSize: Int): List<Block> {
        val toCheck: LinkedList<Block> = Lists.newLinkedList()
        val vein: ArrayList<Block> = Lists.newArrayList()

        toCheck.add(start)
        vein.add(start)

        while (!toCheck.isEmpty()) {
            val check = toCheck.poll()

            for (nearbyBlock in getNearby(check)) {
                if (vein.contains(nearbyBlock)) continue
                var type = nearbyBlock.type
                if (type == Material.GLOWING_REDSTONE_ORE) type = Material.REDSTONE_ORE
                if (type != start.type) continue

                toCheck.add(nearbyBlock)
                vein.add(nearbyBlock)

                if (vein.size > maxVeinSize) {
                    return vein
                }
            }
        }
        return vein
    }

}