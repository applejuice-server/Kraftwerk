package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.utils.Chat

class SpawnFeature : Listener {
    companion object {
        val instance = SpawnFeature()
    }

    fun send(p: Player) {
        p.health = 20.0
        p.foodLevel = 20
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null
        p.gameMode = GameMode.SURVIVAL
        val arenaSword = ItemStack(Material.IRON_SWORD)
        val arenaMeta = arenaSword.itemMeta
        arenaMeta.displayName = Chat.colored("&cFFA Arena")
        arenaMeta.lore = listOf(
            Chat.colored("&7Right-click to send yourself into the FFA Arena.")
        )
        arenaSword.itemMeta = arenaMeta
        p.inventory.setItem(0, arenaSword)
        val location = Location(Bukkit.getWorld("Spawn"), 0.5, 5.5, 0.5)
        p.teleport(location)
    }

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.item.itemMeta.displayName == Chat.colored("&cFFA Arena")) {
            ArenaFeature.instance.send(e.player)
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked.world.name == "Spawn") {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if (e.player.world.name == "Spawn") {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.player.world.name == "Spawn") {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (e.player.world.name == "Spawn") {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (e.entity.type == EntityType.PLAYER) {
            if (e.entity.world.name == "Spawn") {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerFoodChange(e: FoodLevelChangeEvent) {
        if (e.entity.type == EntityType.PLAYER) {
            if (e.entity.world.name == "Spawn") {
                e.isCancelled = true
            }
        }
    }
}