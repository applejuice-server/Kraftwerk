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
import org.bukkit.event.player.PlayerItemConsumeEvent
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder

class SpawnFeature : Listener {
    companion object {
        val instance = SpawnFeature()
    }

    fun send(p: Player) {
        p.maxHealth = 20.0
        p.health = 20.0
        p.allowFlight = false
        p.isFlying = false
        p.foodLevel = 20
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null
        p.gameMode = GameMode.SURVIVAL
        p.exp = 0F
        p.level = 0
        val arenaSword = ItemBuilder(Material.IRON_SWORD)
            .name("&cFFA Arena &7(Right Click)")
            .addLore("&7Right-click to send yourself into the FFA Arena.")
            .noAttributes()
            .make()
        p.inventory.setItem(4, arenaSword)

        val stats = ItemBuilder(Material.WRITTEN_BOOK)
            .name("&cView Stats &7(Right Click)")
            .addLore("&7Right-click to view your stats.")
            .make()
        p.inventory.setItem(5, stats)

        val config = ItemBuilder(Material.GOLDEN_APPLE)
            .name("&cUHC Configuration &7(Right Click)")
            .addLore("&7Right-click to view the UHC Configuration.")
            .make()
        p.inventory.setItem(3, config)

        val scenarios = ItemBuilder(Material.EMERALD)
            .name("&cActive Scenarios &7(Right Click)")
            .addLore("&7Right-click to view active scenarios.")
            .make()
        p.inventory.setItem(0, scenarios)

        val location = Location(Bukkit.getWorld("Spawn"), -221.5, 95.0, -140.5)
        p.teleport(location)
    }

    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (e.player.world.name == "Spawn") {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.player.world.name == "Spawn") {
            if (e.item !== null) {
                when (e.item.itemMeta.displayName) {
                    Chat.colored("&cFFA Arena &7(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "a")
                    }
                    Chat.colored("&cView Stats &7(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "stats")
                    }
                    Chat.colored("&cUHC Configuration &7(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "uhc")
                    }
                    Chat.colored("&cActive Scenarios &7(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "scen")
                    }
                }
            }
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