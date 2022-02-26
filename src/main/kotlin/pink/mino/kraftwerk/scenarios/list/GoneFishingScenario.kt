package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat

class GoneFishingScenario : Scenario(
    "GoneFishing",
    "Enchanting is disabled, so you must fish to get enchantments!",
    "gonefishing",
    Material.FISHING_ROD
) {
    override fun onStart() {
        val fishingRod = ItemStack(Material.FISHING_ROD)
        val meta = fishingRod.itemMeta
        meta.addEnchant(Enchantment.LURE, 8, true)
        meta.addEnchant(Enchantment.LUCK, 100, true)
        meta.spigot().isUnbreakable = true
        meta.displayName = Chat.colored("&cGone Fishin'!")
        fishingRod.itemMeta = meta
        for (player in Bukkit.getOnlinePlayers()) {
            player.level = 25000
            player.inventory.addItem(fishingRod)
            player.inventory.addItem(ItemStack(Material.ANVIL, 64))
        }
    }

    override fun givePlayer(player: Player) {
        val fishingRod = ItemStack(Material.FISHING_ROD)
        val meta = fishingRod.itemMeta
        meta.addEnchant(Enchantment.LURE, 8, true)
        meta.addEnchant(Enchantment.LUCK, 100, true)
        meta.spigot().isUnbreakable = true
        meta.displayName = Chat.colored("&cGone Fishin'!")
        fishingRod.itemMeta = meta
        player.level = 25000
        player.inventory.addItem(fishingRod)
        player.inventory.addItem(ItemStack(Material.ANVIL, 64))
    }

    @EventHandler
    fun onEnchant(e: EnchantItemEvent) {
        if (enabled) {
            e.isCancelled = true
            Chat.sendMessage(e.enchanter, "&cYou can't enchant this item in a Gone Fishin' game!")
        }
    }
}