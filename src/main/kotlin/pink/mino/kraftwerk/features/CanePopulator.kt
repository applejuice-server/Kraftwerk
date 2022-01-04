package pink.mino.kraftwerk.features

import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.generator.BlockPopulator
import java.util.*

class CanePopulator : BlockPopulator() {

    private var canePatchChance = 0
    private var cane: Material? = null
    private var faces: Array<BlockFace> = arrayOf(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)

    init {
        canePatchChance = 60
        cane = Material.SUGAR_CANE_BLOCK
    }

    override fun populate(world: World?, random: Random, source: Chunk) {
        if (random.nextInt(100) < canePatchChance) {
            for (i in 0..15) {
                var block: Block? = if (random.nextBoolean()) {
                    getHighestBlock(source, random.nextInt(16), i)
                } else {
                    getHighestBlock(source, i, random.nextInt(16))
                }
                if (block!!.type == Material.GRASS || block.type == Material.SAND) {
                    createCane(block, random)
                }
            }
        }
    }

    private fun createCane(block: Block?, rand: Random) {
        var create = false
        val var4 = faces
        val var5 = var4.size
        for (var6 in 0 until var5) {
            val face = var4[var6]
            if (block!!.getRelative(face).type.name.lowercase(Locale.getDefault()).contains("water")) {
                create = true
            }
        }
        if (create) {
            for (i in 1 until rand.nextInt(4) + 3) {
                block!!.getRelative(0, i, 0).type = cane
            }
        }
    }

    private fun getHighestBlock(chunk: Chunk, x: Int, z: Int): Block? {
        var block: Block? = null
        for (i in 127 downTo 0) {
            if (chunk.getBlock(x, i, z).also { block = it }.typeId != 0) {
                return block
            }
        }
        return block
    }

}