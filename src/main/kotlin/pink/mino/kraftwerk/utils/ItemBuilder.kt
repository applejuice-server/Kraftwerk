package pink.mino.kraftwerk.utils

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta


class ItemBuilder(material: Material) {
    var item: ItemStack = ItemStack(material)
    var meta: ItemMeta = item.itemMeta

    fun name(name: String): ItemBuilder {
        meta.displayName = Chat.colored(name)
        item.itemMeta = meta
        return this
    }
    fun addLore(line: String): ItemBuilder {
        var lore = meta.lore
        if (lore == null) lore = ArrayList()
        lore.add(Chat.colored(line))
        meta.lore = lore
        item.itemMeta = meta
        return this
    }
    fun removeLore(index: Int): ItemBuilder {
        meta.lore.removeAt(index)
        item.itemMeta = meta
        return this
    }
    fun clearLore(): ItemBuilder {
        meta.lore.clear()
        item.itemMeta = meta
        return this
    }
    fun addEnchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        meta.addEnchant(enchantment, level, true)
        item.itemMeta = meta
        return this
    }
    fun isUnbreakable(boolean: Boolean): ItemBuilder {
        meta.spigot().isUnbreakable = boolean
        item.itemMeta = meta
        return this
    }
    fun noAttributes(): ItemBuilder {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        item.itemMeta = meta
        return this
    }
    fun setDurability(durability: Short): ItemBuilder {
        item.durability = durability
        return this
    }
    fun setAmount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }
    fun toSkull(): ItemBuilder {
        if (item.type != Material.SKULL_ITEM) throw Error("You can't do this, this isn't a head.")
        meta = meta as SkullMeta
        item = ItemStack(Material.SKULL_ITEM, 1, 3)
        item.itemMeta = meta
        return this
    }
    fun toEnchant(): ItemBuilder {
        if (item.type != Material.ENCHANTED_BOOK) throw Error("You can't do this, this isn't an enchant book.")
        meta = meta as EnchantmentStorageMeta
        item.itemMeta = meta
        return this
    }
    fun addStoredEnchant(enchantment: Enchantment, level: Int): ItemBuilder {
        if (meta is EnchantmentStorageMeta) {
            (meta as EnchantmentStorageMeta).addStoredEnchant(enchantment, level, true)
        } else {
            throw Error("You're attempting to add a stored enchant to something that isn't an enchant book.")
        }
        item.itemMeta = meta
        return this
    }
    fun setOwner(owner: String): ItemBuilder {
        if (meta is SkullMeta) {
            (meta as SkullMeta).owner = owner
        } else {
            throw Error("You're attempting to set the owner to something that isn't a skull.")
        }
        item.itemMeta = meta
        return this
    }
    fun make(): ItemStack {
        return item
    }
}