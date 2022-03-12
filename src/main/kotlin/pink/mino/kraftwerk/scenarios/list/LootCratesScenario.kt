package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import kotlin.random.Random


class GiveLootCrates : BukkitRunnable() {
    var timer = 600
    override fun run() {
        timer -= 1
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("lootcrates"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
        if (timer == 0) {
            timer = 600
            val tier1Chest = ItemStack(Material.CHEST)
            var meta = tier1Chest.itemMeta
            meta.displayName = Chat.colored("&cTier I Loot Crate")
            meta.lore = listOf(
                "&7Right-click to redeem a Tier I item!"
            )
            tier1Chest.itemMeta = meta
            val tier2Chest = ItemStack(Material.ENDER_CHEST)
            meta = tier2Chest.itemMeta
            meta.displayName = Chat.colored("&cTier II Loot Crate")
            meta.lore = listOf(
                "&7Right-click to redeem a Tier II item!"
            )
            tier2Chest.itemMeta = meta
            for (player in Bukkit.getOnlinePlayers()) {
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    Chat.sendMessage(player, "${Chat.prefix} Players have been given their lootcrates.")
                } else {
                    val chance = Random.nextInt(2)
                    if (chance == 1) {
                        player.inventory.addItem(tier1Chest)
                        Chat.sendMessage(player, "${Chat.prefix} You've been given a &cTier I&7 Loot Crate.")
                    } else {
                        player.inventory.addItem(tier2Chest)
                        Chat.sendMessage(player, "${Chat.prefix} You've been given a &cTier II&7 Loot Crate.")
                    }
                }
                player.playSound(player.location, Sound.NOTE_PLING, 10F, 1F)
            }
        }
    }
}

class LootCratesScenario : Scenario(
    "Loot Crates",
    "Every 10 minutes, every player will receive a lootcrate filled with a random goody.",
    "lootcrates",
    Material.ENDER_CHEST
) {
    val tier1 = arrayOf<ItemStack>(
        ItemStack(Material.IRON_PICKAXE),
        ItemStack(Material.APPLE, 2),
        ItemStack(Material.COOKED_BEEF, 8),
        ItemStack(Material.CAKE),
        ItemStack(Material.RAW_FISH, 64),
        ItemStack(Material.BOW),
        ItemStack(Material.GOLD_CHESTPLATE),
        ItemStack(Material.FISHING_ROD),
        ItemStack(Material.IRON_SWORD),
        ItemStack(Material.DIAMOND_SPADE),
        ItemStack(Material.IRON_LEGGINGS),
        ItemStack(Material.SNOW_BALL, 16),
        ItemStack(Material.BOOK, 3)
    )
    val tier2 = arrayOf(
        ItemStack(Material.DIAMOND),
        ItemStack(Material.GOLD_INGOT, 3),
        ItemStack(Material.IRON_INGOT, 10),
        ItemStack(Material.ENCHANTMENT_TABLE),
        ItemStack(Material.DIAMOND_SWORD),
        ItemStack(Material.DIAMOND_HELMET),
        ItemStack(Material.ARROW, 32),
        ItemStack(Material.GOLDEN_APPLE)
    )

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (!enabled) return
        if (e.item == null) return
        if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) {
            val p = e.player
            val itemStack = p.itemInHand
            if (!itemStack.hasItemMeta() || !itemStack.itemMeta.hasDisplayName()) return
            if (itemStack.type == Material.CHEST) {
                if (!itemStack.itemMeta.displayName.equals(
                        Chat.colored("&cTier I Loot Crate"),
                        ignoreCase = true
                    )
                ) return
                if (itemStack.amount > 1) {
                    Chat.sendMessage(p, "${Chat.prefix} &7You can only open one lootcrate at a time!")
                    return
                }
                val stack = tier1[Random.nextInt(tier1.size)]
                p.inventory.itemInHand = null
                p.inventory.addItem(stack)
                Chat.sendMessage(p, "${Chat.prefix} You have received &c${stack.amount} ${stack.type.name}&7 from your lootcrate!")
            } else if (itemStack.type == Material.ENDER_CHEST) {
                if (!itemStack.itemMeta.displayName.equals(
                        Chat.colored("&cTier II Loot Crate"),
                        ignoreCase = true
                    )
                ) return
                if (itemStack.amount > 1) {
                    Chat.sendMessage(p, "${Chat.prefix} &7You can only open one lootcrate at a time!")
                    return
                }
                val stack = tier2[Random.nextInt(tier1.size)]
                p.inventory.itemInHand = null
                p.inventory.addItem(stack)
                Chat.sendMessage(p, "${Chat.prefix} You have received &c${stack.amount} ${stack.type.name}&7 from your lootcrate!")
            }
        }
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        if (!enabled) {
            return
        }
        if (e.block.type == Material.CHEST || e.block.type == Material.ENDER_CHEST) {
            e.isCancelled = true
            Chat.sendMessage(e.player, "${Chat.prefix} You can't place chests in Loot Crates.")
        }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (!enabled) {
            return
        }
        if (e.currentItem.type == Material.ENDER_CHEST || e.currentItem.type == Material.CHEST) {
            e.isCancelled = true
            Chat.sendMessage(e.whoClicked, "${Chat.prefix} You can't craft chests in Loot Crates.")
        }
    }


    override fun onStart() {
        val tier1Chest = ItemStack(Material.CHEST)
        var meta = tier1Chest.itemMeta
        meta.displayName = Chat.colored("&cTier I Loot Crate")
        meta.lore = listOf(
            "&7Right-click to redeem a Tier I item!"
        )
        tier1Chest.itemMeta = meta
        val tier2Chest = ItemStack(Material.ENDER_CHEST)
        meta = tier2Chest.itemMeta
        meta.displayName = Chat.colored("&cTier II Loot Crate")
        meta.lore = listOf(
            "&7Right-click to redeem a Tier II item!"
        )
        tier2Chest.itemMeta = meta
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.getSpecs().contains(player.name)) {
                Chat.sendMessage(player, "${Chat.prefix} Players have been given their lootcrates.")
            } else {
                val chance = Random.nextInt(2)
                if (chance == 1) {
                    player.inventory.addItem(tier1Chest)
                    Chat.sendMessage(player, "${Chat.prefix} You've been given a &cTier I&7 Loot Crate.")
                } else {
                    player.inventory.addItem(tier2Chest)
                    Chat.sendMessage(player, "${Chat.prefix} You've been given a &cTier II&7 Loot Crate.")
                }
            }
            player.playSound(player.location, Sound.NOTE_PLING, 10F, 1F)
        }
        GiveLootCrates().runTaskTimerAsynchronously(JavaPlugin.getPlugin(Kraftwerk::class.java), 20L, 20L)
    }
}