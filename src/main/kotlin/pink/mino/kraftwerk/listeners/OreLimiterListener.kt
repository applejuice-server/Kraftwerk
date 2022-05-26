package pink.mino.kraftwerk.listeners

import com.google.common.collect.Sets
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pink.mino.kraftwerk.events.ChunkModifiableEvent
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.BlockUtil
import java.util.*

class OreLimiterListener : Listener {

    private var random = Random()

    @EventHandler
    fun on(event: ChunkModifiableEvent) {
        val chunk = event.chunk
        val checked: MutableSet<Block> = Sets.newHashSet()

        // Redstone removed 40% of the time (Just experimental, Inferno's is the same and gets no complaints)
        // Diamond/Gold removed from SettingsFeature
        val goldRate = SettingsFeature.instance.worlds!!.getInt(chunk.world.name + ".orerates.gold")
        val diamondRate = SettingsFeature.instance.worlds!!.getInt(chunk.world.name + ".orerates.diamond")

        for (x in 0..16) {
            for (y in 0..64) {
                for (z in 0..16) {
                    val block = chunk.getBlock(x, y, z)

                    if (checked.contains(block)) continue

                    val type = block.type

                    if (type != Material.REDSTONE_ORE && type != Material.DIAMOND_ORE && type != Material.GOLD_ORE) continue

                    val vein: List<Block> = BlockUtil().getVein(block)
                    checked.addAll(vein)

                    if (type == Material.REDSTONE_ORE) {
                        if ((random.nextInt(99) + 1) <= 40) {
                            vein.forEach { it.type = Material.STONE }
                        }
                    } else if (type == Material.GOLD_ORE){
                        if ((random.nextInt(99) + 1) <= goldRate) {
                            vein.forEach { it.type = Material.STONE }
                        }
                    } else {
                        if ((random.nextInt(99) + 1) <= diamondRate) {
                            vein.forEach { it.type = Material.STONE }
                        }
                    }
                }
            }
        }
    }
}