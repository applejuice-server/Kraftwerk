package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import kotlin.random.Random

class MonstersIncScenario : Scenario(
    "Monsters Inc.",
    "Right-clicking a door teleports you to another random door.",
    "monstersinc",
    Material.IRON_DOOR
) {
    val doors: ArrayList<Location> = arrayListOf()
    val doorList = listOf(
        Material.IRON_DOOR,
        Material.ACACIA_DOOR,
        Material.DARK_OAK_DOOR,
        Material.SPRUCE_DOOR,
        Material.BIRCH_DOOR,
        Material.JUNGLE_DOOR,
        Material.WOODEN_DOOR,
        Material.WOOD_DOOR
    )

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (!enabled) return
        if (doorList.contains(e.block.type)) {
            doors.add(e.block.location)
            Chat.sendMessage(e.player, "${Chat.prefix} This door has been linked.")
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (doors.contains(e.block.location)) {
            doors.remove(e.block.location)
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.clickedBlock == null) return
        if (e.action != Action.RIGHT_CLICK_BLOCK && e.action != Action.RIGHT_CLICK_AIR) return
        if (doorList.contains(e.clickedBlock.type)) {
            Chat.sendMessage(e.player, "${Chat.prefix} Teleporting to a random door...")
            val location = doors[Random.nextInt(doors.size)]
            e.player.teleport(location)
        }
    }
}