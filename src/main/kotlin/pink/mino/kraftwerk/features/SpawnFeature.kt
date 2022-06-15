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
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder

class SpawnFeature : Listener {
    val spawnLocation = Location(Bukkit.getWorld("Spawn"), -221.5, 95.0, -140.5)

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
        p.gameMode = GameMode.ADVENTURE
        p.exp = 0F
        p.level = 0

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

        val donator = ItemBuilder(Material.EMERALD)
            .name("&cDonator Menu &7(Right Click)")
            .addLore("&7Right-click to view the Donator Menu.")
            .make()
        p.inventory.setItem(4, donator)

        val scenarios = ItemBuilder(Material.CHEST)
            .name("&cActive Scenarios &7(Right Click)")
            .addLore("&7Right-click to view active scenarios.")
            .make()
        p.inventory.setItem(8, scenarios)
        val championsKit = ItemBuilder(Material.NETHER_STAR)
            .name("&cChampions Kit &7(Right Click)")
            .addLore("&7Right-click to view the Champions kit selector.")
            .make()

        if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("auction"))) {
            p.inventory.clear()
        }
        if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("champions"))) {
            p.inventory.setItem(0, championsKit)
        }
        val location = if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("auction"))) Location(Bukkit.getWorld("Spawn"), -221.5, 95.0, -140.5) else Location(Bukkit.getWorld("Spawn"), -278.0, 96.5, 7.0)
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
                    Chat.colored("&cDonator Menu &7(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "donator")
                    }
                    Chat.colored("&cChampions Kit &7(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "ckit")
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