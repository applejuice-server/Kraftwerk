package pink.mino.kraftwerk.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import java.util.*


class FoodChangeListener : Listener {
    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (event.foodLevel < (event.entity as Player).foodLevel) {
            event.isCancelled = Random().nextInt(100) < 66
        }
    }
}