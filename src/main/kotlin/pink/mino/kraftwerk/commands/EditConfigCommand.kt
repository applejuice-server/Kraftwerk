package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
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
            gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config"))
            size = 44
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
                Chat.colored("&7Final Heal happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7.")
            )
            pvpMeta.lore = listOf(
                Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")
            )
            muMeta.lore = listOf(
                Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")
            )

            finalHeal.itemMeta = fhMeta
            pvp.itemMeta = pvpMeta
            meetup.itemMeta = muMeta

            gui.item(20, finalHeal).onClick runnable@ {
                it.isCancelled = true
            }
            gui.item(22, pvp).onClick runnable@ {
                it.isCancelled = true
            }
            gui.item(24, meetup).onClick runnable@ {
                it.isCancelled = true
            }

            val addFinalHeal = ItemStack(Material.WOOL, 1, 5)
            val addPvP = ItemStack(Material.WOOL, 1, 5)
            val addMeetup = ItemStack(Material.WOOL, 1, 5)

            val removeFinalHeal = ItemStack(Material.WOOL, 1, 14)
            val removePvP = ItemStack(Material.WOOL, 1, 14)
            val removeMeetup = ItemStack(Material.WOOL, 1, 14)

            val afhMeta = addFinalHeal.itemMeta
            afhMeta.displayName = Chat.colored("&aAdd +1")
            afhMeta.lore = listOf(
                Chat.colored("&7Add one to the Final Heal timer.")
            )
            val apMeta = addPvP.itemMeta
            apMeta.displayName = Chat.colored("&aAdd +1")
            apMeta.lore = listOf(
                Chat.colored("&7Add one to the PvP timer.")
            )
            val amMeta = addMeetup.itemMeta
            amMeta.displayName = Chat.colored("&aAdd +1")
            amMeta.lore = listOf(
                Chat.colored("&7Add one to the Meetup timer.")
            )

            val rfhMeta = removeFinalHeal.itemMeta
            rfhMeta.displayName = Chat.colored("&cRemove -1")
            rfhMeta.lore = listOf(
                Chat.colored("&7Remove one from the Final Heal timer.")
            )
            val rpMeta = removePvP.itemMeta
            rpMeta.displayName = Chat.colored("&cRemove -1")
            rpMeta.lore = listOf(
                Chat.colored("&7Remove one from the PvP timer.")
            )
            val rmMeta = removeMeetup.itemMeta
            rmMeta.displayName = Chat.colored("&cRemove -1")
            rmMeta.lore = listOf(
                Chat.colored("&7Remove one from the Meetup timer.")
            )

            addFinalHeal.itemMeta = afhMeta
            addPvP.itemMeta = apMeta
            addMeetup.itemMeta = amMeta

            removeMeetup.itemMeta = rmMeta
            removePvP.itemMeta = rpMeta
            removeFinalHeal.itemMeta = rfhMeta

            gui.item(11, addFinalHeal).onClick runnable@ { clickEvent ->
                clickEvent.isCancelled = true
                SettingsFeature.instance.data!!.set("game.events.final-heal", SettingsFeature.instance.data!!.getInt("game.events.final-heal") + 1)
                SettingsFeature.instance.saveData()
                fhMeta.lore = listOf(
                    Chat.colored("&7Final Heal happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7.")
                )
                finalHeal.itemMeta = fhMeta
                gui.item(20, finalHeal).onClick runnable@ {
                    it.isCancelled = true
                }
            }
            gui.item(13, addPvP).onClick runnable@ { it ->
                it.isCancelled = true
                SettingsFeature.instance.data!!.set("game.events.pvp", SettingsFeature.instance.data!!.getInt("game.events.pvp") + 1)
                SettingsFeature.instance.saveData()
                pvpMeta.lore = listOf(
                    Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")
                )
                pvp.itemMeta = pvpMeta
                gui.item(22, pvp).onClick runnable@ {
                    it.isCancelled = true
                }
            }
            gui.item(15, addMeetup).onClick runnable@ { clickEvent ->
                clickEvent.isCancelled = true
                SettingsFeature.instance.data!!.set("game.events.meetup", SettingsFeature.instance.data!!.getInt("game.events.meetup") + 1)
                SettingsFeature.instance.saveData()
                muMeta.lore = listOf(
                    Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")
                )
                meetup.itemMeta = muMeta
                gui.item(24, meetup).onClick runnable@ {
                    it.isCancelled = true
                }
            }

            gui.item(29, removeFinalHeal).onClick runnable@ { clickEvent ->
                clickEvent.isCancelled = true
                SettingsFeature.instance.data!!.set("game.events.final-heal", SettingsFeature.instance.data!!.getInt("game.events.final-heal") - 1)
                SettingsFeature.instance.saveData()
                fhMeta.lore = listOf(
                    Chat.colored("&7Final Heal happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7.")
                )
                finalHeal.itemMeta = fhMeta
                gui.item(20, finalHeal).onClick runnable@ {
                    it.isCancelled = true
                }
            }
            gui.item(31, removePvP).onClick runnable@ { clickEvent ->
                clickEvent.isCancelled = true
                SettingsFeature.instance.data!!.set("game.events.pvp", SettingsFeature.instance.data!!.getInt("game.events.pvp") - 1)
                SettingsFeature.instance.saveData()
                pvpMeta.lore = listOf(
                    Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")
                )
                pvp.itemMeta = pvpMeta
                gui.item(22, pvp).onClick runnable@ {
                    it.isCancelled = true
                }

            }
            gui.item(33, removeMeetup).onClick runnable@ { clickEvent ->
                clickEvent.isCancelled = true
                SettingsFeature.instance.data!!.set("game.events.meetup", SettingsFeature.instance.data!!.getInt("game.events.meetup") - 1)
                SettingsFeature.instance.saveData()
                muMeta.lore = listOf(
                    Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")
                )
                meetup.itemMeta = muMeta
                gui.item(24, meetup).onClick runnable@ {
                    it.isCancelled = true
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