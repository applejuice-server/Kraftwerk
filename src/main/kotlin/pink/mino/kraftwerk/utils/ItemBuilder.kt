package pink.mino.kraftwerk.utils

import com.google.common.collect.Lists
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.math.floor


class PotionBuilder {
    companion object {
        private fun getPotionName(type: PotionEffectType): String? {
            return when (type.name.lowercase(Locale.getDefault())) {
                "speed" -> "Speed"
                "slow" -> "Slowness"
                "fast_digging" -> "Haste"
                "slow_digging" -> "Mining Fatigue"
                "increase_damage" -> "Strength"
                "heal" -> "Instant Health"
                "harm" -> "Instant Damage"
                "jump" -> "Jump Boost"
                "confusion" -> "Nausea"
                "regeneration" -> "Regeneration"
                "damage_resistance" -> "Resistance"
                "fire_resistance" -> "Fire Resistance"
                "water_breathing" -> "Water breathing"
                "invisibility" -> "Invisibility"
                "blindness" -> "Blindness"
                "night_vision" -> "Night Vision"
                "hunger" -> "Hunger"
                "weakness" -> "Weakness"
                "poison" -> "Poison"
                "wither" -> "Wither"
                "health_boost" -> "Health Boost"
                "absorption" -> "Absorption"
                "saturation" -> "Saturation"
                else -> "???"
            }
        }

        private fun getTierString(tier: Int): String? {
            return when (tier) {
                0 -> "I"
                1 -> "II"
                2 -> "III"
                3 -> "IV"
                4 -> "V"
                5 -> "VI"
                6 -> "VII"
                7 -> "VIII"
                8 -> "IX"
                9 -> "X"
                else -> "" + (tier + 1)
            }
        }

        private val SECONDS_PER_HOUR: Long = 3600
        private val SECONDS_PER_MINUTE: Long = 60

        /**
         * Converts the seconds to hours, minutes and seconds.
         *
         * @param ticks the number of seconds
         * @return The converted version.
         */
        private fun potionTicksToString(ticks: Long): String {
            var ticks = ticks
            ticks /= 20
            val hours = floor(ticks / SECONDS_PER_HOUR.toDouble()).toInt().toLong()
            ticks -= hours * SECONDS_PER_HOUR
            val minutes = floor(ticks / SECONDS_PER_MINUTE.toDouble()).toInt().toLong()
            ticks -= minutes * SECONDS_PER_MINUTE
            val seconds = ticks.toInt()
            val output = StringBuilder()
            if (hours > 0) {
                output.append(hours).append(':')
                if (minutes == 0L) {
                    output.append(minutes).append(':')
                }
            }
            output.append(minutes).append(':')
            if (seconds.toString().length == 1) {
                output.append(0)
            }
            output.append(seconds)
            return output.toString()
        }
        fun createPotion(vararg effects: PotionEffect): ItemStack {
            val item = ItemStack(Material.POTION)
            val pot = item.itemMeta as PotionMeta
            val lore: MutableList<String>
            lore = if (pot.hasLore()) {
                pot.lore
            } else {
                Lists.newArrayList()
            }
            for (effect in effects) {
                pot.addCustomEffect(effect, true)
                if (effect.duration > 0) {
                    lore.add(
                        "§7" + getPotionName(effect.type) + " " + getTierString(effect.amplifier) + " (" + potionTicksToString(
                            effect.duration.toLong()
                        ) + ")"
                    )
                } else {
                    lore.add("§7" + getPotionName(effect.type) + " " + getTierString(effect.amplifier))
                }
            }
            pot.lore = lore
            item.itemMeta = pot
            return item
        }
    }
}
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
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
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

    fun setColor(color: Color): ItemBuilder {
        val leather = item.itemMeta as LeatherArmorMeta
        leather.color = color
        item.itemMeta = leather
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