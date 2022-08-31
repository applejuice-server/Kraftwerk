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
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (!ConfigOptionHandler.getOption("splitenchants")!!.enabled) {
            Chat.sendMessage(sender, "&cSplit Enchants is disabled.")
            return false
        }
        val hand: ItemStack? = sender.itemInHand

        if (hand == null || !hand.type.equals(Material.ENCHANTED_BOOK)) {
            Chat.sendMessage(sender, "&cYou are not holding an enchanted book.")
            return false
        }
        val handMeta = sender.itemInHand.itemMeta as EnchantmentStorageMeta

        val storedEnchants: Map<Enchantment, Int> = handMeta.storedEnchants

        if (storedEnchants.isEmpty()) {
            Chat.sendMessage(sender, "&cYou are not holding an enchanted book.")
            return false
        }

        val size = storedEnchants.size
        if (size == 1) {
            Chat.sendMessage(sender, "&cThat book only has one enchantment.")
            return false
        }

        if (sender.level < size) {
            Chat.sendMessage(sender, "&cYou require $size level(s).")
            return false
        }

        sender.level = sender.level - size
        sender.itemInHand = null

        for (entry in storedEnchants) {
            val book = ItemStack(Material.ENCHANTED_BOOK)
            val bookMeta = book.itemMeta as EnchantmentStorageMeta

            bookMeta.addStoredEnchant(entry.key, entry.value, true)
            book.itemMeta = bookMeta

            sender.inventory.addItem(book)
        }

        Chat.sendMessage(sender, "${Chat.dash} &7Your enchants have been split.")
        return true
    }
}