package pink.mino.kraftwerk.scenarios.list

import com.google.common.collect.ImmutableList
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState
import java.util.*


class FlowerPowerScenario : Scenario(
    "Flower Power",
    "Flowers drop random items.",
    "flowerpower",
    Material.YELLOW_FLOWER
) {
    val flowerTypes = ImmutableList.of(
        Material.YELLOW_FLOWER,
        Material.DOUBLE_PLANT,
        Material.RED_ROSE,
        Material.BROWN_MUSHROOM,
        Material.RED_MUSHROOM
    )
    val blacklistedMaterials = ImmutableList.of(
        Material.MONSTER_EGG,
        Material.MONSTER_EGGS,
        Material.GRASS,
        Material.COMMAND_MINECART,
        Material.HOPPER_MINECART,
        Material.COMMAND,
        Material.GLOWSTONE,
        Material.GLOWSTONE_DUST,
        Material.BARRIER,
        Material.BEDROCK,
        Material.BED,
        Material.ENDER_PORTAL_FRAME
    )

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val block: Block = e.block
        if (flowerTypes.contains(block.type)) {
            e.isCancelled = true
            if (block.type === Material.DOUBLE_PLANT) {
                val other: Block = block.location.world.getBlockAt(block.location.add(0.0, 1.0, 0.0))
                other.type = Material.AIR
            }
            block.type = Material.AIR
            val item: ItemStack = generateRandomItem()
            block.world.dropItemNaturally(block.location, item)
        }
    }

    @EventHandler
    fun onSpawn(e: ItemSpawnEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val item: Item = e.entity
        val type: Material = item.itemStack.type
        if (blacklistedMaterials.contains(type) || flowerTypes.contains(type)) {
            item.remove()
        }
    }


    private fun generateRandomItem(): ItemStack {
        var item = ItemStack(Material.AIR)
        while (item.type === Material.AIR || blacklistedMaterials.contains(item.type) || item.typeId === 60) {
            item = ItemStack(Material.values()[Random().nextInt(Material.values().size)])
        }
        if (item.maxStackSize > 2) {
            item.amount = Random().nextInt(63) + 1
        }
        return item
    }

}