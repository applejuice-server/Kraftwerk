package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.block.Block
import pink.mino.kraftwerk.scenarios.Scenario

class SlimyCrackScenario : Scenario(
    "Slimy Crack",
    "A Chunk Error running on the X or Z axis splits the world in half. The bottom will be replaced with slime blocks, allowing for easy spleefing.",
    "slimycrack",
    Material.SLIME_BLOCK,
    true
) {
    override fun handleBlock(block: Block) {
        if (!enabled) return
        if (block.location.blockX >= -50 && block.location.blockX <= 50) {
            block.type = Material.AIR
            if (block.location.blockY == 1) {
                block.type == Material.SLIME_BLOCK
            }
        }
    }
}