package pink.mino.kraftwerk.scenarios.list

import me.lucko.helper.utils.Log
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState
import kotlin.math.roundToInt

class RedArrowsScenario : Scenario(
    "Red Arrows",
    "Large red arrows appear when a player dies.",
    "redarrows",
    Material.ARROW
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val x = e.entity.location.x
        val z = e.entity.location.z
        val highest = e.entity.location.world.getHighestBlockAt(x.toInt(), z.toInt())
        Log.info("Creating a Red Arrow at x: ${x.roundToInt()}, y: ${highest.y}, z: ${z.roundToInt()}")
        var block = highest.getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
            .getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.EAST)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block = block.getRelative(BlockFace.EAST)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.WEST)
        block = block.getRelative(BlockFace.WEST)
        block = block.getRelative(BlockFace.WEST)
        block = block.getRelative(BlockFace.WEST)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.DOWN)
        block = block.getRelative(BlockFace.EAST)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.DOWN)
        block = block.getRelative(BlockFace.EAST)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
        block = block.getRelative(BlockFace.UP)
        block.type = Material.STAINED_CLAY
        block.setData(1)
    }
}