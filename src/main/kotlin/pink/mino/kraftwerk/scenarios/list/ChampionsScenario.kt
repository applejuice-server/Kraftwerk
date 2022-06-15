package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import org.bukkit.material.SpawnEgg
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.*
import java.util.*
import kotlin.math.floor

class ChampionsScenario : Scenario(
    "Champions",
    "Adds Hypixel UHC crafts & other features to the game.||||Click for more information.",
    "champions",
    Material.GOLDEN_APPLE
), CommandExecutor {
    val prefix = Chat.colored("&8[&4Champions&8] &7")
    val kits = hashMapOf<UUID, String>()

    init {
        JavaPlugin.getPlugin(Kraftwerk::class.java).getCommand("championsKit").executor = this
    }

    override fun onToggle(to: Boolean) {
        if (!to) {
            Bukkit.resetRecipes()
            val mater = MaterialData(Material.SKULL_ITEM)
            mater.data = 3.toByte()
            val head = ItemStack(Material.GOLDEN_APPLE)
            val meta: ItemMeta = head.itemMeta
            meta.displayName = ChatColor.GOLD.toString() + "Golden Head"
            meta.lore = listOf(ChatColor.DARK_PURPLE.toString() + "Some say consuming the head of a", ChatColor.DARK_PURPLE.toString() + "fallen foe strengthens the blood.")
            head.itemMeta = meta
            val goldenHead: ShapedRecipe = ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', mater)
            Bukkit.getServer().addRecipe(goldenHead)
        } else {
            Bukkit.resetRecipes()
            val vorpalSword = ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.DARK_PURPLE.toString() + "Vorpal Sword")
                .addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 2)
                .addEnchantment(Enchantment.DAMAGE_UNDEAD, 2)
                .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 2)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(vorpalSword).shape(" $ ", " * ", " + ")
                
                .setIngredient('$', Material.BONE)
                .setIngredient('*', Material.IRON_SWORD)
                .setIngredient('+', Material.ROTTEN_FLESH))
            val bookOfSharpening = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Book of Sharpening")
                .toEnchant()
                .addStoredEnchant(Enchantment.DAMAGE_ALL, 1)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(bookOfSharpening).shape("F  ", " PP", " PS")
                .setIngredient('F', Material.FLINT)
                
                .setIngredient('P', Material.PAPER)
                .setIngredient('S', Material.IRON_SWORD)
            )
            val bookOfPower = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Book of Power")
                .toEnchant()
                .addStoredEnchant(Enchantment.ARROW_DAMAGE, 1)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(bookOfPower).shape("F  ", " PP", " PB")
                .setIngredient('F', Material.FLINT)
                
                .setIngredient('P', Material.PAPER)
                .setIngredient('B', Material.BONE)
            )
            val dragonSword = ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.YELLOW.toString() + "Dragon Sword")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(dragonSword).shape(" B ", " S ", "OBO")
                
                .setIngredient('B', Material.BLAZE_POWDER)
                .setIngredient('S', Material.DIAMOND_SWORD)
                .setIngredient('O', Material.OBSIDIAN)
            )
            val leatherEconomy = ItemBuilder(Material.LEATHER)
                .setAmount(8)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(leatherEconomy).shape("/L/", "/L/", "/L/")
                .setIngredient('/', Material.STICK)
                .setIngredient('L', Material.LEATHER)
            )
            val bookOfProtection = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Book of Protection")
                .toEnchant()
                .addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(bookOfProtection).shape("   ", " PP", " PI")
                
                .setIngredient('P', Material.PAPER)
                .setIngredient('I', Material.IRON_INGOT)
            )
            val artemisBook = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Artemis' Book")
                .toEnchant()
                .addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 1)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(artemisBook).shape("   ", " PP", " PA")
                
                .setIngredient('P', Material.PAPER)
                .setIngredient('A', Material.ARROW)
            )
            val dustOfLight = ItemBuilder(Material.GLOWSTONE_DUST)
                .name(ChatColor.DARK_PURPLE.toString() + "Dust of Light")
                .setAmount(8)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(dustOfLight).shape("***", "*F*", "***")
                .setIngredient('*', Material.GLOWSTONE_DUST)
                .setIngredient('F', Material.FLINT)
            )
            val nectar = PotionBuilder.createPotion(PotionEffect(PotionEffectType.REGENERATION, 200, 1, false, true))
            Bukkit.getServer().addRecipe(ShapedRecipe(nectar).shape(" E ", "GMG", " P ")
                
                .setIngredient('P', Material.POTION)
                .setIngredient('E', Material.EMERALD)
                .setIngredient('G', Material.GOLD_INGOT)
                .setIngredient('M', Material.MELON)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.NETHER_WARTS)).shape(" S ", "SNS", " S ")
                
                .setIngredient('N', Material.FERMENTED_SPIDER_EYE)
                .setIngredient('S', Material.SEEDS)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.BLAZE_ROD)).shape("OLO", "OFO", "OLO")
                .setIngredient('O', Material.STAINED_GLASS, 1)
                .setIngredient('L', Material.LAVA_BUCKET)
                .setIngredient('F', Material.FIREWORK)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.COOKED_BEEF, 10)).shape("BBB", "BCB", "BBB")
                .setIngredient('B', Material.RAW_BEEF)
                .setIngredient('C', Material.COAL)
            )
            val potionOfToughness = PotionBuilder.createPotion(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 1, false, true))
            Bukkit.getServer().addRecipe(ShapedRecipe(potionOfToughness).shape(" S ", " W ", " P ")
                
                .setIngredient('P', Material.GLASS_BOTTLE)
                .setIngredient('S', Material.SLIME_BALL)
                .setIngredient('W', Material.WOOL)
            )
            val spikedArmor = ItemBuilder(Material.LEATHER_CHESTPLATE)
                .name(ChatColor.DARK_PURPLE.toString() + "Spiked Armor")
                .addEnchantment(Enchantment.THORNS, 0)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(spikedArmor).shape(" L ", " C ", " P ")
                
                .setIngredient('C', Material.CACTUS)
                .setIngredient('L', Material.WATER_LILY)
                .setIngredient('P', Material.LEATHER_CHESTPLATE)
            )
            val sevenLeagueBoots = ItemBuilder(Material.DIAMOND_BOOTS)
                .name(ChatColor.DARK_PURPLE.toString() + "Seven League Boots")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.PROTECTION_FALL, 3)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(sevenLeagueBoots).shape("FEF", "FDF", "FWF")
                .setIngredient('F', Material.FEATHER)
                .setIngredient('E', Material.ENDER_PEARL)
                .setIngredient('D', Material.DIAMOND_BOOTS)
                .setIngredient('W', Material.WATER_BUCKET)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.IRON_INGOT, 10)).shape("III", "ICI", "III")
                .setIngredient('I', Material.IRON_ORE)
                .setIngredient('C', Material.COAL)
            )
            Bukkit.getServer().addRecipe(ShapelessRecipe(ItemStack(Material.OBSIDIAN)).addIngredient(Material.WATER_BUCKET).addIngredient(Material.LAVA_BUCKET))
            val tarnhelm = ItemBuilder(Material.DIAMOND_HELMET)
                .name(ChatColor.DARK_PURPLE.toString() + "Tarnhelm")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
                .addEnchantment(Enchantment.WATER_WORKER, 1)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(tarnhelm).shape("DID", "DRD", "   ")
                
                .setIngredient('D', Material.DIAMOND)
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('R', Material.REDSTONE_BLOCK)
            )
            val philosophersPickaxe = ItemBuilder(Material.DIAMOND_PICKAXE)
                .name(ChatColor.DARK_PURPLE.toString() + "Philosopher's Pickaxe")
                .addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2)
            philosophersPickaxe.setDurability((philosophersPickaxe.item.durability - 2).toShort())
            Bukkit.getServer().addRecipe(ShapedRecipe(philosophersPickaxe.make()).shape("IGI", "LSL", " S ")
                
                .setIngredient('I', Material.IRON_ORE)
                .setIngredient('G', Material.GOLD_ORE)
                .setIngredient('L', Material.LAPIS_BLOCK)
                .setIngredient('S', Material.STICK)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.EXP_BOTTLE, 8)).shape(" R ", "RPR", " R ")
                
                .setIngredient('R', Material.REDSTONE_BLOCK)
                .setIngredient('P', Material.GLASS_BOTTLE)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.ANVIL)).shape("III", " B ", "III")
                
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('B', Material.IRON_BLOCK)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.ENCHANTMENT_TABLE)).shape(" B ", "ODO", "OXO")
                
                .setIngredient('B', Material.BOOKSHELF)
                .setIngredient('O', Material.OBSIDIAN)
                .setIngredient('D', Material.DIAMOND)
                .setIngredient('X', Material.EXP_BOTTLE)
            )
            val bookOfThoth = ItemBuilder(Material.BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Book of Thoth")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                .addEnchantment(Enchantment.FIRE_ASPECT, 1)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(bookOfThoth).shape("E  ", " PP", " PB")
                
                .setIngredient('E', Material.EYE_OF_ENDER)
                .setIngredient('B', Material.FIREBALL)
                .setIngredient('P', Material.PAPER)
            )
            Bukkit.getServer().addRecipe(ShapelessRecipe(ItemStack(Material.APPLE, 2)).addIngredient(Material.APPLE).addIngredient(Material.INK_SACK, 15))
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.MELON)).shape("BSB", "SAS", "BSB")
                .setIngredient('B', Material.INK_SACK, 15)
                .setIngredient('S', Material.SEEDS)
                .setIngredient('A', Material.APPLE)
            )
            val holyWater = PotionBuilder.createPotion(PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 3, false, true))
            Bukkit.getServer().addRecipe(ShapedRecipe(holyWater).shape("GRG", " D ", " P ")
                
                .setIngredient('G', Material.GOLD_INGOT)
                .setIngredient('R', Material.REDSTONE_BLOCK)
                .setIngredient('D', Material.GOLD_RECORD)
                .setIngredient('P', Material.GLASS_BOTTLE)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.GOLDEN_APPLE)).shape(" G ", "GAG", " G ")
                
                .setIngredient('G', Material.GOLD_INGOT)
                .setIngredient('A', Material.APPLE)
            )
            val goldenHead = ItemBuilder(Material.SKULL_ITEM)
                .toSkull()
                .setOwner("PhantomTupac")
                .name("&6Golden Head")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(goldenHead).shape("GGG", "GHG", "GGG")
                .setIngredient('H', Material.SKULL_ITEM)
                .setIngredient('G', Material.GOLD_INGOT)
            )
            val pandorasBox = ItemBuilder(Material.CHEST)
                .name(ChatColor.DARK_PURPLE.toString() + "Pandora's Box")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(pandorasBox).shape("GGG", "GHG", "GGG")
                .setIngredient('G', Material.GOLD_INGOT)
                .setIngredient('H', Material.SKULL_ITEM)
            )
            val panacea = PotionBuilder.createPotion(PotionEffect(PotionEffectType.HEAL, 1, 3, false, true))
            Bukkit.getServer().addRecipe(ShapedRecipe(panacea).shape("   ", "HGH", " P ")
                
                .setIngredient('H', Material.SKULL_ITEM)
                .setIngredient('P', Material.GLASS_BOTTLE)
                .setIngredient('G', Material.SPECKLED_MELON)
            )
            val cupidsBow = ItemBuilder(Material.BOW)
                .name(ChatColor.DARK_PURPLE.toString() + "Cupid's Bow")
                .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                .addEnchantment(Enchantment.ARROW_FIRE, 1)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(cupidsBow).shape(" B ", "HBH", " L ")
                
                .setIngredient('B', Material.BOW)
                .setIngredient('H', Material.SKULL_ITEM)
                .setIngredient('L', Material.LAVA_BUCKET)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.ARROW, 20)).shape("FFF", "SSS", "RRR")
                .setIngredient('F', Material.FLINT)
                .setIngredient('R', Material.FEATHER)
                .setIngredient('S', Material.STICK)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.LEATHER)).shape("LLL", "SLS", "I/I")
                
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('L', Material.LEATHER)
            )
            val potionOfVelocity = PotionBuilder.createPotion(PotionEffect(PotionEffectType.SPEED, 20 * 12, 3, false, true))
            Bukkit.getServer().addRecipe(ShapedRecipe(potionOfVelocity).shape(" C ", " S ", " P ")
                
                .setIngredient('C', Material.COCOA)
                .setIngredient('S', Material.SUGAR)
                .setIngredient('P', Material.GLASS_BOTTLE)
            )
            val fenrir = SpawnEgg(EntityType.WOLF).toItemStack()
            Bukkit.getServer().addRecipe(ShapedRecipe(fenrir).shape("LLL", "BXB", "LLL")
                .setIngredient('L', Material.LEATHER)
                .setIngredient('X', Material.EXP_BOTTLE)
                .setIngredient('B', Material.BONE)
            )
            val forge = ItemBuilder(Material.FURNACE)
                .name(ChatColor.DARK_PURPLE.toString() + "Forge")
                .addLore("&7Instantly smelts items. Breaks after 10 uses.")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(forge).shape("CCC", "COC", "CCC")
                .setIngredient('C', Material.COBBLESTONE)
                .setIngredient('O', Material.COAL_BLOCK)
            )
            val quickPick = ItemBuilder(Material.IRON_PICKAXE)
                .name(ChatColor.DARK_PURPLE.toString() + "Quick Pick")
                .addEnchantment(Enchantment.DIG_SPEED, 0)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(quickPick).shape("III", "CSC", " S ")
                .setIngredient('I', Material.IRON_ORE)
                .setIngredient('C', Material.COAL)
                .setIngredient('S', Material.STICK)

            )
            val lumberjackAxe = ItemBuilder(Material.IRON_AXE)
                .name(ChatColor.DARK_PURPLE.toString() + "Lumberjack Axe")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(lumberjackAxe).shape("IIF", "IS ", " S ")
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('F', Material.FLINT)
                .setIngredient('S', Material.STICK)
                
            )
            val apprenticeHelmet = ItemBuilder(Material.IRON_HELMET)
                .name(ChatColor.DARK_PURPLE.toString() + "Apprentice Helmet")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
                .addEnchantment(Enchantment.PROTECTION_FIRE, 0)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 0)
                .addEnchantment(Enchantment.PROTECTION_PROJECTILE, 0)
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(apprenticeHelmet).shape("III", "IRI", "   ")
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('R', Material.REDSTONE_TORCH_ON)
                
            )
            val apprenticeSword = ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.DARK_PURPLE.toString() + "Apprentice Sword")
                .addLore("&7Gains &fSharpness I&7 at Final Heal&7.")
                .addLore("&7Gains &fSharpness II&7 at PvP&7.")
                .addLore("&7Gains &fSharpness III&7 at Meetup&7.")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(apprenticeSword).shape(" R ", " I ", " R ")
                .setIngredient('I', Material.IRON_SWORD)
                
                .setIngredient('R', Material.REDSTONE_BLOCK)
            )
            val apprenticeBow = ItemBuilder(Material.BOW)
                .name(ChatColor.DARK_PURPLE.toString() + "Apprentice Bow")
                .addLore("&7Gains &fPower I&7 at Final Heal&7.")
                .addLore("&7Gains &fPower II&7 at PvP&7.")
                .addLore("&7Gains &fPower III&7 at Meetup&7.")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(apprenticeBow).shape(" RS", "R S", " RS")
                .setIngredient('R', Material.REDSTONE_TORCH_ON)
                
                .setIngredient('S', Material.STICK)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.GOLD_INGOT, 10))
                .shape("GGG", "GCG", "GGG")
                .setIngredient('G', Material.GOLD_INGOT)
                .setIngredient('C', Material.COAL)
            )
            Bukkit.getServer().addRecipe(ShapedRecipe(ItemStack(Material.SUGAR_CANE, 4))
                .shape(" G ", "SRS", "   ")
                .setIngredient('G', Material.SAPLING)
                .setIngredient('R', Material.SUGAR)
                
                .setIngredient('S', Material.SEEDS)
            )



        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (e.entity.killer == null) return
        if (e.entity.killer !is Player) return
        e.drops.add(ItemStack(Material.GOLD_NUGGET, 5))
        e.droppedExp = (e.droppedExp + floor((e.droppedExp * 0.20))).toInt()
        e.entity.killer.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 2, 0, false, true))
        e.entity.killer.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 2, 0, false, true))
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.item == null) return
        if (e.item.type == Material.SKULL_ITEM && e.item.itemMeta.displayName == Chat.colored("&6Golden Head")) {
            e.player.inventory.remove(e.item)
            Chat.sendMessage(e.player, "$prefix You ate a &6Golden Head&7 and gained 5 seconds of Regeneration III & 2 minutes of Absorption.")
            e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 0, false, true))
            e.player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 0, false, true))
        } else if (e.item.type == Material.SKULL_ITEM) {
            e.player.inventory.remove(e.item)
            Chat.sendMessage(e.player, "$prefix You ate a player head and gained 4 seconds of Regeneration III! You gained 9 seconds of Speed II")
            e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 0, false, true))
            e.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 9, 0, false, true))
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (!enabled) return
        if (e.entity !is Player) return
        if (
            e.cause == EntityDamageEvent.DamageCause.FALL ||
            e.cause == EntityDamageEvent.DamageCause.CONTACT ||
            e.cause == EntityDamageEvent.DamageCause.VOID ||
            e.cause == EntityDamageEvent.DamageCause.SUFFOCATION ||
            e.cause == EntityDamageEvent.DamageCause.LIGHTNING ||
            e.cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
            e.cause == EntityDamageEvent.DamageCause.FIRE ||
            e.cause == EntityDamageEvent.DamageCause.LAVA ||
            e.cause == EntityDamageEvent.DamageCause.DROWNING ||
            e.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            e.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
            e.cause == EntityDamageEvent.DamageCause.MAGIC
        ) {
            e.damage = e.damage - (e.damage * 0.16)
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (e.player !is Player) return
        e.player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 400, 0, false, false))
        if (e.block.type == Material.GOLD_ORE || e.block.type == Material.IRON_ORE) {
            val chance = (0..100).random()
            if (chance <= 8) {
                e.player.location.world.dropItem(e.player.location, ItemBuilder(e.block.type).make())
            }
        }
        if (e.block.type == Material.SAND || e.block.type == Material.GRAVEL || e.block.type == Material.OBSIDIAN) {
            val chance = (0..100).random()
            if (chance <= 20) {
                e.player.location.world.dropItem(e.player.location, ItemBuilder(e.block.type).make())
            }
        }
    }

    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) continue
            player.maxHealth = 40.0
            player.health = 40.0
            player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 4800, 0, false, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 240 * 20, 0, false, false))
            if (kits[player.uniqueId] == null) {
                kits[player.uniqueId] = "leather"
            }
            if (kits[player.uniqueId] == "leather") {
                val helmet = ItemBuilder(Material.LEATHER_HELMET)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                val chestplate = ItemBuilder(Material.LEATHER_CHESTPLATE)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                val leggings = ItemBuilder(Material.LEATHER_LEGGINGS)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                val boots = ItemBuilder(Material.LEATHER_BOOTS)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                player.inventory.helmet = helmet
                player.inventory.chestplate = chestplate
                player.inventory.leggings = leggings
                player.inventory.boots = boots
            } else if (kits[player.uniqueId] == "enchanter") {
                val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.BOOK, 4),
                    ItemStack(Material.EXP_BOTTLE, 15),
                    ItemStack(Material.INK_SACK, 18, 4),
                    pickaxe
                ))
            } else if (kits[player.uniqueId] == "archer") {
                val shovel = ItemBuilder(Material.STONE_SPADE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.STRING, 6),
                    ItemStack(Material.FEATHER, 9),
                    shovel
                ))
            } else if (kits[player.uniqueId] == "stoneGear") {
                val shovel = ItemBuilder(Material.STONE_SPADE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                val axe = ItemBuilder(Material.STONE_AXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                val sword = ItemBuilder(Material.STONE_SWORD)
                    .make()
                PlayerUtils.bulkItems(player, arrayListOf(
                    shovel,
                    pickaxe,
                    axe,
                    sword
                ))
            } else if (kits[player.uniqueId] == "lunchBox") {
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.COOKED_BEEF, 9),
                    ItemStack(Material.CARROT, 12),
                    ItemStack(Material.MELON, 2),
                    ItemStack(Material.APPLE, 3),
                    ItemStack(Material.GOLD_INGOT, 3),
                ))
            } else if (kits[player.uniqueId] == "looter") {
                val sword = ItemBuilder(Material.STONE_SWORD)
                    .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 1)
                    .make()
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.BONE, 3),
                    ItemStack(Material.SLIME_BALL, 3),
                    ItemStack(Material.SULPHUR, 2),
                    ItemStack(Material.SPIDER_EYE, 2),
                    sword
                ))
            } else if (kits[player.uniqueId] == "ecologist") {
                val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.VINE, 21),
                    ItemStack(Material.WATER_LILY, 64),
                    ItemStack(Material.SUGAR_CANE, 12),
                    pickaxe
                ))
            } else if (kits[player.uniqueId] == "farmer") {
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.IRON_HOE),
                    ItemStack(Material.MELON, 3),
                    ItemStack(Material.CARROT, 3),
                    ItemStack(Material.INK_SACK, 15),
                ))
            } else if (kits[player.uniqueId] == "horseman") {
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.LEATHER, 12),
                    ItemStack(Material.HAY_BLOCK, 1),
                    ItemStack(Material.STRING, 4),
                    ItemStack(Material.IRON_BARDING),
                    SpawnEgg(EntityType.HORSE).toItemStack()
                ))
            } else if (kits[player.uniqueId] == "trapper") {
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.PISTON_BASE, 8),
                    ItemStack(Material.PISTON_STICKY_BASE, 8),
                    ItemStack(Material.REDSTONE, 25),
                    ItemStack(Material.LOG, 16)
                ))
            }
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command.")
            return true
        }
        if (!enabled) {
            sender.sendMessage("$prefix Champions is not enabled.")
            return true
        }
        val gui = GuiBuilder().rows(2).name("&cChampions Kit Selector").owner(sender)
        val leatherKit = ItemBuilder(Material.LEATHER_CHESTPLATE)
            .name("&aLeather Armor")
            .addLore("&6Kit:")
            .addLore("&7Spawn with a full set of ")
            .addLore("&7protection III leather armor.")
            .make()
        val enchanterKit = ItemBuilder(Material.BOOK)
            .name("&aEnchanting Set")
            .addLore("&6Kit:")
            .addLore("&7Spawn with 4 books, 15")
            .addLore("&7bottles, 18 lapis and an")
            .addLore("&7efficiency 3, unbreaking 1 stone")
            .addLore("&7pickaxe.")
        val archerKit = ItemBuilder(Material.BOW)
            .name("&aArcher Set")
            .addLore("&6Kit:")
            .addLore("&7Spawn with 6 strings, 9")
            .addLore("&7feathers and an efficiency 3,")
            .addLore("&7unbreaking 1 stone shovel.")
            .make()
        val stoneGear = ItemBuilder(Material.STONE_PICKAXE)
            .name("&aStone Gear")
            .addLore("&6Kit:")
            .addLore("&7Spawn with a full set of stone")
            .addLore("&7tools with efficiency III and")
            .addLore("&7unbreaking I.")
            .make()
        val lunchBox = ItemBuilder(Material.APPLE)
            .name("&aLunch Box")
            .addLore("&6Kit:")
            .addLore("&7Spawn with 9 steaks, 12")
            .addLore("&7carrots, 2 melon slices, 2 gold")
            .addLore("&7ingots and 3 apples.")
            .make()
        val looter = ItemBuilder(Material.BONE)
            .name("&aLooter")
            .addLore("&6Kit:")
            .addLore("&7Spawn with 3 bones, 3 slime")
            .addLore("&7balls, 2 gunpowder, 2 spider")
            .addLore("&7eyes and a looting 1 stone")
            .addLore("&7sword.")
            .make()
        val ecologist = ItemBuilder(Material.IRON_AXE)
            .name("&aEcologist")
            .addLore("&6Kit:")
            .addLore("&7Spawn with 21 vines, 64 lily")
            .addLore("&7pads, 12 sugar cane and an")
            .addLore("&7efficiency 3, unbreaking 1 stone")
            .addLore("&7pickaxe.")
            .make()
        val farmer = ItemBuilder(Material.SEEDS)
            .name("&aFarmer")
            .addLore("&6Kit:")
            .addLore("&7Spawn with an iron hoe, 3")
            .addLore("&7melon slices, 3 carrots and 4")
            .addLore("&7bonemeal.")
            .make()
        val horseman = ItemBuilder(Material.SADDLE)
            .name("&aHorseman")
            .addLore("&6Kit:")
            .addLore("&7Spawn with 12 leather, 1 hay")
            .addLore("&7bale, 4 string, gold horse armor")
            .addLore("&7and a horse spawn egg.")
            .make()
        val trapper = ItemBuilder(Material.PISTON_BASE)
            .name("&aTrapper")
            .addLore("&6Kit:")
            .addLore("&7Spawn with 8 pistons, 8 sticky")
            .addLore("&7pistons, 25 redstone and 16 oak")
            .addLore("&7logs.")
            .make()
        gui.item(0, leatherKit).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "leather") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "leather"
            Chat.sendMessage(sender, "&aYou have selected the &6Leather Armor&a kit.")
        }
        gui.item(1, enchanterKit.make()).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "enchanter") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "enchanter"
            Chat.sendMessage(sender, "&aYou have selected the &6Enchanting Set&a kit.")
        }
        gui.item(2, archerKit).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "archer") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "archer"
            Chat.sendMessage(sender, "&aYou have selected the &6Archer Set&a kit.")
        }
        gui.item(3, stoneGear).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "stoneGear") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "stoneGear"
            Chat.sendMessage(sender, "&aYou have selected the &6Stone Gear&a kit.")
        }
        gui.item(4, lunchBox).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "lunchBox") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "lunchBox"
            Chat.sendMessage(sender, "&aYou have selected the &6Lunch Box&a kit.")
        }
        gui.item(5, looter).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "looter") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "looter"
            Chat.sendMessage(sender, "&aYou have selected the &6Looter&a kit.")
        }
        gui.item(6, ecologist).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "ecologist") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "ecologist"
            Chat.sendMessage(sender, "&aYou have selected the &6Ecologist&a kit.")
        }
        gui.item(7, farmer).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "farmer") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "farmer"
            Chat.sendMessage(sender, "&aYou have selected the &6Farmer&a kit.")
        }
        gui.item(8, horseman).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "horseman") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "horseman"
            Chat.sendMessage(sender, "&aYou have selected the &6Horseman&a kit.")
        }
        gui.item(9, trapper).onClick runnable@ {
            if (this.kits[sender.uniqueId] == "trapper") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "trapper"
            Chat.sendMessage(sender, "&aYou have selected the &6Trapper&a kit.")
        }
        sender.openInventory(gui.make())
        return true
    }
}