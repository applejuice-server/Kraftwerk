package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat


class BowlessScenario : Scenario(
    "Bowless",
    "Bows are disabled for the entirety of the game.",
    "bowless",
    Material.BOW
) {
    @EventHandler(ignoreCancelled = true)
    fun on(event: CraftItemEvent) {
        if (!enabled) return
        val item: ItemStack = event.currentItem ?: return
        if (item.type !== Material.BOW) {
            return
        }
        Chat.sendMessage(event.whoClicked as Player, "&cBows are disabled in Bowless!")
        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPickup(e: PlayerPickupItemEvent) {
        if (!enabled) return
        val item = e.item
        val itemStack: ItemStack = item.itemStack
        if (itemStack.type !== Material.BOW) return
        e.isCancelled = true
        item.remove()
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (!enabled) {
            return
        }
        if (e.inventory == null) return
        if (e.whoClicked == null) return
        if (e.click == null) return
        if (e.currentItem == null) return
        if (e.currentItem.type === Material.BOW) {
            e.currentItem.type = Material.AIR //Chests, Villagers, etc
        }
    }

    @EventHandler
    fun onShoot(e: EntityShootBowEvent) {
        if (!enabled) {
            return
        }
        if (e.entity is Player) {
            val p: Player = e.entity as Player
            if (p.itemInHand.type === Material.BOW) {
                e.isCancelled = true
                Chat.sendMessage(p, "&cYou can't use a bow in Bowless!")
            }
        }
    }

    @EventHandler
    fun onhit(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (e.damager is Arrow) {
            e.isCancelled = true
        }
    }
}