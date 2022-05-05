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
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        } else {
            Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You must be a player to use this command.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} &7Usage: &f/editpregen <border/generation/settings>&7.")
            return false
        }
        val pregenConfig = PregenConfigHandler.getConfig(sender as OfflinePlayer)
        if (pregenConfig == null) {
            Chat.sendMessage(sender, "${Chat.prefix} &7You don't have a pregeneration configuration set up yet.")
            return false
        }
        val gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Edit Pregen Config"))
        if (args[0] == "border") {
            val border = ItemBuilder(Material.BEDROCK)
                .name("&7Border: &c±${pregenConfig.border}")
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
                    meta.displayName = Chat.colored("&7Border: &c±${pregenConfig.border}")
                    it.currentItem.itemMeta = meta
                } else {
                    pregenConfig.border -= 50
                    val meta = it.currentItem.itemMeta
                    meta.displayName = Chat.colored("&7Border: &c±${pregenConfig.border}")
                    it.currentItem.itemMeta = meta
                }
            }
        } else if (args[0] == "generation") {
            val type = ItemBuilder(Material.GRASS)
                .name("&7World Environment: &c${pregenConfig.type}")
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
                meta.displayName = Chat.colored("&7World Environment: &c${pregenConfig.type}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7World environment set to &c${pregenConfig.type.name.uppercase()}&7.")
            }
            val generator = ItemBuilder(Material.DIAMOND_BLOCK)
                .name("&7Generator: &c${pregenConfig.generator}")
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
                        pregenConfig.generator = PregenerationGenerationTypes.SPAWNERS_GALORE
                    }
                    PregenerationGenerationTypes.SPAWNERS_GALORE -> {
                        pregenConfig.generator = PregenerationGenerationTypes.NONE
                    }
                }
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7Generator: &c${pregenConfig.generator}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7Generator set to &c${pregenConfig.generator.name.uppercase()}&7.")
            }
        } else if (args[0] == "settings") {
            val clearTrees = ItemBuilder(Material.LEAVES)
                .name("&7Clear Trees: &c${pregenConfig.clearTrees}")
                .addLore("&7Click to toggle clearing trees.")
                .addLore(" ")
                .addLore("&8Left Click &7to toggle clearing trees.")
                .addLore(" ")
                .make()
            val clearWater = ItemBuilder(Material.WATER_BUCKET)
                .name("&7Clear Water: &c${pregenConfig.clearWater}")
                .addLore("&7Click to toggle clearing water.")
                .addLore(" ")
                .addLore("&8Left Click &7to toggle clearing water.")
                .addLore(" ")
                .make()
            gui.item(3, clearTrees).onClick {
                it.isCancelled = true
                pregenConfig.clearTrees = !pregenConfig.clearTrees
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7Clear Trees: &c${pregenConfig.clearTrees}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7Clear trees set to &c${pregenConfig.clearTrees}&7.")
            }
            gui.item(5, clearWater).onClick {
                it.isCancelled = true
                pregenConfig.clearWater = !pregenConfig.clearWater
                val meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("&7Clear Water: &c${pregenConfig.clearWater}")
                it.currentItem.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} &7Clear water set to &c${pregenConfig.clearWater}&7.")
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