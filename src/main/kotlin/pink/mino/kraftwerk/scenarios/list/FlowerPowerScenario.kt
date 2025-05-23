package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState
import java.util.*


class FlowerPowerScenario : Scenario(
    "Flower Power",
    "Flowers drop random items.",
    "flowerpower",
    Material.YELLOW_FLOWER
) {
    val flowerTypes = listOf(
        Material.YELLOW_FLOWER,
        Material.DOUBLE_PLANT,
        Material.RED_ROSE,
        Material.BROWN_MUSHROOM,
        Material.RED_MUSHROOM
    )
    val enchantments = listOf<Enchantment>(
        Enchantment.ARROW_FIRE,
        Enchantment.ARROW_DAMAGE,
        Enchantment.FIRE_ASPECT,
        Enchantment.PROTECTION_ENVIRONMENTAL,
        Enchantment.LOOT_BONUS_BLOCKS,
        Enchantment.DAMAGE_ALL,
        Enchantment.THORNS
    )

    val blacklistedMaterials = listOf(
        Material.AIR,
        Material.MONSTER_EGG,
        Material.MONSTER_EGGS,
        Material.GRASS,
        Material.COMMAND,
        Material.BARRIER,
        Material.BEDROCK,
        Material.BED,
        Material.ENDER_PORTAL_FRAME,
        Material.ACACIA_DOOR,
        Material.DARK_OAK_DOOR,
        Material.IRON_DOOR,
        Material.WOODEN_DOOR,
        Material.BIRCH_DOOR,
        Material.SPRUCE_DOOR,
        Material.JUNGLE_DOOR,
        Material.PISTON_EXTENSION,
        Material.PISTON_MOVING_PIECE,
        Material.FIRE,
        Material.PORTAL,
        Material.ENDER_PORTAL,
        Material.SKULL,
        Material.SIGN_POST,
        Material.WALL_SIGN,
        Material.REDSTONE_WIRE,
        Material.TRIPWIRE,
        Material.CROPS,
        Material.CARROT,
        Material.POTATO,
        Material.MELON_STEM,
        Material.PUMPKIN_STEM,
        Material.COCOA,
        Material.NETHER_WARTS,
        Material.SUGAR_CANE_BLOCK,
        Material.BURNING_FURNACE,
        Material.REDSTONE_COMPARATOR_OFF,
        Material.REDSTONE_COMPARATOR_ON,
        Material.REDSTONE_TORCH_OFF,
        Material.REDSTONE_TORCH_ON,
        Material.POWERED_RAIL,
        Material.DETECTOR_RAIL,
        Material.ACTIVATOR_RAIL,
        Material.DAYLIGHT_DETECTOR,
        Material.COMMAND_MINECART,
        Material.EXPLOSIVE_MINECART,
        Material.HOPPER_MINECART,
        Material.MINECART,
        Material.STORAGE_MINECART,
        Material.ITEM_FRAME,
        Material.ARMOR_STAND,
        Material.WATER,
        Material.STANDING_BANNER
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
        if (e.entityType != EntityType.DROPPED_ITEM) return
        val item: Item = e.entity
        val type: Material = item.itemStack.type
        if (blacklistedMaterials.contains(type) || flowerTypes.contains(type)) {
            item.remove()
        }
    }


    private fun generateRandomItem(): ItemStack {
        var item = ItemStack(Material.AIR)
        while (item.type === Material.AIR || blacklistedMaterials.contains(item.type) || CraftMagicNumbers.getItem(item.type) == null) {
            item = ItemStack(Material.values()[Random().nextInt(Material.values().size)])
            if (item.type == Material.ENCHANTED_BOOK) {
                val enchantMeta = (item.itemMeta as EnchantmentStorageMeta)
                enchantMeta.addStoredEnchant(enchantments.random(), 1, true)
                item.itemMeta = enchantMeta
            }
        }
        if (item.maxStackSize > 2) {
            item.amount = Random().nextInt(63) + 1
        }
        return item
    }

}