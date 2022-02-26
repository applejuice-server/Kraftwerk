package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario

class BleedingSweetsScenario : Scenario(
    "Bleeding Sweets",
    "When a player dies, they drop 1 diamond, 5 gold, 16 arrows and 1 string.",
    "bleedingsweets",
    Material.REDSTONE
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val diamond = ItemStack(Material.DIAMOND, 1)
        val gold = ItemStack(Material.GOLD_INGOT, 5)
        val arrows = ItemStack(Material.ARROW, 16)
        val string = ItemStack(Material.STRING, 1)
        val book = ItemStack(Material.BOOK, 1)
        e.drops.add(diamond)
        e.drops.add(gold)
        e.drops.add(arrows)
        e.drops.add(string)
        e.drops.add(book)
    }
}