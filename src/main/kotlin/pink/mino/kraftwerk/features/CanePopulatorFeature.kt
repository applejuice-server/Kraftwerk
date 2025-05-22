package pink.mino.kraftwerk.features

import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pink.mino.kraftwerk.events.ChunkModifiableEvent
import java.util.*

class CanePopulatorFeature : Listener {

    private var cane: Material = Material.SUGAR_CANE_BLOCK
    private var faces: Array<BlockFace> = arrayOf(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)

    @EventHandler
    fun on(event: ChunkModifiableEvent) {
        val chunk = event.chunk
        val rand = Random()

        if (chunk.world.environment != World.Environment.NORMAL) return

        if (ConfigFeature.instance.worlds!!.getInt("${chunk.world!!.name}.canerate") == 0) return

        if (rand.nextInt(100) <= ConfigFeature.instance.worlds!!.getInt("${chunk.world!!.name}.canerate")) {
            for (x in 0..15) {
                val block: Block? = if (rand.nextBoolean()) {
                    getHighestBlock(chunk, rand.nextInt(16), x)
                } else {
                    getHighestBlock(chunk, x, rand.nextInt(16))
                }
                if (block == null) continue
                if (block.type == Material.GRASS || block.type == Material.SAND) {
                    createCane(block, rand)
                }
            }
        }
    }
    private fun createCane(block: Block?, rand: Random) {

        var create = false
        for (face in faces) {
            if (block!!.getRelative(face).type.name.lowercase(Locale.getDefault()).contains("water")) {
                create = true
                break
            }
        }
        if (!create) return
        for (i in 1..rand.nextInt(4)+3) {
            block!!.getRelative(0, i, 0).type = this.cane
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