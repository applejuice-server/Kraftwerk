package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario

class BetaZombiesScenario : Scenario(
    "Beta Zombies",
    "Zombies drop feathers.",
    "betazombies",
    Material.ROTTEN_FLESH
) {
    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        if (!enabled) return
        if (e.entity.type == EntityType.ZOMBIE) {
            e.drops.add(ItemStack(Material.FEATHER))
        }
    }
}