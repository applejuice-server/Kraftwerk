package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
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
import pink.mino.kraftwerk.utils.PlayerUtils
import kotlin.random.Random


class GiveLootCrates : BukkitRunnable() {
    var timer = 600
    override fun run() {
        timer -= 1
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("lootcrates")) && !ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("oplootcrates"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
        if (timer == 0) {
            timer = 600
            val tier1Chest = ItemStack(Material.CHEST)
            var meta = tier1Chest.itemMeta
            meta.displayName = Chat.colored("${Chat.primaryColor}Tier I Loot Crate")
            meta.lore = listOf(
                "&7Right-click to redeem a Tier I item!"
            )
            tier1Chest.itemMeta = meta
            val tier2Chest = ItemStack(Material.ENDER_CHEST)
            meta = tier2Chest.itemMeta
            meta.displayName = Chat.colored("${Chat.primaryColor}Tier II Loot Crate")
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
                        PlayerUtils.bulkItems(player, arrayListOf(tier1Chest))
                        Chat.sendMessage(player, "${Chat.prefix} You've been given a ${Chat.primaryColor}Tier I&7 Loot Crate.")
                    } else {
                        PlayerUtils.bulkItems(player, arrayListOf(tier2Chest))
                        Chat.sendMessage(player, "${Chat.prefix} You've been given a ${Chat.primaryColor}Tier II&7 Loot Crate.")
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
    var task: GiveLootCrates? = null

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

    val prefix = "&8[${Chat.primaryColor}Loot Crates&8]&7"
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
                        Chat.colored("${Chat.primaryColor}Tier I Loot Crate"),
                        ignoreCase = true
                    )
                ) return
                if (itemStack.amount > 1) {
                    Chat.sendMessage(p, "${prefix} &7You can only open one lootcrate at a time!")
                    return
                }
                val stack = tier1[Random.nextInt(tier1.size)]
                p.inventory.itemInHand = null
                p.inventory.addItem(stack)
                Chat.sendMessage(p, "${prefix} You have received ${Chat.primaryColor}${stack.amount} ${stack.type.name}&7 from your loot-crate!")
            } else if (itemStack.type == Material.ENDER_CHEST) {
                if (!itemStack.itemMeta.displayName.equals(
                        Chat.colored("${Chat.primaryColor}Tier II Loot Crate"),
                        ignoreCase = true
                    )
                ) return
                if (itemStack.amount > 1) {
                    Chat.sendMessage(p, "${prefix} &7You can only open one loot-crate at a time!")
                    return
                }
                val stack = tier2[Random.nextInt(tier2.size)]
                p.inventory.itemInHand = null
                p.inventory.addItem(stack)
                Chat.sendMessage(p, "${prefix} You have received ${Chat.primaryColor}${stack.amount} ${stack.type.name}&7 from your loot-crate!")
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
            Chat.sendMessage(e.player, "${prefix} You can't place chests in Loot Crates.")
        }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (!enabled) {
            return
        }
        if (e.currentItem.type == Material.ENDER_CHEST || e.currentItem.type == Material.CHEST) {
            e.isCancelled = true
            Chat.sendMessage(e.whoClicked, "${prefix} You can't craft chests in Loot Crates.")
        }
    }

    override fun givePlayer(player: Player) {
        val tier1Chest = ItemStack(Material.CHEST)
        var meta = tier1Chest.itemMeta
        meta.displayName = Chat.colored("${Chat.primaryColor}Tier I Loot Crate")
        meta.lore = listOf(
            "&7Right-click to redeem a Tier I item!"
        )
        tier1Chest.itemMeta = meta
        val tier2Chest = ItemStack(Material.ENDER_CHEST)
        meta = tier2Chest.itemMeta
        meta.displayName = Chat.colored("${Chat.primaryColor}Tier II Loot Crate")
        meta.lore = listOf(
            "&7Right-click to redeem a Tier II item!"
        )
        tier2Chest.itemMeta = meta
        val chance = Random.nextInt(2)
        if (chance == 1) {
            player.inventory.addItem(tier1Chest)
            Chat.sendMessage(player, "${prefix} You've been given a ${Chat.primaryColor}Tier I&7 Loot Crate.")
        } else {
            player.inventory.addItem(tier2Chest)
            Chat.sendMessage(player, "${prefix} You've been given a ${Chat.primaryColor}Tier II&7 Loot Crate.")
        }
        player.playSound(player.location, Sound.NOTE_PLING, 10F, 1F)
    }

    override fun returnTimer(): Int? {
        return if (task != null) {
            task!!.timer
        } else {
            null
        }
    }

    override fun onStart() {
        val tier1Chest = ItemStack(Material.CHEST)
        var meta = tier1Chest.itemMeta
        meta.displayName = Chat.colored("${Chat.primaryColor}Tier I Loot Crate")
        meta.lore = listOf(
            "&7Right-click to redeem a Tier I item!"
        )
        tier1Chest.itemMeta = meta
        val tier2Chest = ItemStack(Material.ENDER_CHEST)
        meta = tier2Chest.itemMeta
        meta.displayName = Chat.colored("${Chat.primaryColor}Tier II Loot Crate")
        meta.lore = listOf(
            "&7Right-click to redeem a Tier II item!"
        )
        tier2Chest.itemMeta = meta
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.getSpecs().contains(player.name)) {
                Chat.sendMessage(player, "${prefix} Players have been given their lootcrates.")
            } else {
                val chance = Random.nextInt(2)
                if (chance == 1) {
                    player.inventory.addItem(tier1Chest)
                    Chat.sendMessage(player, "${prefix} You've been given a ${Chat.primaryColor}Tier I&7 Loot Crate.")
                } else {
                    player.inventory.addItem(tier2Chest)
                    Chat.sendMessage(player, "${prefix} You've been given a ${Chat.primaryColor}Tier II&7 Loot Crate.")
                }
            }
            player.playSound(player.location, Sound.NOTE_PLING, 10F, 1F)
        }
        task = GiveLootCrates()
        task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 20L, 20L)
    }
}