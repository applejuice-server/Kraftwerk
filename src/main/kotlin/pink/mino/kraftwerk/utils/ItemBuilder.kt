package pink.mino.kraftwerk.utils

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ItemBuilder(material: Material) {
    val item: ItemStack = ItemStack(material)
    var meta: ItemMeta = item.itemMeta

    fun name(name: String): ItemBuilder {
        meta.displayName = Chat.colored(name)
        item.itemMeta = meta
        return this
    }
    fun addLore(line: String): ItemBuilder {
        meta.lore.add(line)
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
    fun toPotion(): ItemBuilder {
        if (item.type != Material.POTION) throw Error("You can't do this, this isn't a potion.")
        meta = meta as PotionMeta
        item.itemMeta = meta
        return this
    }
    fun toEnchant(): ItemBuilder {
        if (item.type != Material.ENCHANTED_BOOK) throw Error("You can't do this, this isn't an enchant book..")
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
    fun addEffect(potionEffect: PotionEffect): ItemBuilder {
        if (meta is PotionMeta) {
            (meta as PotionMeta).addCustomEffect(potionEffect, true)
        } else {
            throw Error("You're attempting to add an effect to something that isn't a potion.")
        }
        item.itemMeta = meta
        return this
    }
    fun setMainEffect(potionEffectType: PotionEffectType): ItemBuilder {
        if (meta is PotionMeta) {
            (meta as PotionMeta).setMainEffect(potionEffectType)
        } else {
            throw Error("You're attempting to set a main effect to something that isn't a potion.")
        }
        item.itemMeta = meta
        return this
    }
    fun make(): ItemStack {
        return item
    }
}