package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class GraveRobbersScenario : Scenario(
    "Grave Robbers",
    "Players are buried into a grave when they die.",
    "graverobbers",
    Material.MOSSY_COBBLESTONE
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        var block = e.entity.location.block
        val chest1 = e.entity.location.block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN)
        chest1.type = Material.CHEST
        val chest2 = e.entity.location.block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH)
        chest2.type = Material.CHEST
        val chest = chest2.state as Chest
        for (item in e.drops) {
            if (item != null && item.type != Material.AIR) {
                chest.inventory.addItem(item)
            }
        }
        e.drops.clear()
        block = block.getRelative(BlockFace.DOWN)
        block.type = Material.GRAVEL
        block = block.getRelative(BlockFace.NORTH)
        block.type = Material.GRAVEL
        block = block.getRelative(BlockFace.NORTH)
        block.type = Material.COBBLESTONE
        block = block.getRelative(BlockFace.UP)
        block.type = Material.COBBLE_WALL
        block = block.getRelative(BlockFace.UP)
        block.type = Material.COBBLE_WALL
        block = block.getRelative(BlockFace.UP)
        block.type = Material.COBBLE_WALL
        block = block.getRelative(BlockFace.EAST)
        block.type = Material.COBBLE_WALL
        block = block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST)
        block.type = Material.COBBLE_WALL
        block = block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP)
        block.type = Material.COBBLE_WALL
    }
}