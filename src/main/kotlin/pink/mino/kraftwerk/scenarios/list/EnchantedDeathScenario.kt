package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class EnchantedDeathScenario : Scenario(
    "Enchanted Death",
    "Enchantment Tables are disabled, the only way to acquire one is by killing someone.",
    "enchanteddeath",
    Material.ENCHANTMENT_TABLE
) {
    @EventHandler
    fun onPlayerCraft(e: CraftItemEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val item: ItemStack? = e.currentItem
        if (item != null) {
            e.isCancelled = item == ItemStack(Material.ENCHANTMENT_TABLE)
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        e.drops.add(ItemStack(Material.ENCHANTMENT_TABLE))
    }
}