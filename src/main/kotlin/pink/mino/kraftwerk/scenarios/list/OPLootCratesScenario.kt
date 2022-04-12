package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class OPLootCratesScenario : Scenario(
    "OP Loot Crates",
    "Like Loot Crates, except the drops are even more overpowered.",
    "oplootcrates",
    Material.ENDER_CHEST
) {

    fun generateTier1(): ArrayList<ItemStack> {
        val list = arrayListOf<ItemStack>(
            ItemStack(Material.DIAMOND_HELMET),
            ItemStack(Material.DIAMOND_BOOTS),
            ItemStack(Material.DIAMOND_PICKAXE),
            ItemStack(Material.ENCHANTMENT_TABLE),
            ItemStack(Material.GOLDEN_APPLE),
            ItemStack(Material.DIAMOND, 5),
            ItemStack(Material.APPLE, 8),
            ItemStack(Material.GOLD_INGOT, 12),
            ItemStack(Material.BOOK, 10),
            ItemStack(Material.LAPIS_BLOCK, 3),
        )
        val sharpness2 = ItemStack(Material.ENCHANTED_BOOK)
        val sharpness2Meta = sharpness2.itemMeta as EnchantmentStorageMeta
        sharpness2Meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 2, true)
        sharpness2.itemMeta = sharpness2Meta
        val power2 = ItemStack(Material.ENCHANTED_BOOK)
        val power2Meta = power2.itemMeta as EnchantmentStorageMeta
        power2Meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 2, true)
        power2.itemMeta = power2Meta
        val protection2 = ItemStack(Material.ENCHANTED_BOOK)
        val protection2Meta = protection2.itemMeta as EnchantmentStorageMeta
        protection2Meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        protection2.itemMeta = protection2Meta
        list.add(sharpness2)
        list.add(protection2)
        list.add(power2)
        return list
    }

    // - 4 gold blocks
    // - 2 diamond blocks
    // - potion of speed 1
    // - prot 2 dia chest
    // - prot 2 dia legs
    // - prot 2 dia helm
    // - prot 2 dia boots
    // - sharp 3 dia
    // - prot 4 book
    // - proj prot 4 book
    // - sharp 4 book
    // - fortune 3 book
    // - power 4 book

    fun generateTier2(): ArrayList<ItemStack> {
        val list = arrayListOf<ItemStack>(
            ItemStack(Material.GOLD_BLOCK, 2),
            ItemStack(Material.DIAMOND_BLOCK, 2),
        )
        val speed1 = Potion(PotionType.SPEED, 2)
        list.add(speed1.toItemStack(1))
        val chestplate = ItemStack(Material.DIAMOND_CHESTPLATE)
        val chestplateMeta = chestplate.itemMeta
        chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        chestplate.itemMeta = chestplateMeta
        val leggings = ItemStack(Material.DIAMOND_LEGGINGS)
        val leggingsMeta = chestplate.itemMeta
        leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        leggings.itemMeta = leggingsMeta
        val helmet = ItemStack(Material.DIAMOND_HELMET)
        val helmetMeta = chestplate.itemMeta
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        helmet.itemMeta = helmetMeta
        val boots = ItemStack(Material.DIAMOND_BOOTS)
        val bootsMeta = boots.itemMeta
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        boots.itemMeta = bootsMeta
        list.add(boots)
        list.add(leggings)
        list.add(chestplate)
        list.add(helmet)
        val sword = ItemStack(Material.DIAMOND_SWORD)
        val swordMeta = sword.itemMeta
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 2, true)
        sword.itemMeta = swordMeta
        list.add(sword)

        val protection4 = ItemStack(Material.ENCHANTED_BOOK)
        val protection4Meta = protection4.itemMeta as EnchantmentStorageMeta
        protection4Meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true)
        protection4.itemMeta = protection4Meta

        val projProt4 = ItemStack(Material.ENCHANTED_BOOK)
        val projProt4Meta = projProt4.itemMeta as EnchantmentStorageMeta
        projProt4Meta.addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true)
        projProt4.itemMeta = projProt4Meta

        val sharp4 = ItemStack(Material.ENCHANTED_BOOK)
        val sharp4Meta = sharp4.itemMeta as EnchantmentStorageMeta
        sharp4Meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 3, true)
        sharp4.itemMeta = sharp4Meta

        val power4 = ItemStack(Material.ENCHANTED_BOOK)
        val power4Meta = power4.itemMeta as EnchantmentStorageMeta
        power4Meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 3, true)
        power4.itemMeta = power4Meta

        val fortune3 = ItemStack(Material.ENCHANTED_BOOK)
        val fortune3Meta = fortune3.itemMeta as EnchantmentStorageMeta
        fortune3Meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true)
        fortune3.itemMeta = fortune3Meta

        list.add(protection4)
        list.add(sharp4)
        list.add(projProt4)
        list.add(power4)
        list.add(fortune3)
        return list
    }

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
                val stack = generateTier1()[Random.nextInt(generateTier1().size)]
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
                val stack = generateTier2()[Random.nextInt(generateTier2().size)]
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
        GiveLootCrates().runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 20L, 20L)
    }
}