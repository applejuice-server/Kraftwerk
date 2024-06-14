package pink.mino.kraftwerk.commands

import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder

class EditPregenCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.pregen")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        } else {
            Chat.sendMessage(sender, "${ChatColor.RED}You must be a player to use this command.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} &7Usage: ${Chat.secondaryColor}/editpregen <border/generation/settings>&7.")
            return false
        }
        val pregenConfig = PregenConfigHandler.getConfig(sender as OfflinePlayer)
        if (pregenConfig == null) {
            Chat.sendMessage(sender, "${Chat.prefix} &7You don't have a pregeneration configuration set up yet.")
            return false
        }
        val gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit Pregen Config")).owner(sender)
        if (args[0] == "border") {
            val border = ItemBuilder(Material.BEDROCK)
                .name("&7Border: ${Chat.primaryColor}±${pregenConfig.border}")
                .addLore("&7Click to change the border size.")
                .addLore(" ")
                .addLore("&8Left Click: &a+50")
                .addLore("&8Right Click: &c-50")
                .addLore(" ")
                .make()
            gui.item(4, border).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.border += 50
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Border: ${Chat.primaryColor}±${pregenConfig.border}")
                    it.currentItem.itemMeta = meta
                } else {
                    pregenConfig.border -= 50
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Border: ${Chat.primaryColor}±${pregenConfig.border}")
                    it.currentItem.itemMeta = meta
                }
            }
        } else if (args[0] == "generation") {
            val type = ItemBuilder(Material.GRASS)
                .name("&7World Environment: ${Chat.primaryColor}${pregenConfig.type}")
                .addLore("&7Click to change the generation type.")
                .addLore(" ")
                .addLore("&8Left Click &7to toggle between the types.")
                .addLore(" ")
                .make()
            gui.item(3, type).onClick {
                it.isCancelled = true
                when (pregenConfig.type) {
                    World.Environment.NORMAL -> {
                        pregenConfig.type = World.Environment.NETHER
                        it.currentItem.type = Material.NETHERRACK
                    }
                    World.Environment.NETHER -> {
                        pregenConfig.type = World.Environment.THE_END
                        it.currentItem.type = Material.ENDER_STONE
                    }
                    World.Environment.THE_END -> {
                        pregenConfig.type = World.Environment.NORMAL
                        it.currentItem.type = Material.GRASS
                    }
                }
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7World Environment: ${Chat.primaryColor}${pregenConfig.type}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7World environment set to ${Chat.primaryColor}${pregenConfig.type.name.uppercase()}&7.")
            }
            val generator = ItemBuilder(Material.DIAMOND_BLOCK)
                .name("&7Generator: ${Chat.primaryColor}${pregenConfig.generator}")
                .addLore("&7Click to change the generator type.")
                .addLore(" ")
                .addLore("&8Left Click &7to toggle between the generator types.")
                .addLore(" ")
                .make()
            gui.item(5, generator).onClick {
                it.isCancelled = true
                when (pregenConfig.generator) {
                    PregenerationGenerationTypes.NONE -> {
                        pregenConfig.generator = PregenerationGenerationTypes.CITY_WORLD
                    }
                    PregenerationGenerationTypes.CITY_WORLD -> {
                        pregenConfig.generator = PregenerationGenerationTypes.NONE
                    }
                }
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7Generator: ${Chat.primaryColor}${pregenConfig.generator}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7Generator set to ${Chat.primaryColor}${pregenConfig.generator.name.uppercase()}&7.")
            }
        } else if (args[0] == "settings") {
            val oresOutsideCaves = ItemBuilder(Material.DIAMOND_PICKAXE)
                .name("&7Ores Outside Caves: ${Chat.primaryColor}${pregenConfig.oresOutsideCaves}")
                .addLore("&7Click to toggle spawning ores outside caves.")
                .addLore(" ")
                .addLore("&8Left Click &7to toggle spawning ores outside caves.")
                .addLore(" ")
                .make()
            val clearTrees = ItemBuilder(Material.LEAVES)
                .name("&7Clear Trees: ${Chat.primaryColor}${pregenConfig.clearTrees}")
                .addLore("&7Click to toggle clearing trees.")
                .addLore(" ")
                .addLore("&8Left Click &7to toggle clearing trees.")
                .addLore(" ")
                .make()
            val clearWater = ItemBuilder(Material.WATER_BUCKET)
                .name("&7Clear Water: ${Chat.primaryColor}${pregenConfig.clearWater}")
                .addLore("&7Click to toggle clearing water.")
                .addLore(" ")
                .addLore("&8Left Click &7to toggle clearing water.")
                .addLore(" ")
                .make()
            val diaRates = ItemBuilder(Material.DIAMOND_ORE)
                .name("&7Diamond Ore Rates: ${Chat.primaryColor}${pregenConfig.diamondore}% Removed")
                .addLore("&7Click to change the diamond ore rates.")
                .addLore(" ")
                .addLore("&8Left Click: &a+5")
                .addLore("&8Right Click: &c-5")
                .addLore(" ")
                .make()
            val goldRates = ItemBuilder(Material.GOLD_ORE)
                .name("&7Gold Ore Rates: ${Chat.primaryColor}${pregenConfig.goldore}% Removed")
                .addLore("&7Click to change the gold ore rates.")
                .addLore(" ")
                .addLore("&8Left Click: &a+5")
                .addLore("&8Right Click: &c-5")
                .addLore(" ")
                .make()
            val caneRates = ItemBuilder(Material.SUGAR_CANE)
                .name("&7Cane Rates: ${Chat.primaryColor}${pregenConfig.canerate}% Increased")
                .addLore("&7Click to change the cane rates.")
                .addLore(" ")
                .addLore("&8Left Click: &a+5")
                .addLore("&8Right Click: &c-5")
                .addLore(" ")
                .make()
            gui.item(1, oresOutsideCaves).onClick {
                it.isCancelled = true
                pregenConfig.oresOutsideCaves = !pregenConfig.oresOutsideCaves
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7Ores Outside Caves: ${Chat.primaryColor}${pregenConfig.oresOutsideCaves}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7Ores outside caves set to ${Chat.primaryColor}${pregenConfig.oresOutsideCaves}&7.")
            }
            gui.item(2, clearTrees).onClick {
                it.isCancelled = true
                pregenConfig.clearTrees = !pregenConfig.clearTrees
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7Clear Trees: ${Chat.primaryColor}${pregenConfig.clearTrees}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7Clear trees set to ${Chat.primaryColor}${pregenConfig.clearTrees}&7.")
            }
            gui.item(3, clearWater).onClick {
                it.isCancelled = true
                pregenConfig.clearWater = !pregenConfig.clearWater
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7Clear Water: ${Chat.primaryColor}${pregenConfig.clearWater}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7Clear water set to ${Chat.primaryColor}${pregenConfig.clearWater}&7.")
            }
            gui.item(4, diaRates).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.diamondore += 5
                    if (pregenConfig.diamondore > 100) {
                        pregenConfig.diamondore = 100
                    }
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Diamond Ore Rates: ${Chat.primaryColor}${pregenConfig.diamondore}% Removed")
                    it.currentItem.itemMeta = meta
                } else {
                    pregenConfig.diamondore -= 5
                    if (pregenConfig.diamondore < 0) {
                        pregenConfig.diamondore = 0
                    }
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Diamond Ore Rates: ${Chat.primaryColor}${pregenConfig.diamondore}% Removed")
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(5, goldRates).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.goldore += 5
                    if (pregenConfig.goldore > 100) {
                        pregenConfig.goldore = 100
                    }
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Gold Ore Rates: ${Chat.primaryColor}${pregenConfig.goldore}% Removed")
                    it.currentItem.itemMeta = meta
                } else {
                    pregenConfig.goldore -= 5
                    if (pregenConfig.goldore < 0) {
                        pregenConfig.goldore = 0
                    }
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Gold Ore Rates: ${Chat.primaryColor}${pregenConfig.goldore}% Removed")
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(6, caneRates).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.canerate += 5
                    if (pregenConfig.canerate > 100) {
                        pregenConfig.canerate = 100
                    }
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Cane Rates: ${Chat.primaryColor}${pregenConfig.canerate}% Increased")
                    it.currentItem.itemMeta = meta
                } else {
                    pregenConfig.canerate -= 5
                    if (pregenConfig.canerate < 0) {
                        pregenConfig.canerate = 0
                    }
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Cane Rates: ${Chat.primaryColor}${pregenConfig.canerate}% Increased")
                    it.currentItem.itemMeta = meta
                }
            }
        }
        val back = ItemStack(Material.ARROW)
        val backMeta = back.itemMeta
        backMeta.displayName = Chat.colored("&cBack")
        backMeta.lore = listOf(
            Chat.colored("&7Go back to the pregen config menu.")
        )
        back.itemMeta = backMeta
        gui.item(8, back).onClick runnable@ {
            it.isCancelled = true
            sender.closeInventory()
            Bukkit.getServer().dispatchCommand(sender as CommandSender, "pregen ${pregenConfig.name}")
        }
        sender.openInventory(gui.make())
        return true
    }
}