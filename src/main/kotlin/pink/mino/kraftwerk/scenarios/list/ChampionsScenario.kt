package pink.mino.kraftwerk.scenarios.list

import com.google.common.collect.Lists
import me.lucko.helper.Schedulers
import net.minecraft.server.v1_8_R3.*
import org.bukkit.*
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.block.Furnace
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.*
import org.bukkit.inventory.ItemStack
import org.bukkit.material.SpawnEgg
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.Events
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.*
import java.util.*
import kotlin.math.floor


class ChampionsScenario : Scenario(
    "Champions",
    "Adds Hypixel UHC crafts & other features to the game.",
    "champions",
    Material.GOLDEN_APPLE
), CommandExecutor {
    val prefix = Chat.colored("&8[${Chat.primaryColor}Champions&8] &7")
    val kits = hashMapOf<UUID, String>()

    init {
        JavaPlugin.getPlugin(Kraftwerk::class.java).getCommand("championsKit").executor = this
    }

    override fun onToggle(to: Boolean) {
        if (!to) {
            Bukkit.resetRecipes()
            JavaPlugin.getPlugin(Kraftwerk::class.java).addRecipes()
        } else {
            Bukkit.resetRecipes()
            val vorpalSword = ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.DARK_PURPLE.toString() + "Vorpal Sword")
                .addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 2)
                .addEnchantment(Enchantment.DAMAGE_UNDEAD, 2)
                .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 2)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(vorpalSword).shape(" $ ", " * ", " + ")

                    .setIngredient('$', Material.BONE)
                    .setIngredient('*', Material.IRON_SWORD)
                    .setIngredient('+', Material.ROTTEN_FLESH)
            )
            val bookOfSharpening = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Book of Sharpening")
                .toEnchant()
                .addStoredEnchant(Enchantment.DAMAGE_ALL, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(bookOfSharpening).shape("F  ", " PP", " PS")
                    .setIngredient('F', Material.FLINT)

                    .setIngredient('P', Material.PAPER)
                    .setIngredient('S', Material.IRON_SWORD)
            )
            val bookOfPower = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Book of Power")
                .toEnchant()
                .addStoredEnchant(Enchantment.ARROW_DAMAGE, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(bookOfPower).shape("F  ", " PP", " PB")
                    .setIngredient('F', Material.FLINT)

                    .setIngredient('P', Material.PAPER)
                    .setIngredient('B', Material.BONE)
            )
            var dragonSword = ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.GREEN.toString() + "Dragon Sword")
                .make()
            val stack = CraftItemStack.asNMSCopy(dragonSword)
            val compound: NBTTagCompound = if (stack.hasTag()) stack.tag else NBTTagCompound()
            val modifiers = NBTTagList()
            val damage: NBTTagCompound = NBTTagCompound()
            damage.set("AttributeName", NBTTagString("generic.attackDamage"))
            damage.set("Name", NBTTagString("generic.attackDamage"))
            damage.set("Amount", NBTTagInt(8))
            damage.set("Operation", NBTTagInt(0))
            damage.set("UUIDLeast", NBTTagInt(894654))
            damage.set("UUIDMost", NBTTagInt(2872))
            modifiers.add(damage)
            compound.set("AttributeModifiers", modifiers)
            stack.tag = compound
            dragonSword = CraftItemStack.asBukkitCopy(stack)
            Bukkit.getServer().addRecipe(
                ShapedRecipe(dragonSword).shape(" B ", " S ", "OBO")
                    .setIngredient('B', Material.BLAZE_POWDER)
                    .setIngredient('S', Material.DIAMOND_SWORD)
                    .setIngredient('O', Material.OBSIDIAN)
            )
            val leatherEconomy = ItemBuilder(Material.LEATHER)
                .setAmount(8)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(leatherEconomy).shape("/L/", "/L/", "/L/")
                    .setIngredient('/', Material.STICK)
                    .setIngredient('L', Material.LEATHER)
            )
            val bookOfProtection = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Book of Protection")
                .toEnchant()
                .addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(bookOfProtection).shape("   ", " PP", " PI")

                    .setIngredient('P', Material.PAPER)
                    .setIngredient('I', Material.IRON_INGOT)
            )
            val artemisBook = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.DARK_PURPLE.toString() + "Artemis' Book")
                .toEnchant()
                .addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(artemisBook).shape("   ", " PP", " PA")

                    .setIngredient('P', Material.PAPER)
                    .setIngredient('A', Material.ARROW)
            )
            val dragonArmor = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .name(ChatColor.YELLOW.toString() + "Dragon Armor")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(dragonArmor).shape(" B ", " D ", "OAO")
                    .setIngredient('B', Material.MAGMA_CREAM)
                    .setIngredient('D', Material.DIAMOND_CHESTPLATE)
                    .setIngredient('O', Material.OBSIDIAN)
                    .setIngredient('A', Material.ANVIL)
            )
            val dustOfLight = ItemBuilder(Material.GLOWSTONE_DUST)
                .name(ChatColor.DARK_PURPLE.toString() + "Dust of Light")
                .setAmount(8)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(dustOfLight).shape("***", "*F*", "***")
                    .setIngredient('*', Material.GLOWSTONE_DUST)
                    .setIngredient('F', Material.FLINT)
            )
            val nectar = PotionBuilder.createPotion(PotionEffect(PotionEffectType.REGENERATION, 200, 1, false, true))
            Bukkit.getServer().addRecipe(
                ShapedRecipe(nectar).shape(" E ", "GMG", " P ")

                    .setIngredient('P', Material.POTION)
                    .setIngredient('E', Material.EMERALD)
                    .setIngredient('G', Material.GOLD_INGOT)
                    .setIngredient('M', Material.MELON)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.NETHER_STALK)).shape(" S ", "SNS", " S ")

                    .setIngredient('N', Material.FERMENTED_SPIDER_EYE)
                    .setIngredient('S', Material.SEEDS)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.BLAZE_ROD)).shape("OLO", "OFO", "OLO")
                    .setIngredient('O', Material.STAINED_GLASS, 1)
                    .setIngredient('L', Material.LAVA_BUCKET)
                    .setIngredient('F', Material.FIREWORK)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.COOKED_BEEF, 10)).shape("BBB", "BCB", "BBB")
                    .setIngredient('B', Material.RAW_BEEF)
                    .setIngredient('C', Material.COAL)
            )
            val potionOfToughness =
                PotionBuilder.createPotion(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 1, false, true))
            Bukkit.getServer().addRecipe(
                ShapedRecipe(potionOfToughness).shape(" S ", " W ", " P ")

                    .setIngredient('P', Material.GLASS_BOTTLE)
                    .setIngredient('S', Material.SLIME_BALL)
                    .setIngredient('W', Material.WOOL)
            )
            val spikedArmor = ItemBuilder(Material.LEATHER_CHESTPLATE)
                .name(ChatColor.DARK_PURPLE.toString() + "Spiked Armor")
                .addEnchantment(Enchantment.THORNS, 0)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(spikedArmor).shape(" L ", " C ", " P ")

                    .setIngredient('C', Material.CACTUS)
                    .setIngredient('L', Material.WATER_LILY)
                    .setIngredient('P', Material.LEATHER_CHESTPLATE)
            )
            val sevenLeagueBoots = ItemBuilder(Material.DIAMOND_BOOTS)
                .name(ChatColor.DARK_PURPLE.toString() + "Seven League Boots")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.PROTECTION_FALL, 3)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(sevenLeagueBoots).shape("FEF", "FDF", "FWF")
                    .setIngredient('F', Material.FEATHER)
                    .setIngredient('E', Material.ENDER_PEARL)
                    .setIngredient('D', Material.DIAMOND_BOOTS)
                    .setIngredient('W', Material.WATER_BUCKET)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.IRON_INGOT, 10)).shape("III", "ICI", "III")
                    .setIngredient('I', Material.IRON_ORE)
                    .setIngredient('C', Material.COAL)
            )
            Bukkit.getServer().addRecipe(
                ShapelessRecipe(ItemStack(Material.OBSIDIAN)).addIngredient(Material.WATER_BUCKET)
                    .addIngredient(Material.LAVA_BUCKET)
            )
            val tarnhelm = ItemBuilder(Material.DIAMOND_HELMET)
                .name(ChatColor.DARK_PURPLE.toString() + "Tarnhelm")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
                .addEnchantment(Enchantment.WATER_WORKER, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(tarnhelm).shape("DID", "DRD", "   ")

                    .setIngredient('D', Material.DIAMOND)
                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('R', Material.REDSTONE_BLOCK)
            )


            val philosophersPickaxe = ItemBuilder(Material.DIAMOND_PICKAXE)
                .name(ChatColor.DARK_PURPLE.toString() + "Philosopher's Pickaxe")
                .addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2)
            philosophersPickaxe.setDurability((philosophersPickaxe.item.type.maxDurability - 2).toShort())
            Bukkit.getServer().addRecipe(
                ShapedRecipe(philosophersPickaxe.make()).shape("IGI", "LSL", " S ")

                    .setIngredient('I', Material.IRON_ORE)
                    .setIngredient('G', Material.GOLD_ORE)
                    .setIngredient('L', Material.LAPIS_BLOCK)
                    .setIngredient('S', Material.STICK)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.EXP_BOTTLE, 8)).shape(" R ", "RPR", " R ")

                    .setIngredient('R', Material.REDSTONE_BLOCK)
                    .setIngredient('P', Material.GLASS_BOTTLE)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.ANVIL)).shape("III", " B ", "III")

                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('B', Material.IRON_BLOCK)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.ENCHANTMENT_TABLE)).shape(" B ", "ODO", "OXO")

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
            Bukkit.getServer().addRecipe(
                ShapedRecipe(bookOfThoth).shape("E  ", " PP", " PB")

                    .setIngredient('E', Material.EYE_OF_ENDER)
                    .setIngredient('B', Material.FIREBALL)
                    .setIngredient('P', Material.PAPER)
            )
            Bukkit.getServer().addRecipe(
                ShapelessRecipe(ItemStack(Material.APPLE, 2)).addIngredient(Material.APPLE)
                    .addIngredient(Material.INK_SACK, 15)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.MELON)).shape("BSB", "SAS", "BSB")
                    .setIngredient('B', Material.INK_SACK, 15)
                    .setIngredient('S', Material.SEEDS)
                    .setIngredient('A', Material.APPLE)
            )
            val holyWater =
                PotionBuilder.createPotion(PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 3, false, true))
            Bukkit.getServer().addRecipe(
                ShapedRecipe(holyWater).shape("GRG", " D ", " P ")

                    .setIngredient('G', Material.GOLD_INGOT)
                    .setIngredient('R', Material.REDSTONE_BLOCK)
                    .setIngredient('D', Material.GOLD_RECORD)
                    .setIngredient('P', Material.GLASS_BOTTLE)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.GOLDEN_APPLE)).shape(" G ", "GAG", " G ")

                    .setIngredient('G', Material.GOLD_INGOT)
                    .setIngredient('A', Material.APPLE)
            )
            val goldenHead = ItemBuilder(Material.SKULL_ITEM)
                .toSkull()
                .setOwner("PhantomTupac")
                .name("&6Golden Head")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(goldenHead).shape("GGG", "GHG", "GGG")
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('G', Material.GOLD_INGOT)
            )
            val pandorasBox = ItemBuilder(Material.CHEST)
                .name(ChatColor.DARK_PURPLE.toString() + "Pandora's Box")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(pandorasBox).shape("GGG", "GHG", "GGG")
                    .setIngredient('G', Material.CHEST)
                    .setIngredient('H', Material.SKULL_ITEM, 3)
            )
            val panacea = PotionBuilder.createPotion(PotionEffect(PotionEffectType.HEAL, 1, 3, false, true))
            Bukkit.getServer().addRecipe(
                ShapedRecipe(panacea).shape("   ", "HGH", " P ")

                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('P', Material.GLASS_BOTTLE)
                    .setIngredient('G', Material.SPECKLED_MELON)
            )
            val cupidsBow = ItemBuilder(Material.BOW)
                .name(ChatColor.DARK_PURPLE.toString() + "Cupid's Bow")
                .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                .addEnchantment(Enchantment.ARROW_FIRE, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(cupidsBow).shape(" B ", "HBH", " L ")

                    .setIngredient('B', Material.BOW)
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('L', Material.LAVA_BUCKET)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.ARROW, 20)).shape("FFF", "SSS", "RRR")
                    .setIngredient('F', Material.FLINT)
                    .setIngredient('R', Material.FEATHER)
                    .setIngredient('S', Material.STICK)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.LEATHER)).shape("LLL", "SLS", "I/I")

                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('L', Material.LEATHER)
            )
            val potionOfVelocity =
                PotionBuilder.createPotion(PotionEffect(PotionEffectType.SPEED, 20 * 12, 3, false, true))
            Bukkit.getServer().addRecipe(
                ShapedRecipe(potionOfVelocity).shape(" C ", " S ", " P ")

                    .setIngredient('C', Material.COCOA)
                    .setIngredient('S', Material.SUGAR)
                    .setIngredient('P', Material.GLASS_BOTTLE)
            )
            val fenrir = SpawnEgg(EntityType.WOLF).toItemStack()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(fenrir).shape("LLL", "BXB", "LLL")
                    .setIngredient('L', Material.LEATHER)
                    .setIngredient('X', Material.EXP_BOTTLE)
                    .setIngredient('B', Material.BONE)
            )
            val forge = ItemBuilder(Material.FURNACE)
                .name(ChatColor.DARK_PURPLE.toString() + "Forge")
                .addLore("&7Instantly smelts items. Breaks after 10 uses.")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(forge).shape("CCC", "COC", "CCC")
                    .setIngredient('C', Material.COBBLESTONE)
                    .setIngredient('O', Material.COAL_BLOCK)
            )
            val quickPick = ItemBuilder(Material.IRON_PICKAXE)
                .name(ChatColor.DARK_PURPLE.toString() + "Quick Pick")
                .addEnchantment(Enchantment.DIG_SPEED, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(quickPick).shape("III", "CSC", " S ")
                    .setIngredient('I', Material.IRON_ORE)
                    .setIngredient('C', Material.COAL)
                    .setIngredient('S', Material.STICK)

            )
            val lumberjackAxe = ItemBuilder(Material.IRON_AXE)
                .name(ChatColor.DARK_PURPLE.toString() + "Lumberjack Axe")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(lumberjackAxe).shape("IIF", "IS ", " S ")
                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('F', Material.FLINT)
                    .setIngredient('S', Material.STICK)

            )
            val apprenticeHelmet = ItemBuilder(Material.IRON_HELMET)
                .name(ChatColor.DARK_PURPLE.toString() + "Apprentice Helmet")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1)
                .addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(apprenticeHelmet).shape("III", "IRI", "   ")
                    .setIngredient('I', Material.IRON_INGOT)
                    .setIngredient('R', Material.REDSTONE_TORCH_ON)

            )
            val apprenticeSword = ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.DARK_PURPLE.toString() + "Apprentice Sword")
                .addLore("&7Gains ${Chat.secondaryColor}Sharpness I&7 at Final Heal&7.")
                .addLore("&7Gains ${Chat.secondaryColor}Sharpness II&7 at PvP&7.")
                .addLore("&7Gains ${Chat.secondaryColor}Sharpness III&7 at Meetup&7.")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(apprenticeSword).shape(" R ", " I ", " R ")
                    .setIngredient('I', Material.IRON_SWORD)

                    .setIngredient('R', Material.REDSTONE_BLOCK)
            )
            val apprenticeBow = ItemBuilder(Material.BOW)
                .name(ChatColor.DARK_PURPLE.toString() + "Apprentice Bow")
                .addLore("&7Gains ${Chat.secondaryColor}Power I&7 at Final Heal&7.")
                .addLore("&7Gains ${Chat.secondaryColor}Power II&7 at PvP&7.")
                .addLore("&7Gains ${Chat.secondaryColor}Power III&7 at Meetup&7.")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(apprenticeBow).shape(" RS", "R S", " RS")
                    .setIngredient('R', Material.REDSTONE_TORCH_ON)

                    .setIngredient('S', Material.STRING)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.GOLD_INGOT, 10))
                    .shape("GGG", "GCG", "GGG")
                    .setIngredient('G', Material.GOLD_ORE)
                    .setIngredient('C', Material.COAL)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(ItemStack(Material.SUGAR_CANE, 4))
                    .shape(" G ", "SRS", "   ")
                    .setIngredient('G', Material.SAPLING)
                    .setIngredient('R', Material.SUGAR)
                    .setIngredient('S', Material.SEEDS)
            )
            val fusionArmor = ItemBuilder(Material.DIAMOND_HELMET)
                .name(ChatColor.DARK_PURPLE.toString() + "Fusion Armor")
                .addLore("&7Crafting this item will allow you to receive a random piece of armor with ${Chat.secondaryColor}Protection IV&7!")
                .make()
            Bukkit.getServer().addRecipe(
                ShapelessRecipe(fusionArmor)
                    .addIngredient(Material.DIAMOND_HELMET)
                    .addIngredient(Material.DIAMOND_CHESTPLATE)
                    .addIngredient(Material.DIAMOND_LEGGINGS)
                    .addIngredient(Material.DIAMOND_BOOTS)
            )
            val artemisBow = ItemBuilder(Material.BOW)
                .name(ChatColor.YELLOW.toString() + "Artemis' Bow")
                .addLore("&725% of Homing Arrows")
                .addEnchantment(Enchantment.ARROW_DAMAGE, 3)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(artemisBow).shape("FDF", "FBF", "FEF")
                    .setIngredient('F', Material.FEATHER)
                    .setIngredient('D', Material.DIAMOND)
                    .setIngredient('B', Material.BOW)
                    .setIngredient('E', Material.EYE_OF_ENDER)
            )
            val flaskOfIchor = PotionBuilder.createPotion(PotionEffect(PotionEffectType.HARM, 1, 2, false, false))
            Bukkit.getServer().addRecipe(
                ShapedRecipe(flaskOfIchor).shape(" H ", "BPB", " S ")
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('B', Material.BROWN_MUSHROOM)
                    .setIngredient('P', Material.GLASS_BOTTLE)
                    .setIngredient('S', Material.INK_SACK)
            )
            val exodus = ItemBuilder(Material.DIAMOND_HELMET)
                .name(ChatColor.YELLOW.toString() + "Exodus")
                .addEnchantment(Enchantment.DURABILITY, 3)
                .addLore("&7Upon hitting a player with sword or bow, gain Regeneration I for 2.5 seconds, totals to 1 heart per hit.")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(exodus).shape("DDD", "DHD", "ECE")
                    .setIngredient('D', Material.DIAMOND)
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('E', Material.EMERALD)
                    .setIngredient('C', Material.GOLDEN_CARROT)
            )
            val hideOfLeviathan = ItemBuilder(Material.DIAMOND_LEGGINGS)
                .name(ChatColor.YELLOW.toString() + "Hide of Leviathan")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addEnchantment(Enchantment.OXYGEN, 3)
                .addEnchantment(Enchantment.WATER_WORKER, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(hideOfLeviathan).shape("LWL", "DGD", "P P")
                    .setIngredient('L', Material.LAPIS_BLOCK)
                    .setIngredient('W', Material.WATER_BUCKET)
                    .setIngredient('D', Material.DIAMOND)
                    .setIngredient('G', Material.DIAMOND_LEGGINGS)
                    .setIngredient('P', Material.WATER_LILY)
            )
            val tabletsOfDestiny = ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.YELLOW.toString() + "Tablets of Destiny")
                .toEnchant()
                .addStoredEnchant(Enchantment.DAMAGE_ALL, 3)
                .addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .addStoredEnchant(Enchantment.ARROW_DAMAGE, 3)
                .addStoredEnchant(Enchantment.FIRE_ASPECT, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(tabletsOfDestiny).shape(" M ", "SBO", "XXX")
                    .setIngredient('M', Material.MAGMA_CREAM)
                    .setIngredient('S', Material.DIAMOND_SWORD)
                    .setIngredient('B', Material.BOOK_AND_QUILL)
                    .setIngredient('O', Material.BOW)
                    .setIngredient('X', Material.EXP_BOTTLE)
            )
            val axeOfPerun = ItemBuilder(Material.DIAMOND_AXE)
                .name(ChatColor.YELLOW.toString() + "Axe of Perun")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(axeOfPerun).shape("DTF", "DS ", " S ")
                    .setIngredient('D', Material.DIAMOND)
                    .setIngredient('T', Material.TNT)
                    .setIngredient('F', Material.FIREBALL)
                    .setIngredient('S', Material.STICK)
            )
            val anduril = ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.YELLOW.toString() + "Andūril")
                .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(anduril).shape("FIF", "FIF", "FBF")
                    .setIngredient('F', Material.FEATHER)
                    .setIngredient('I', Material.IRON_BLOCK)
                    .setIngredient('B', Material.BLAZE_ROD)
            )
            val deathsScythe = ItemBuilder(Material.IRON_HOE)
                .name(ChatColor.YELLOW.toString() + "Death's Scythe")
                .addLore("&7Damage dealt will be 20% of the target's current health, bypassses armor")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(deathsScythe).shape(" HH", " BW", "B  ")
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('B', Material.BONE)
                    .setIngredient('W', Material.WATCH)
            )
            val chestOfFate = ItemBuilder(Material.CHEST)
                .name(ChatColor.YELLOW.toString() + "Chest of Fate")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(chestOfFate).shape("WWW", "WHW", "WWW")
                    .setIngredient('W', Material.WOOD)
                    .setIngredient('H', Material.SKULL_ITEM, 3)
            )
            val cornucopia = ItemBuilder(Material.GOLDEN_CARROT)
                .name(ChatColor.YELLOW.toString() + "Cornucopia")
                .setAmount(3)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(cornucopia).shape("CCC", "CGC", "CCC")
                    .setIngredient('C', Material.CARROT)
                    .setIngredient('G', Material.GOLDEN_APPLE)
            )
            val essenceOfYggdrasil = ItemBuilder(Material.EXP_BOTTLE)
                .name(ChatColor.YELLOW.toString() + "Essence of Yggdrasil")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(essenceOfYggdrasil).shape("LEL", "GBG", "LRL")
                    .setIngredient('L', Material.LAPIS_BLOCK)
                    .setIngredient('E', Material.ENCHANTMENT_TABLE)
                    .setIngredient('G', Material.GLOWSTONE)
                    .setIngredient('R', Material.REDSTONE_BLOCK)
                    .setIngredient('B', Material.GLASS_BOTTLE)
            )
            val deusExMachina =
                PotionBuilder.createPotion(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 15, 3, false, true))
            Bukkit.getServer().addRecipe(
                ShapedRecipe(deusExMachina).shape(" E ", " H ", " P ")
                    .setIngredient('E', Material.EMERALD)
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('P', Material.GLASS_BOTTLE)
            )
            val diceOfGod = ItemBuilder(Material.ENDER_PORTAL_FRAME)
                .name(ChatColor.YELLOW.toString() + "Dice of God")
                .addLore("&7Grants a random Extra Ultimate.")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(diceOfGod).shape("CHC", "CJC", "CCC")
                    .setIngredient('C', Material.MOSSY_COBBLESTONE)
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('J', Material.JUKEBOX)
            )
            val kingsRod = ItemBuilder(Material.FISHING_ROD)
                .name(ChatColor.YELLOW.toString() + "King's Rod")
                .addEnchantment(Enchantment.LUCK, 10)
                .addEnchantment(Enchantment.LURE, 5)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(kingsRod).shape(" F ", "LCL", " W ")
                    .setIngredient('F', Material.FISHING_ROD)
                    .setIngredient('L', Material.WATER_LILY)
                    .setIngredient('C', Material.COMPASS)
                    .setIngredient('W', Material.WATER_BUCKET)
            )
            val daredevil = SpawnEgg(EntityType.HORSE).toItemStack(1)
            val daredevilMeta = daredevil.itemMeta
            daredevilMeta.displayName = ChatColor.YELLOW.toString() + "Daredevil"
            daredevil.itemMeta = daredevilMeta
            Bukkit.getServer().addRecipe(
                ShapedRecipe(daredevil).shape("HS ", "BBB", "B B")
                    .setIngredient('H', Material.SKULL_ITEM, 3)
                    .setIngredient('S', Material.SADDLE)
                    .setIngredient('B', Material.BONE)
            )
            val excalibur = ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.YELLOW.toString() + "Excalibur")
                .addLore("&72 hearts of true damage on hit, 5 seconds cooldown")
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(excalibur).shape("SBS", "STS", "SDS")
                    .setIngredient('S', Material.SOUL_SAND)
                    .setIngredient('T', Material.TNT)
                    .setIngredient('D', Material.DIAMOND_SWORD)
            )
            val shoesOfVidar = ItemBuilder(Material.DIAMOND_BOOTS)
                .name(ChatColor.YELLOW.toString() + "Shoes of Vidar")
                .addEnchantment(Enchantment.DURABILITY, 3)
                .addEnchantment(Enchantment.DEPTH_STRIDER, 2)
                .addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2)
                .addEnchantment(Enchantment.THORNS, 1)
                .make()
            Bukkit.getServer().addRecipe(
                ShapedRecipe(shoesOfVidar).shape(" P ", "BDB", " R ")
                    .setIngredient('P', Material.RAW_FISH, 3)
                    .setIngredient('B', Material.POTION)
                    .setIngredient('D', Material.DIAMOND_BOOTS)
                    .setIngredient('R', Material.FISHING_ROD)
            )
            val potionOfVitality = PotionBuilder.createPotion(
                PotionEffect(PotionEffectType.SPEED, 20 * 12, 1, false, true),
                PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1, false, true),
                PotionEffect(PotionEffectType.WEAKNESS, 20 * 12, 1, false, true),
                PotionEffect(PotionEffectType.WITHER, 20 * 6, 1, false, true)
            )
            Bukkit.getServer().addRecipe(
                ShapedRecipe(potionOfVitality).shape(" S ", " N ", " B")
                    .setIngredient('S', Material.SKULL_ITEM, 0)
                    .setIngredient('N', Material.NETHER_WARTS)
                    .setIngredient('B', Material.GLASS_BOTTLE)
            )
            val ambrosia = ItemBuilder(Material.GLOWSTONE_DUST)
                .name(ChatColor.YELLOW.toString() + "Ambrosia")
                .addLore("&7When used in a brewing stand buffs all potions to level III and reduces duration to 1 minute if above ")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(ambrosia).shape("BSB", "GTG", "GGG")
                .setIngredient('B', Material.BLAZE_POWDER)
                .setIngredient('S', Material.SKULL_ITEM, 1)
                .setIngredient('G', Material.GLOWSTONE)
                .setIngredient('T', Material.GHAST_TEAR)
            )
            val bloodlust = ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.YELLOW.toString() + "Bloodlust")
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore("&7Gains Sharpness II After 1 kill")
                .addLore("&7Gains Sharpness III after 3 kill")
                .addLore("&7Gains Sharpness IV after 6 kill")
                .addLore("&7Gains Sharpness V after 10 kill")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(bloodlust).shape("RDR", "RSR", "RXR")
                .setIngredient('R', Material.REDSTONE_BLOCK)
                .setIngredient('D', Material.DIAMOND)
                .setIngredient('S', Material.DIAMOND_SWORD)
                .setIngredient('X', Material.EXP_BOTTLE)
            )
            val modularBow = ItemBuilder(Material.BOW)
                .name(ChatColor.YELLOW.toString() + "Modular Bow")
                .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
                .addLore("&dLeft click to switch modes")
                .addLore("&7Mode 1: Punch I, Mode 2: Poison I, Mode 3: Lightning (1.5 hearts)")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(modularBow).shape(" W ", "PBP", "ESE")
                .setIngredient('W', Material.WATCH)
                .setIngredient('P', Material.BLAZE_POWDER)
                .setIngredient('E', Material.SPIDER_EYE)
                .setIngredient('B', Material.BOW)
                .setIngredient('S', Material.SLIME_BALL)
            )
            val hermesBoots = ItemBuilder(Material.DIAMOND_BOOTS)
                .name(ChatColor.YELLOW.toString() + "Hermes Boots")
                .addEnchantment(Enchantment.PROTECTION_FALL, 1)
                .addEnchantment(Enchantment.DURABILITY, 2)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore("&7While wearing: 10% movement speed increase.")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(hermesBoots).shape("DHD", "PBP", "F F")
                .setIngredient('D', Material.DIAMOND)
                .setIngredient('H', Material.SKULL_ITEM, 3)
                .setIngredient('P', Material.BLAZE_POWDER)
                .setIngredient('F', Material.FEATHER)
                .setIngredient('B', Material.DIAMOND_BOOTS)
            )
            val barbarianChestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .name(ChatColor.YELLOW.toString() + "Barbarian Chestplate")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addLore("&7While wearing: Gives Strength I, Resistance I")
                .make()
            Bukkit.getServer().addRecipe(ShapedRecipe(barbarianChestplate).shape("RCR", "ISI", "   ")
                .setIngredient('R', Material.BLAZE_ROD)
                .setIngredient('C', Material.DIAMOND_CHESTPLATE)
                .setIngredient('I', Material.IRON_BLOCK)
                .setIngredient('S', Material.POTION, 5)
            )
        }
    }

    @EventHandler
    fun onLumberjackAxe(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (
            e.player.inventory.itemInHand != null &&
            e.player.inventory.itemInHand.hasItemMeta() &&
            e.player.inventory.itemInHand.itemMeta.displayName == Chat.colored("&5Lumberjack Axe")
        ) {
            timberTree(e.block.location, e.block.type, e.player)
        }
    }

    private fun timberTree(loc: Location, material: Material, player: Player) {
        for (x in loc.blockX - 1..loc.blockX + 1) {
            for (y in loc.blockY - 1..loc.blockY + 1) {
                for (z in loc.blockZ - 1..loc.blockZ + 1) {
                    val newLoc = Location(loc.world, x.toDouble(), y.toDouble(), z.toDouble())
                    if (loc.world.getBlockAt(x, y, z).type == material) {
                        loc.world.getBlockAt(x, y, z).breakNaturally()
                        loc.world.playSound(newLoc, Sound.DIG_WOOD, 1f, 1f)
                        BlockUtil().degradeDurability(player)
                        timberTree(newLoc, material, player)
                    }
                }
            }
        }
    }

    private val perunCooldownsMap = hashMapOf<Player, Long>()
    @EventHandler
    fun onPvP(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.damager is Player && e.entity is Player) {
            if ((e.damager as Player).inventory.itemInHand != null && (e.damager as Player).inventory.itemInHand.hasItemMeta() && (e.damager as Player).inventory.itemInHand.itemMeta.displayName == Chat.colored("&eDeath's Scythe")) {
                (e.entity as Player).damage((e.entity as Player).health * .2)
            }
            if ((e.damager as Player).inventory.helmet != null && (e.damager as Player).inventory.helmet.hasItemMeta() && (e.damager as Player).inventory.helmet.itemMeta.displayName == Chat.colored("&eExodus")) {
                if (!(e.damager as Player).hasPotionEffect(PotionEffectType.REGENERATION)) {
                    (e.damager as Player).addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 50, 0))
                }
            }
            if ((e.damager as Player).inventory.itemInHand != null && (e.damager as Player).inventory.itemInHand.hasItemMeta() && (e.damager as Player).inventory.itemInHand.itemMeta.displayName == Chat.colored("&eAxe of Perun")) {
                if (perunCooldownsMap[e.damager as Player] == null || perunCooldownsMap[e.damager as Player]!! < System.currentTimeMillis()) {
                    (e.damager as Player).world.strikeLightning((e.entity as Player).location)
                    perunCooldownsMap[e.damager as Player] = System.currentTimeMillis() + 8000
                } else {
                    return
                }
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (e.entity.killer == null) return
        if (e.entity.killer !is Player) return
        e.drops.add(ItemStack(Material.GOLD_NUGGET, 10))
        e.droppedExp = (e.droppedExp + floor((e.droppedExp * 0.50))).toInt()
        if (e.entity.killer.itemInHand != null && e.entity.killer.itemInHand.hasItemMeta() && e.entity.killer.itemInHand.itemMeta.displayName == Chat.colored("&eBloodlust")) {
            if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 1) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 2)
            } else if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 3) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 3)
            } else if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 6) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 4)
            } else if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 10) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 5)
            }
        }
        e.entity.killer.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 0, false, true))
        e.entity.killer.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 0, false, true))
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (!enabled) return
        if (e.item == null) return
        if (e.item.type == Material.SKULL_ITEM && e.item.itemMeta.displayName == Chat.colored("&6Golden Head")) {
            e.isCancelled = true
            val item = e.item.clone()
            item.amount = 1
            e.player.inventory.removeItem(item)
            if (TeamsFeature.manager.getTeam(e.player) == null) {
                Chat.sendMessage(
                    e.player,
                    "$prefix You ate a &6Golden Head&7 and gained 15 seconds of Regeneration II & 2 minutes of Absorption."
                )
                e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1, false, true))
                e.player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1, false, true))
            } else {
                for (teammate in TeamsFeature.manager.getTeam(e.player)!!.players) {
                    if (teammate.isOnline && teammate != null) {
                        Chat.sendMessage(
                            teammate as Player,
                            "$prefix &6${e.player.name}&7 ate a &6Golden Head&7 and you gained gained 5 seconds of Regeneration II & 1 minute of Absorption."
                        )
                        teammate.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1, false, true))
                        teammate.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 1, false, true))
                    }
                }
            }
        } else if (e.item.type == Material.SKULL_ITEM) {
            e.isCancelled = true
            e.item.amount = e.item.amount - 1
            if (e.item.amount == 0) {
                e.player.inventory.remove(e.item)
            }
            Chat.sendMessage(
                e.player,
                "$prefix You ate a player head and gained 20 seconds of Regeneration I! You gained 9 seconds of Speed II"
            )
            e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 0, false, true))
            e.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 9, 0, false, true))
        }
        if (e.item.itemMeta.displayName == Chat.colored("&cCrafting Recipes")) {
            e.isCancelled = true
            Chat.sendMessage(
                e.player,
                "$prefix View the crafting recipes for &cChampions&7 here: &chttps://hypixel.fandom.com/wiki/UHC_Champions#Recipes"
            )
        }
        if (e.item.type == Material.BOW && e.item.itemMeta.displayName == Chat.colored("&eModular Bow")) {
            val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Punch)"))
                .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1).make()
            e.player.itemInHand = bow
            Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Punch.")
        }
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            if (e.item.type == Material.BOW && e.item.itemMeta.displayName == Chat.colored("&eModular Bow (Punch)")) {
                val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Poison I)")).make()
                e.player.itemInHand = bow
                Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Poison I.")
            }
            if (e.item.type == Material.BOW && e.item.itemMeta.displayName == Chat.colored("&eModular Bow (Poison I)")) {
                val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Lightning)")).make()
                e.player.itemInHand = bow
                Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Lightning.")
            }
            if (e.item.type == Material.BOW && e.item.itemMeta.displayName == Chat.colored("&eModular Bow (Lightning)")) {
                val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Punch)"))
                    .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1).make()
                e.player.itemInHand = bow
                Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Punch.")
            }
        }
    }

    @EventHandler
    fun onFenrirSpawn(event: PlayerInteractEvent) {
        if (!enabled) return
        val player: Player = event.player

        val action: Action = event.action
        var item = event.item
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (item == null) {
            return
        }
        if (item.type != Material.MONSTER_EGG) {
            return
        }
        if (item.durability.toInt() != 95) {
            return
        }
        item = item.clone()
        item.amount = 1
        player.inventory.removeItem(item)
        var block: Block? = event.clickedBlock ?: return
        block = block!!.getRelative(event.blockFace)
        val wolf: Wolf = block.world.spawn(block.location.add(0.5, 0.1, 0.5), Wolf::class.java)
        wolf.isTamed = true
        wolf.owner = player
        wolf.maxHealth = 40.0
        wolf.health = 40.0
        wolf.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 0))
        wolf.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0))
        event.isCancelled = true
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
            e.damage = e.damage - (e.damage * 0.8)
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (e.player !is Player) return
        e.player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 100, 0, false, false))
        if (e.block.type == Material.GOLD_ORE || e.block.type == Material.IRON_ORE) {
            val chance = (0..100).random()
            if (chance <= 14) {
                e.player.location.world.dropItem(e.player.location, ItemBuilder(e.block.type).make())
            }
        }
        if (e.block.type == Material.SAND || e.block.type == Material.GRAVEL || e.block.type == Material.OBSIDIAN) {
            val chance = (0..100).random()
            if (chance <= 50) {
                e.player.location.world.dropItem(e.player.location, ItemBuilder(e.block.type).make())
            }
        }
    }

    @EventHandler
    fun onModularBowShot(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (e.damager.type == EntityType.ARROW && ((e.damager as Arrow).shooter) is Player && e.entity.type == EntityType.PLAYER) {
            if (((e.damager as Arrow).shooter as Player).itemInHand.type == Material.BOW && ((e.damager as Arrow).shooter as Player).itemInHand.itemMeta.displayName == Chat.colored("&eModular Bow (Poison I)")) {
                (e.entity as Player).addPotionEffect(PotionEffect(PotionEffectType.POISON, 20 * 33, 0, false, true))
            }
            if (((e.damager as Arrow).shooter as Player).itemInHand.type == Material.BOW && ((e.damager as Arrow).shooter as Player).itemInHand.itemMeta.displayName == Chat.colored("&eModular Bow (Lightning)")) {
                e.damager.world.strikeLightning(e.entity.location)
            }
        }
    }

    override fun givePlayer(player: Player) {
        player.maxHealth = 40.0
        player.health = 40.0
        player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 6000, 0, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 6000, 0, false, false))
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
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.BOOK, 4),
                    ItemStack(Material.EXP_BOTTLE, 15),
                    ItemStack(Material.INK_SACK, 18, 4),
                    pickaxe
                )
            )
        } else if (kits[player.uniqueId] == "archer") {
            val shovel = ItemBuilder(Material.STONE_SPADE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.STRING, 6),
                    ItemStack(Material.FEATHER, 9),
                    shovel
                )
            )
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
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    shovel,
                    pickaxe,
                    axe,
                    sword
                )
            )
        } else if (kits[player.uniqueId] == "lunchBox") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.COOKED_BEEF, 9),
                    ItemStack(Material.CARROT, 12),
                    ItemStack(Material.MELON, 2),
                    ItemStack(Material.APPLE, 3),
                    ItemStack(Material.GOLD_INGOT, 3),
                )
            )
        } else if (kits[player.uniqueId] == "looter") {
            val sword = ItemBuilder(Material.STONE_SWORD)
                .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 1)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.BONE, 3),
                    ItemStack(Material.SLIME_BALL, 3),
                    ItemStack(Material.SULPHUR, 2),
                    ItemStack(Material.SPIDER_EYE, 2),
                    sword
                )
            )
        } else if (kits[player.uniqueId] == "ecologist") {
            val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.VINE, 21),
                    ItemStack(Material.WATER_LILY, 64),
                    ItemStack(Material.SUGAR_CANE, 12),
                    pickaxe
                )
            )
        } else if (kits[player.uniqueId] == "farmer") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.IRON_HOE),
                    ItemStack(Material.MELON, 3),
                    ItemStack(Material.CARROT, 3),
                    ItemStack(Material.INK_SACK, 15),
                )
            )
        } else if (kits[player.uniqueId] == "horseman") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.LEATHER, 12),
                    ItemStack(Material.HAY_BLOCK, 1),
                    ItemStack(Material.STRING, 4),
                    ItemStack(Material.IRON_BARDING),
                    SpawnEgg(EntityType.HORSE).toItemStack(1)
                )
            )
        } else if (kits[player.uniqueId] == "trapper") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.PISTON_BASE, 8),
                    ItemStack(Material.PISTON_STICKY_BASE, 8),
                    ItemStack(Material.REDSTONE, 25),
                    ItemStack(Material.LOG, 16)
                )
            )
        }
        val book = ItemBuilder(Material.ENCHANTED_BOOK)
            .name("&cCrafting Recipes")
            .addLore("&7Click to open & view a list of crafting recipes.")
            .make()
        PlayerUtils.bulkItems(
            player, arrayListOf(
                book
            )
        )
    }
    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) continue
            player.maxHealth = 40.0
            player.health = 40.0
            player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 6000, 0, false, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 6000, 0, false, false))
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
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.BOOK, 4),
                        ItemStack(Material.EXP_BOTTLE, 15),
                        ItemStack(Material.INK_SACK, 18, 4),
                        pickaxe
                    )
                )
            } else if (kits[player.uniqueId] == "archer") {
                val shovel = ItemBuilder(Material.STONE_SPADE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.STRING, 6),
                        ItemStack(Material.FEATHER, 9),
                        shovel
                    )
                )
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
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        shovel,
                        pickaxe,
                        axe,
                        sword
                    )
                )
            } else if (kits[player.uniqueId] == "lunchBox") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.COOKED_BEEF, 9),
                        ItemStack(Material.CARROT, 12),
                        ItemStack(Material.MELON, 2),
                        ItemStack(Material.APPLE, 3),
                        ItemStack(Material.GOLD_INGOT, 3),
                    )
                )
            } else if (kits[player.uniqueId] == "looter") {
                val sword = ItemBuilder(Material.STONE_SWORD)
                    .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 1)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.BONE, 3),
                        ItemStack(Material.SLIME_BALL, 3),
                        ItemStack(Material.SULPHUR, 2),
                        ItemStack(Material.SPIDER_EYE, 2),
                        sword
                    )
                )
            } else if (kits[player.uniqueId] == "ecologist") {
                val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.VINE, 21),
                        ItemStack(Material.WATER_LILY, 64),
                        ItemStack(Material.SUGAR_CANE, 12),
                        pickaxe
                    )
                )
            } else if (kits[player.uniqueId] == "farmer") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.IRON_HOE),
                        ItemStack(Material.MELON, 3),
                        ItemStack(Material.CARROT, 3),
                        ItemStack(Material.INK_SACK, 15),
                    )
                )
            } else if (kits[player.uniqueId] == "horseman") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.LEATHER, 12),
                        ItemStack(Material.HAY_BLOCK, 1),
                        ItemStack(Material.STRING, 4),
                        ItemStack(Material.IRON_BARDING),
                        SpawnEgg(EntityType.HORSE).toItemStack(1)
                    )
                )
            } else if (kits[player.uniqueId] == "trapper") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.PISTON_BASE, 8),
                        ItemStack(Material.PISTON_STICKY_BASE, 8),
                        ItemStack(Material.REDSTONE, 25),
                        ItemStack(Material.LOG, 16)
                    )
                )
            }
            val book = ItemBuilder(Material.ENCHANTED_BOOK)
                .name("&cCrafting Recipes")
                .addLore("&7Click to open & view a list of crafting recipes.")
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    book
                )
            )
        }
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.inventory.itemInHand != null && player.inventory.itemInHand.hasItemMeta() && player.inventory.itemInHand.itemMeta.displayName == Chat.colored("&eAndūril")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20, 0, false, true))
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0, false, true))
                    }
                    if (player.inventory.chestplate != null && player.inventory.chestplate.hasItemMeta() && player.inventory.chestplate.itemMeta.displayName == Chat.colored("&eBarbarian Chestplate")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 0, false, true))
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0, false, true))
                    }
                    if (player.inventory.boots != null && player.inventory.boots.hasItemMeta() && player.inventory.boots.itemMeta.displayName == Chat.colored("&eHermes' Boots")) {
                        player.walkSpeed = 0.2F + 0.02F
                    } else {
                        player.walkSpeed = 0.2F
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
    }

    val forgeMap = hashMapOf<UUID, Int>()

    @EventHandler
    fun onForgePlace(e: BlockPlaceEvent) {
        if (!enabled) return
        if (e.block.type == Material.FURNACE && (e.block.state as Furnace).inventory.title == Chat.colored("&5Forge")) {
            val lava = ItemBuilder(Material.LAVA_BUCKET)
                .name("&5Forgium")
                .make()
            (e.block.state as Furnace).inventory.fuel = lava
        }
    }

    @EventHandler
    fun onShootEvent(e: EntityShootBowEvent) {
        if (!enabled) return
        if (e.entity is Player && e.bow != null && e.bow.hasItemMeta() && e.bow.itemMeta.displayName == Chat.colored("&eArtemis' Bow") && Random().nextInt(100) <= 25) {
            val arrow = e.projectile as Arrow
            (e.entity as Player).playSound(e.entity.location, Sound.LEVEL_UP, 1f, 1f)
            object : BukkitRunnable() {
                override fun run() {
                    val target = e.entity.getNearbyEntities(200.0, 200.0, 200.0).firstOrNull { it is Player && it != ((e.projectile as Arrow).shooter as Player) } as? Player
                    if (arrow.isOnGround || arrow.isDead || target == null || target.isDead) {
                        cancel()
                        return
                    }
                    arrow.velocity = target.location.toVector().subtract(arrow.location.toVector()).normalize()
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 5L, 1L)
        }
    }

    @EventHandler
    fun onForgeBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (e.block.type == Material.FURNACE || e.block.type == Material.BURNING_FURNACE) {
            if ((e.block.state as Furnace).inventory.title == Chat.colored("&5Forge")) {
                e.isCancelled = true
                val forge = ItemBuilder(Material.FURNACE)
                    .name(ChatColor.DARK_PURPLE.toString() + "Forge")
                    .addLore("&7Instantly smelts items. Breaks after 10 uses.")
                    .make()
                for (item in (e.block.state as Furnace).inventory.contents) {
                    if (item == null) continue
                    if (item.hasItemMeta() && item.itemMeta.displayName == Chat.colored("&5Forgium")) continue
                    e.block.world.dropItemNaturally(e.block.location, item)
                }
                e.block.type = Material.AIR
                e.block.world.dropItemNaturally(e.block.location, forge)
            }
        }
    }

    override fun onFinalHeal() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) continue
            for (item in player.inventory.contents) {
                if (item == null) continue
                if (!item.hasItemMeta()) continue
                if (item.itemMeta.displayName == Chat.colored("&5Apprentice Bow")) {
                    val bow = ItemBuilder(Material.BOW)
                        .name("&5Apprentice Bow")
                        .addLore("&7Gains ${Chat.secondaryColor}Power I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Power II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Power III&7 at Meetup&7.")
                        .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                        .make()
                    item.itemMeta = bow.itemMeta
                }
                if (item.itemMeta.displayName == Chat.colored("&5Apprentice Sword")) {
                    val sword = ItemBuilder(Material.IRON_SWORD)
                        .name("&5Apprentice Sword")
                        .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness III&7 at Meetup&7.")
                        .make()
                    item.itemMeta = sword.itemMeta
                }
            }
        }
    }

    override fun onMeetup() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) continue
            for (item in player.inventory.contents) {
                if (item == null) continue
                if (!item.hasItemMeta()) continue
                if (item.itemMeta.displayName == Chat.colored("&5Apprentice Bow")) {
                    val bow = ItemBuilder(Material.BOW)
                        .name("&5Apprentice Bow")
                        .addLore("&7Gains ${Chat.secondaryColor}Power I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Power II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Power III&7 at Meetup&7.")
                        .addEnchantment(Enchantment.ARROW_DAMAGE, 3)
                        .make()
                    item.itemMeta = bow.itemMeta
                }
                if (item.itemMeta.displayName == Chat.colored("&5Apprentice Sword")) {
                    val sword = ItemBuilder(Material.IRON_SWORD)
                        .name("&5Apprentice Sword")
                        .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness III&7 at Meetup&7.")
                        .make()
                    item.itemMeta = sword.itemMeta
                }
            }
        }
    }

    override fun onPvP() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) continue
            for (item in player.inventory.contents) {
                if (item == null) continue
                if (!item.hasItemMeta()) continue
                if (item.itemMeta.displayName == Chat.colored("&5Apprentice Bow")) {
                    val bow = ItemBuilder(Material.BOW)
                        .name("&5Apprentice Bow")
                        .addLore("&7Gains ${Chat.secondaryColor}Power I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Power II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Power III&7 at Meetup&7.")
                        .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                        .make()
                    item.itemMeta = bow.itemMeta
                }
                if (item.itemMeta.displayName == Chat.colored("&5Apprentice Sword")) {
                    val sword = ItemBuilder(Material.IRON_SWORD)
                        .name("&5Apprentice Sword")
                        .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness III&7 at Meetup&7.")
                        .make()
                    item.itemMeta = sword.itemMeta
                }
            }
        }
    }

    @EventHandler
    fun onForgeSmelt(e: InventoryClickEvent) {
        if (!enabled) return
        if (e.inventory.type != InventoryType.FURNACE) return
        if (e.inventory.title != Chat.colored("&5Forge")) return
        if (e.clickedInventory == null) return
        if (e.currentItem.type == Material.LAVA_BUCKET && e.currentItem.hasItemMeta() && e.currentItem.itemMeta.displayName == Chat.colored("&5Forgium")) {
            e.isCancelled = true
            return
        }
        Schedulers.sync().runLater(runnable@ {
            val furnace = e.inventory.holder as Furnace
            var result: ItemStack? = null
            val iter: Iterator<Recipe> = Bukkit.recipeIterator()
            val item = furnace.inventory.smelting
            while (iter.hasNext()) {
                val recipe = iter.next()
                if (recipe !is FurnaceRecipe) continue
                if (recipe.input.type != item.type) continue
                result = recipe.result
                break
            }
            if (result == null) return@runnable
            val amount = furnace.inventory.smelting.amount
            for (i in 0 until amount) {
                furnace.block.world.dropItemNaturally(furnace.block.location, result)
                val smelting = furnace.inventory.smelting
                smelting.amount--
                furnace.inventory.smelting = smelting
                if (forgeMap[e.whoClicked.uniqueId] == null) forgeMap[e.whoClicked.uniqueId] = 0
                forgeMap[e.whoClicked.uniqueId] = forgeMap[e.whoClicked.uniqueId]!! + 1
                if (forgeMap[e.whoClicked.uniqueId]!! >= 10) {
                    furnace.inventory.fuel = ItemStack(Material.AIR)
                    furnace.block.type = Material.AIR
                    forgeMap.remove(e.whoClicked.uniqueId)
                    Chat.sendMessage(e.whoClicked, "&aYour forge has broken from reaching its limit.")
                    return@runnable
                }
            }
        }, 1L)
    }

    private val rand = Random()

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun on(event: CraftItemEvent) {
        if (!enabled) return
        val player = event.whoClicked as Player
        val inv = event.inventory
        val item = inv.result
        if (!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) {
            return
        }
        val name = item.itemMeta.displayName
        if (name == Chat.colored("&5Pandora's Box")) {
            val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
            if (block == null || block.type !== Material.WORKBENCH) {
                player.sendMessage("$prefix You are not looking at a crafting table.")
                event.isCancelled = true
                return
            }
            event.isCancelled = true
            event.view.topInventory.clear()
            block.type = Material.CHEST
            val chest: Chest = block.state as Chest
            chest.inventory.setItem(13, randomReward())
        }
        if (name == Chat.colored("&eEssence of Yggdrasil")) {
            inv.result = null
            if (TeamsFeature.manager.getTeam(player) == null) {
                player.level += 30
                Chat.sendMessage(player, "&eYou've been blessed by the Essence of Yggdrasil.")
            } else {
                for (member in TeamsFeature.manager.getTeam(player)!!.players) {
                    if (member.uniqueId == player.uniqueId) continue
                    if (member.isOnline) member.player.level += 8
                    Chat.sendMessage(member.player, "&eYou've been blessed by the Essence of Yggdrasil.")
                }
                player.level += 15
                Chat.sendMessage(player, "&eYou've been blessed by the Essence of Yggdrasil.")
            }
        }
        if (name == Chat.colored("&eDeus Ex Machina")) {
            player.health = player.health / 2
            Chat.sendMessage(player, "&eYour health has been siphoned to create a Deus Ex Machina.")
        }
        if (name == Chat.colored("&eDaredevil")) {
            val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
            if (block == null || block.type !== Material.WORKBENCH) {
                player.sendMessage("$prefix You are not looking at a crafting table.")
                event.isCancelled = true
                return
            }
            event.isCancelled = true
            event.view.topInventory.clear()
            block.type = Material.AIR
            val h = player.world.spawn(block.location, Horse::class.java)
            h.customName = Chat.colored("&eDaredevil")
            h.variant = Horse.Variant.SKELETON_HORSE
            h.maxHealth = 50.0
            h.health = 50.0
            h.inventory.saddle = ItemBuilder(Material.SADDLE).make()
            h.isTamed = true
            h.owner = player
            h.jumpStrength = 1.0
            val speed: AttributeInstance = ((h as CraftEntity).handle as EntityLiving)
                .getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
            speed.value = 0.5

        }
        if (name == Chat.colored("&5Fusion Armor")) {
            val helmet = ItemBuilder(Material.DIAMOND_HELMET)
                .name("&5Fusion Helmet")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .make()
            val chestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .name("&5Fusion Chestplate")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .make()
            val leggings = ItemBuilder(Material.DIAMOND_LEGGINGS)
                .name("&5Fusion Leggings")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .make()
            val boots = ItemBuilder(Material.DIAMOND_BOOTS)
                .name("&5Fusion Boots")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .make()
            inv.result = arrayListOf(helmet, chestplate, leggings, boots).random()
        }
        if (name == Chat.colored("&eDice of God")) {
            val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
            if (block == null || block.type !== Material.WORKBENCH) {
                player.sendMessage("$prefix You are not looking at a crafting table.")
                event.isCancelled = true
                return
            }
            val extraUltimates: MutableList<ItemStack> = Lists.newArrayList()
            val it = Bukkit.recipeIterator()
            while (it.hasNext()) {
                val next = it.next()
                val result = next.result
                if (!result.hasItemMeta() || !result.itemMeta.hasDisplayName()) {
                    continue
                }
                if (result.itemMeta.displayName.startsWith("§e")) {
                    extraUltimates.add(result)
                }
            }
            event.currentItem = extraUltimates[rand.nextInt(extraUltimates.size)]
        }
        if (name == Chat.colored("&5Apprentice Sword")) {
            when (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent) {
                Events.FINAL_HEAL -> {
                    val sword = ItemBuilder(Material.IRON_SWORD)
                        .name("&5Apprentice Sword")
                        .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness III&7 at Meetup&7.")
                        .make()
                    inv.result = sword
                }
                Events.PVP -> {
                    val sword = ItemBuilder(Material.IRON_SWORD)
                        .name("&5Apprentice Sword")
                        .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness III&7 at Meetup&7.")
                        .make()
                    inv.result = sword
                }
                Events.MEETUP, Events.BORDER_SHRINK_ONE, Events.BORDER_SHRINK_TWO, Events.BORDER_SHRINK_THREE, Events.BORDER_SHRINK_FOUR, Events.BORDER_SHRINK_FIVE, Events.BORDER_SHRINK_SIX -> {
                    val sword = ItemBuilder(Material.IRON_SWORD)
                        .name("&5Apprentice Sword")
                        .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness I&7 at Final Heal&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness II&7 at PvP&7.")
                        .addLore("&7Gains ${Chat.secondaryColor}Sharpness III&7 at Meetup&7.")
                        .make()
                    inv.result = sword
                }
            }
        }
        if (name == Chat.colored("&5Apprentice Bow")) {
            val bow = ItemBuilder(Material.BOW)
                .name("&5Apprentice Bow")
                .addLore("&7Gains ${Chat.secondaryColor}Power I&7 at Final Heal&7.")
                .addLore("&7Gains ${Chat.secondaryColor}Power II&7 at PvP&7.")
                .addLore("&7Gains ${Chat.secondaryColor}Power III&7 at Meetup&7.")
            when (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent) {
                Events.FINAL_HEAL -> {
                    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                    inv.result = bow.make()
                }
                Events.PVP -> {
                    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                    inv.result = bow.make()
                }
                Events.MEETUP, Events.BORDER_SHRINK_ONE, Events.BORDER_SHRINK_TWO, Events.BORDER_SHRINK_THREE, Events.BORDER_SHRINK_FOUR, Events.BORDER_SHRINK_FIVE, Events.BORDER_SHRINK_SIX -> {
                    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 3)
                    inv.result = bow.make()
                }
            }
        }
    }

    /**
     * Get the random pandora's box loot that it should give.
     *
     * @return The random loot.
     */
    private fun randomReward(): ItemStack? {
        var randomPerc = rand.nextDouble() * 100
        if (randomPerc < 2.0) {
            return BookBuilder.createEnchantedBook(Enchantment.ARROW_FIRE, 1)
        }
        randomPerc -= 2.0
        if (randomPerc < 2.0) {
            return BookBuilder.createEnchantedBook(Enchantment.FIRE_ASPECT, 2)
        }
        randomPerc -= 2.0
        if (randomPerc < 3.0) {
            return BookBuilder.createEnchantedBook(Enchantment.FIRE_ASPECT, 1)
        }
        randomPerc -= 3.0
        if (randomPerc < 5.0) {
            return BookBuilder.createEnchantedBook(Enchantment.DAMAGE_ALL, 4)
        }
        randomPerc -= 5.0
        if (randomPerc < 5.0) {
            return BookBuilder.createEnchantedBook(Enchantment.ARROW_DAMAGE, 4)
        }
        randomPerc -= 5.0
        if (randomPerc < 5.0) {
            return ItemStack(Material.DIAMOND, 7)
        }
        randomPerc -= 5.0
        if (randomPerc < 5.0) {
            return PotionBuilder.createPotion(PotionEffect(PotionEffectType.HEAL, 0, 3))
        }
        randomPerc -= 5.0
        if (randomPerc < 7.0) {
            return PotionBuilder.createPotion(PotionEffect(PotionEffectType.HEAL, 0, 2))
        }
        randomPerc -= 7.0
        if (randomPerc < 8.0) {
            return PotionBuilder.createPotion(
                PotionEffect(PotionEffectType.SPEED, 1800, 1, false, true),
                PotionEffect(PotionEffectType.ABSORPTION, 2400, 2, false, true)
            )
        }
        randomPerc -= 8.0
        if (randomPerc < 9.0) {
            return PotionBuilder.createPotion(
                PotionEffect(PotionEffectType.SPEED, 1800, 1, false, true),
                PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1800, 1, false, true)
            )
        }
        randomPerc -= 9.0
        if (randomPerc < 12.0) {
            return ItemStack(Material.GOLDEN_APPLE, 3)
        }
        randomPerc -= 12.0
        return if (randomPerc < 15.0) {
            ItemStack(Material.GOLD_INGOT, 24)
        } else ItemStack(Material.EXP_BOTTLE, 48)
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
        gui.item(0, leatherKit).onClick runnable@{
            if (this.kits[sender.uniqueId] == "leather") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "leather"
            Chat.sendMessage(sender, "&aYou have selected the &6Leather Armor&a kit.")
        }
        gui.item(1, enchanterKit.make()).onClick runnable@{
            if (this.kits[sender.uniqueId] == "enchanter") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "enchanter"
            Chat.sendMessage(sender, "&aYou have selected the &6Enchanting Set&a kit.")
        }
        gui.item(2, archerKit).onClick runnable@{
            if (this.kits[sender.uniqueId] == "archer") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "archer"
            Chat.sendMessage(sender, "&aYou have selected the &6Archer Set&a kit.")
        }
        gui.item(3, stoneGear).onClick runnable@{
            if (this.kits[sender.uniqueId] == "stoneGear") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "stoneGear"
            Chat.sendMessage(sender, "&aYou have selected the &6Stone Gear&a kit.")
        }
        gui.item(4, lunchBox).onClick runnable@{
            if (this.kits[sender.uniqueId] == "lunchBox") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "lunchBox"
            Chat.sendMessage(sender, "&aYou have selected the &6Lunch Box&a kit.")
        }
        gui.item(5, looter).onClick runnable@{
            if (this.kits[sender.uniqueId] == "looter") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "looter"
            Chat.sendMessage(sender, "&aYou have selected the &6Looter&a kit.")
        }
        gui.item(6, ecologist).onClick runnable@{
            if (this.kits[sender.uniqueId] == "ecologist") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "ecologist"
            Chat.sendMessage(sender, "&aYou have selected the &6Ecologist&a kit.")
        }
        gui.item(7, farmer).onClick runnable@{
            if (this.kits[sender.uniqueId] == "farmer") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "farmer"
            Chat.sendMessage(sender, "&aYou have selected the &6Farmer&a kit.")
        }
        gui.item(8, horseman).onClick runnable@{
            if (this.kits[sender.uniqueId] == "horseman") {
                Chat.sendMessage(sender, "&cYou already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "horseman"
            Chat.sendMessage(sender, "&aYou have selected the &6Horseman&a kit.")
        }
        gui.item(9, trapper).onClick runnable@{
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