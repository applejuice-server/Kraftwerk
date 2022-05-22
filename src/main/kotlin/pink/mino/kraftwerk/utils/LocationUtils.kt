package pink.mino.kraftwerk.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.TravelAgent
import org.bukkit.World
import org.bukkit.WorldBorder
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import kotlin.math.abs


object LocationUtils {
    private val faces = arrayOf(
        BlockFace.SELF,
        BlockFace.EAST,
        BlockFace.NORTH,
        BlockFace.SOUTH,
        BlockFace.WEST,
        BlockFace.NORTH_EAST,
        BlockFace.SOUTH_EAST,
        BlockFace.SOUTH_WEST,
        BlockFace.NORTH_WEST
    )

    fun isOutsideOfBorder(loc: Location): Boolean {
        val border = loc.world.worldBorder
        val size = border.size / 2
        val center = border.center
        val x = loc.x - center.x
        val z = loc.z - center.z
        return x > size || -x > size || z > size || -z > size
    }

    fun hasBlockNearby(material: Material, location: Location): Boolean {
        val block: Block = location.block
        for (face in faces) {
            if (block.getRelative(face).type === material) {
                return true
            }
        }
        return false
    }

    fun findSafeLocationInsideBorder(loc: Location, buffer: Int, travel: TravelAgent?): Location {
        val border: WorldBorder = loc.world.worldBorder
        val centre: Location = border.center
        var pos: Location = loc.subtract(centre)
        val size: Double = border.size / 2
        val bufferSize = size - buffer
        val x: Double = pos.x
        val z: Double = pos.z
        var changed = false
        if (abs(x) > size) {
            pos.x = if (x > 0) bufferSize else -bufferSize
            changed = true
        }
        if (abs(z) > size) {
            pos.z = if (z > 0) bufferSize else -bufferSize
            changed = true
        }
        if (!changed) {
            return loc
        }
        pos.y = loc.world.getHighestBlockYAt(loc).toDouble()
        if (travel != null) {
            val to: Location = travel.findOrCreate(pos)
            if (!isOutsideOfBorder(to)) {
                pos = to
            }
        }
        return pos
    }

    fun getHighestBlock(loc: Location): Location {
        var highest: Int
        highest = if (loc.world.environment == World.Environment.NETHER) {
            127
        } else {
            255
        }

        for (i in highest downTo 0) {
            if (loc.world.getBlockAt(loc.blockX, i, loc.blockZ).type != Material.AIR && loc.world.getBlockAt(loc.blockX, i, loc.blockZ).type != Material.LONG_GRASS && loc.world.getBlockAt(loc.blockX, i, loc.blockZ).type != Material.DOUBLE_PLANT && loc.world.getBlockAt(loc.blockX, i, loc.blockZ).type != Material.YELLOW_FLOWER && loc.world.getBlockAt(loc.blockX, i, loc.blockZ).type != Material.RED_ROSE && loc.world.getBlockAt(loc.blockX, i, loc.blockZ).type != Material.VINE) {
                return loc.world.getBlockAt(loc.blockX, i, loc.blockZ).location
            }
        }
        return loc
    }
}