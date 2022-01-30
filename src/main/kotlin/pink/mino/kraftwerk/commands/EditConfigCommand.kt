package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.options.ConfigOptionHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder

class EditConfigCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        val player = sender as Player
        if (!player.hasPermission("uhc.staff")) {
            Chat.sendMessage(player, "&cYou don't have permission to execute this command.")
            return false
        }
        if (args.isEmpty()) {
            // TODO("Make a main config editor menu")
            Chat.sendMessage(player, "&cYou need to provide a valid menu to edit.")
            return false
        }
        var gui = GuiBuilder().rows(4).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config"))
        var size: Int = 35
        if (args[0].lowercase() == "options") {
            gui = GuiBuilder().rows(2).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config"))
            size = 17
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "options") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "&a"
                    else "&c"
                    itemMeta.displayName = Chat.colored("${color}${option.name}")
                    itemMeta.lore = listOf(
                        Chat.colored("&7${option.description}")
                    )
                    item.itemMeta = itemMeta
                    gui.item(iterator, item).onClick runnable@ {
                        it.isCancelled = true
                        ConfigOptionHandler.getOption(option.id)?.toggle()
                        color = if (option.enabled) "&a"
                        else "&c"
                        val meta = it.currentItem.itemMeta
                        meta.displayName = Chat.colored("${color}${option.name}")
                        it.currentItem.itemMeta = meta
                    }
                    iterator++
                }
            }
        } else if (args[0].lowercase() == "rules") {
            gui = GuiBuilder().rows(2).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config"))
            size = 17
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "rules") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "&a"
                    else "&c"
                    itemMeta.displayName = Chat.colored("${color}${option.name}")
                    itemMeta.lore = listOf(
                        Chat.colored("&7${option.description}")
                    )
                    item.itemMeta = itemMeta
                    gui.item(iterator, item).onClick runnable@ {
                        it.isCancelled = true
                        ConfigOptionHandler.getOption(option.id)?.toggle()
                        color = if (option.enabled) "&a"
                        else "&c"
                        val meta = it.currentItem.itemMeta
                        meta.displayName = Chat.colored("${color}${option.name}")
                        it.currentItem.itemMeta = meta
                    }
                    iterator++
                }
            }
        } else if (args[0] == "events") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config"))
            size = 8
            val finalHeal = ItemStack(Material.REDSTONE)
            val pvp = ItemStack(Material.IRON_SWORD)
            val meetup = ItemStack(Material.BEACON)

            val fhMeta = finalHeal.itemMeta
            val pvpMeta = pvp.itemMeta
            val muMeta = meetup.itemMeta
            fhMeta.displayName = Chat.colored("&cFinal Heal")
            pvpMeta.displayName = Chat.colored("&cPvP")
            muMeta.displayName = Chat.colored("&cMeetup")

            fhMeta.lore = listOf(
                Chat.colored("&7Final Heal happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7."),
                "",
                Chat.colored("&8LMB&7 to add &aone&7."),
                Chat.colored("&8RMB&7 to subtract &cone&7.")
            )
            pvpMeta.lore = listOf(
                Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7."),
                "",
                Chat.colored("&8LMB&7 to add &aone&7."),
                Chat.colored("&8RMB&7 to subtract &cone&7.")
            )
            muMeta.lore = listOf(
                Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7."),
                "",
                Chat.colored("&8LMB&7 to add &aone&7."),
                Chat.colored("&8RMB&7 to subtract &cone&7.")
            )

            finalHeal.itemMeta = fhMeta
            pvp.itemMeta = pvpMeta
            meetup.itemMeta = muMeta
            gui.item(2, finalHeal).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    SettingsFeature.instance.data!!.set("game.events.final-heal", SettingsFeature.instance.data!!.getInt("game.events.final-heal") + 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Final Heal happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7.")),
                        "",
                        Chat.colored("&8LMB&7 to add &aone&7."),
                        Chat.colored("&8RMB&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    SettingsFeature.instance.data!!.set("game.events.final-heal", SettingsFeature.instance.data!!.getInt("game.events.final-heal") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Final Heal happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7.")),
                        "",
                        Chat.colored("&8LMB&7 to add &aone&7."),
                        Chat.colored("&8RMB&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(4, pvp).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    SettingsFeature.instance.data!!.set("game.events.pvp", SettingsFeature.instance.data!!.getInt("game.events.pvp") + 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")),
                        "",
                        Chat.colored("&8LMB&7 to add &aone&7."),
                        Chat.colored("&8RMB&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    SettingsFeature.instance.data!!.set("game.events.pvp", SettingsFeature.instance.data!!.getInt("game.events.pvp") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")),
                        "",
                        Chat.colored("&8LMB&7 to add &aone&7."),
                        Chat.colored("&8RMB&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(6, meetup).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    SettingsFeature.instance.data!!.set("game.events.meetup", SettingsFeature.instance.data!!.getInt("game.events.meetup") + 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")),
                        "",
                        Chat.colored("&8LMB&7 to add &aone&7."),
                        Chat.colored("&8RMB&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    SettingsFeature.instance.data!!.set("game.events.meetup", SettingsFeature.instance.data!!.getInt("game.events.meetup") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")),
                        "",
                        Chat.colored("&8LMB&7 to add &aone&7."),
                        Chat.colored("&8RMB&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
        }
        val back = ItemStack(Material.ARROW)
        val backMeta = back.itemMeta
        backMeta.displayName = Chat.colored("&cBack")
        backMeta.lore = listOf(
            Chat.colored("&7Go back to the UHC config editing menu.")
        )
        back.itemMeta = backMeta
        gui.item(size, back).onClick runnable@ {
            it.isCancelled = true
            Bukkit.getServer().dispatchCommand(player as CommandSender, "uhc")
        }
        player.openInventory(gui.make())
        return true
    }
}