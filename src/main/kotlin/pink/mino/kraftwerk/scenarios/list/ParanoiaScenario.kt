package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import java.util.*
import kotlin.math.floor

class ParanoiaScenario : Scenario(
    "Paranoia",
    "Every time you complete a certain activity, your name or co-ordinates are broadcast in chat. These activities include: Mining Gold/Diamond, going to the nether, crafting a brewing stand or golden apple, and dying.",
    "paranoia",
    Material.EYE_OF_ENDER
) {
    val prefix = "&8[${Chat.primaryColor}Paranoia&8]&7"
    var brokenBlocks: HashMap<UUID, HashSet<Block>> = HashMap<UUID, HashSet<Block>>()

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (brokenBlocks.containsKey(e.player.uniqueId)) {
            if (brokenBlocks[e.player.uniqueId]!!.contains(e.block)) return
        }
        if (e.block.type == Material.DIAMOND_ORE) {
            var diamonds = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.DIAMOND_ORE) {
                            diamonds++
                            if (brokenBlocks.containsKey(e.player.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[e.player.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            Bukkit.broadcastMessage(Chat.colored("$prefix ${PlayerUtils.getPrefix(e.player)}${e.player.name}&7 found &b$diamonds diamonds&7. &8(${Chat.primaryColor}x: ${floor(e.player.location.x)}&7, ${Chat.primaryColor}y: ${floor(e.player.location.y)}&7, ${Chat.primaryColor}z: ${floor(e.player.location.z)}&8)"))
        } else if (e.block.type == Material.GOLD_ORE) {
            var gold = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.GOLD_ORE) {
                            gold++
                            if (brokenBlocks.containsKey(e.player.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[e.player.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            Bukkit.broadcastMessage(Chat.colored("$prefix ${PlayerUtils.getPrefix(e.player)}${e.player.name}&7 found &6$gold gold&7. &8(${Chat.primaryColor}x: ${floor(e.player.location.x)}&7, ${Chat.primaryColor}y: ${floor(e.player.location.y)}&7, ${Chat.primaryColor}z: ${floor(e.player.location.z)}&8)"))
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        Bukkit.broadcastMessage(Chat.colored("$prefix ${PlayerUtils.getPrefix(e.entity)}${e.entity.name}&7 died. &8(${Chat.primaryColor}x: ${floor(e.entity.location.x)}&7, ${Chat.primaryColor}y: ${floor(e.entity.location.y)}&7, ${Chat.primaryColor}z: ${floor(e.entity.location.z)}&8)"))
    }

    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.item.type == Material.GOLDEN_APPLE) Bukkit.broadcastMessage(Chat.colored("$prefix ${PlayerUtils.getPrefix(e.player)}${e.player.name}&7 ate a &6Golden Apple&7. &8(${Chat.primaryColor}x: ${floor(e.player.location.x)}&7, ${Chat.primaryColor}y: ${floor(e.player.location.y)}&7, ${Chat.primaryColor}z: ${floor(e.player.location.z)}&8)"))
    }

    @EventHandler
    fun onPlayerCraft(e: CraftItemEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        when (e.inventory.result.type) {
            Material.ANVIL -> {
                Bukkit.broadcastMessage(Chat.colored("$prefix ${PlayerUtils.getPrefix(e.whoClicked as Player)}${e.whoClicked.name}&7 crafted an &fAnvil&7. &8(${Chat.primaryColor}x: ${floor(e.whoClicked.location.x)}&7, ${Chat.primaryColor}y: ${floor(e.whoClicked.location.y)}&7, ${Chat.primaryColor}z: ${floor(e.whoClicked.location.z)}&8)"))
            }
            Material.GOLDEN_APPLE -> {
                Bukkit.broadcastMessage(Chat.colored("$prefix ${PlayerUtils.getPrefix(e.whoClicked as Player)}${e.whoClicked.name}&7 crafted a &6Golden Apple&7. &8(${Chat.primaryColor}x: ${floor(e.whoClicked.location.x)}&7, ${Chat.primaryColor}y: ${floor(e.whoClicked.location.y)}&7, ${Chat.primaryColor}z: ${floor(e.whoClicked.location.z)}&8)"))
            }
            Material.ENCHANTMENT_TABLE -> {
                Bukkit.broadcastMessage(Chat.colored("$prefix ${PlayerUtils.getPrefix(e.whoClicked as Player)}${e.whoClicked.name}&7 crafted an &dEnchantment Table&7. &8(${Chat.primaryColor}x: ${floor(e.whoClicked.location.x)}&7, ${Chat.primaryColor}y: ${floor(e.whoClicked.location.y)}&7, ${Chat.primaryColor}z: ${floor(e.whoClicked.location.z)}&8)"))
            }
            else -> {}
        }
    }
}