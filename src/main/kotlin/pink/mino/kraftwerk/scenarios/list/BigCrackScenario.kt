package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.block.Block
import pink.mino.kraftwerk.scenarios.Scenario

class BigCrackScenario : Scenario(
    "Big Crack",
    "A Chunk Error running on the X or Z axis splits the world in half.",
    "bigcrack",
    Material.GRASS,
    true
) {
    override fun handleBlock(block: Block) {
        if (!enabled) return
        if (block.location.blockX >= -50 && block.location.blockX <= 50) {
            block.type = Material.AIR
        }
    }
}
