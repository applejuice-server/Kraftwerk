package pink.mino.kraftwerk.listeners

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Sheep
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class PlayerInteractListener : Listener {
    @EventHandler
    fun onPlayerInteractWithEntity(e: PlayerInteractAtEntityEvent) {
        if (e.player.itemInHand.type != Material.SHEARS) return
        if (e.rightClicked.type == EntityType.SHEEP) {
            val sheep = e.rightClicked as Sheep
            if (sheep.isSheared) return
            if (Random().nextInt(100) < 33) e.rightClicked.world.dropItemNaturally(e.rightClicked.location, ItemStack(Material.STRING))
        }
    }
}