package pink.mino.kraftwerk.commands

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.utils.Chat

class SplitCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        val player = sender as Player
        if (!ConfigOptionHandler.getOption("splitenchants")!!.enabled) {
            Chat.sendMessage(player, "&cSplit Enchants is disabled.")
            return false
        }
        val hand: ItemStack? = player.itemInHand

        if (hand == null || !hand.type.equals(Material.ENCHANTED_BOOK)) {
            Chat.sendMessage(player, "&cYou are not holding an enchanted book.")
            return false
        }
        val handMeta = player.itemInHand.itemMeta as EnchantmentStorageMeta

        val storedEnchants: Map<Enchantment, Int> = handMeta.storedEnchants

        if (storedEnchants.isEmpty()) {
            Chat.sendMessage(player, "&cYou are not holding an enchanted book.")
            return false
        }

        val size = storedEnchants.size
        if (size == 1) {
            Chat.sendMessage(player, "&cThat book only has one enchantment.")
            return false
        }

        if (player.level < size) {
            Chat.sendMessage(player, "&cYou require $size level(s).")
            return false
        }

        player.level = player.level - size
        player.itemInHand = null

        for (entry in storedEnchants) {
            val book = ItemStack(Material.ENCHANTED_BOOK)
            val bookMeta = book.itemMeta as EnchantmentStorageMeta

            bookMeta.addStoredEnchant(entry.key, entry.value, true)
            book.itemMeta = bookMeta

            player.inventory.addItem(book)
        }

        Chat.sendMessage(player, "${Chat.prefix} &7Your enchants have been split.")
        return true
    }
}