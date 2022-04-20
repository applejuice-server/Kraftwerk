package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState


class VeinMinerScenario : Scenario(
    "Vein Miner",
    "Mining an ore while shifting mines the whole vein.",
    "veinminer",
    Material.EMERALD_ORE
) {

    private fun getBlocks(start: Block, radius: Int, filter: Material): ArrayList<Block> {
        val blocks = ArrayList<Block>()
        var x = start.location.x - radius
        while (x <= start.location.x + radius) {
            var y = start.location.y - radius
            while (y <= start.location.y + radius) {
                var z = start.location.z - radius
                while (z <= start.location.z + radius) {
                    val loc = Location(start.world, x, y, z)
                    if (loc.block.type == filter) blocks.add(loc.block)
                    z++
                }
                y++
            }
            x++
        }
        return blocks
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.player.world.name == "Spawn") return
        if (!e.player.isSneaking) return
        when (e.block.type) {
            Material.COAL_ORE -> {
                val blocks = getBlocks(e.block, 2, Material.COAL_ORE)
                for (block in blocks) {
                    block.breakNaturally(e.player.itemInHand)
                }
            }
            Material.DIAMOND_ORE -> {
                val blocks = getBlocks(e.block, 2, Material.DIAMOND_ORE)
                for (block in blocks) {
                    block.breakNaturally(e.player.itemInHand)
                }
            }
            Material.IRON_ORE -> {
                val blocks = getBlocks(e.block, 2, Material.IRON_ORE)
                for (block in blocks) {
                    block.breakNaturally(e.player.itemInHand)
                }
            }
            Material.EMERALD_ORE -> {
                val blocks = getBlocks(e.block, 2, Material.EMERALD_ORE)
                for (block in blocks) {
                    block.breakNaturally(e.player.itemInHand)
                }
            }
            Material.LAPIS_ORE -> {
                val blocks = getBlocks(e.block, 2, Material.LAPIS_ORE)
                for (block in blocks) {
                    block.breakNaturally(e.player.itemInHand)
                }
            }
            Material.GOLD_ORE -> {
                val blocks = getBlocks(e.block, 2, Material.GOLD_ORE)
                for (block in blocks) {
                    block.breakNaturally(e.player.itemInHand)
                }
            }
            Material.REDSTONE_ORE -> {
                val blocks = getBlocks(e.block, 2, Material.REDSTONE_ORE)
                for (block in blocks) {
                    block.breakNaturally(e.player.itemInHand)
                }
            }
            else -> {}
        }
    }
}