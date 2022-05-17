package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.inventory.InventoryHolder
import pink.mino.kraftwerk.scenarios.Scenario
import kotlin.random.Random


class UndergroundParallelScenario : Scenario(
    "Underground Parallel",
    "Below y42 is a copy of the surface above with changes to trees. Leaves under y42 are replaced with 50% stone, 24.5% coal ore, 24% iron ore, 1% gold ore, and 0.5% diamond ore. Logs under level 42 are replaced with 5% glowstone, 5% redstone ore and 90% gravel. Mineshafts generate floating below y42.",
    "undergroundparallel",
    Material.DIAMOND_ORE,
    true
) {
    override fun handleBlock(block: Block) {
        val loc: Location = block.location

        if (loc.blockY == 0 || loc.blockY > 42) {
            return
        }

        val world: World = block.world
        val surface: Block = world.getBlockAt(block.x, block.y + 59, block.z)

        when (surface.type) {
            Material.LEAVES, Material.LEAVES_2 -> {
                var randomPerLogs: Double = Random.nextDouble() * 100
                if (randomPerLogs < 50.0) {
                    block.type = Material.STONE
                    return
                }
                randomPerLogs -= 50.0
                if (randomPerLogs < 24.5) {
                    block.type = Material.COAL_ORE
                    return
                }
                randomPerLogs -= 24.5
                if (randomPerLogs < 23.5) {
                    block.type = Material.IRON_ORE
                    return
                }
                randomPerLogs -= 23.5
                if (randomPerLogs < 1.0) {
                    block.type = Material.GOLD_ORE
                    return
                }
                randomPerLogs -= 1.0
                if (randomPerLogs < 0.5) {
                    block.type = Material.LAPIS_ORE
                    return
                }
                block.type = Material.DIAMOND_ORE
                return
            }
            Material.LOG, Material.LOG_2 -> {
                var randomPerLeaves: Double = Random.nextDouble() * 100
                if (randomPerLeaves < 5.0) {
                    block.type = Material.REDSTONE_ORE
                    return
                }
                randomPerLeaves -= 5.0
                if (randomPerLeaves < 5.0) {
                    block.type = Material.GLOWSTONE
                    return
                }
                block.type = Material.GRAVEL
                return
            }
            else -> {
                block.type = surface.type
                block.data = surface.data
                if (surface.state is InventoryHolder) {
                    val surfaceInv = surface.state as InventoryHolder
                    val inv = block.state as InventoryHolder
                    inv.inventory.contents = surfaceInv.inventory.contents
                }
            }
        }
    }
}