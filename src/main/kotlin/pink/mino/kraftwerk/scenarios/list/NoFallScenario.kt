package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import pink.mino.kraftwerk.scenarios.Scenario


class NoFallScenario : Scenario(
    "NoFall",
    "Players do not take fall damage.",
    "nofall",
    Material.FEATHER
){
    @EventHandler
    fun on(e: EntityDamageEvent) {
        if (!enabled) {
            return
        }
        if (e.entity is Player) {
            if (e.cause == EntityDamageEvent.DamageCause.FALL) {
                e.isCancelled = true
            }
        }
    }
}