package pink.mino.kraftwerk.listeners

import com.google.common.collect.Sets
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pink.mino.kraftwerk.events.ChunkModifiableEvent
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.BlockUtil
import java.util.*

class OreLimiterListener : Listener {

    private var random = Random()
    val ores = listOf(
        Material.COAL_ORE,
        Material.IRON_ORE,
        Material.GOLD_ORE,
        Material.REDSTONE_ORE,
        Material.LAPIS_ORE,
        Material.DIAMOND_ORE,
        Material.EMERALD_ORE
    )

    @EventHandler
    fun on(event: ChunkModifiableEvent) {
        val chunk = event.chunk
        val checked: MutableSet<Block> = Sets.newHashSet()

        // Redstone removed 40% of the time (Just experimental, Inferno's is the same and gets no complaints)
        // Diamond/Gold removed from SettingsFeature
        val goldRate = ConfigFeature.instance.worlds!!.getInt(chunk.world.name + ".orerates.gold")
        val diamondRate = ConfigFeature.instance.worlds!!.getInt(chunk.world.name + ".orerates.diamond")
        val oresOutsideCaves = ConfigFeature.instance.worlds!!.getBoolean("${chunk.world.name}.oresOutsideCaves")

        for (x in 0..16) {
            for (y in 0..64) {
                for (z in 0..16) {
                    val block = chunk.getBlock(x, y, z)

                    if (checked.contains(block)) continue

                    val type = block.type

                    if (!ores.contains(type)) continue

                    val vein: List<Block> = BlockUtil().getVein(block)
                    checked.addAll(vein)

                    if (!oresOutsideCaves) {
                        var good = false
                        vein.forEach {
                            BlockUtil().getBlocks(it, 2)
                                .forEach { block -> if (block.type == Material.AIR ||
                                    block.type == Material.WATER ||
                                    block.type == Material.STATIONARY_WATER ||
                                    block.type == Material.LAVA ||
                                    block.type == Material.STATIONARY_LAVA
                                ) good = true }
                        }
                        if (!good) {
                            vein.forEach { it.type = Material.STONE }
                            continue
                        }
                    }
                    if (type == Material.REDSTONE_ORE) {
                        if ((random.nextInt(99) + 1) <= 40) {
                            vein.forEach { it.type = Material.STONE }
                        }
                    } else if (type == Material.GOLD_ORE){
                        if ((random.nextInt(99) + 1) <= goldRate) {
                            vein.forEach { it.type = Material.STONE }
                        }
                    } else if (type == Material.DIAMOND_ORE) {
                        if ((random.nextInt(99) + 1) <= diamondRate) {
                            vein.forEach { it.type = Material.STONE }
                        }
                    }
                }
            }
        }
    }
}