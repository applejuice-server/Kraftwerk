package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.GameState
import kotlin.math.floor

class NicholasCageScenario : Scenario(
    "Nicholas Cage",
    "Players are covered in a sphere of TNT when they die.",
    "nicholascage",
    Material.TNT
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val location: Location = e.entity.location
        val blocks: ArrayList<Block>? = BlockUtil().getBlocks(location.block, 10)
        blocks!!.stream().filter { block: Block ->
            floor(
                block.location.distance(location)
            ) == 4.0
        }.filter { block: Block -> block.type === Material.AIR }.forEach { block: Block ->
            block.type = Material.TNT
        }
    }
}