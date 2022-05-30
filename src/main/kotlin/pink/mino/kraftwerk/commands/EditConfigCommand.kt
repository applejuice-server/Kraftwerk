package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder

class EditConfigCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "${Chat.prefix} &cYou don't have permission to use this command.")
                return false
            }
        }
        val player = sender as Player
        var gui: GuiBuilder? = null
        var size: Int = 35
        if (args.isEmpty()) {
            gui = GuiBuilder().rows(2).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
            size = 17
            val rates = ItemBuilder(Material.FLINT)
                .name("&cRates")
                .addLore("&7Click here to edit rates.")
                .make()
            gui.item(0, rates).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig rates")
            }
            val settings = ItemBuilder(Material.LAVA_BUCKET)
                .name("&cSettings")
                .addLore("&7Click here to edit general options.")
                .make()
            gui.item(1, settings).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig options")
            }
            val teams = ItemBuilder(Material.DIAMOND_SWORD)
                .name("&cTeams")
                .addLore("&7Click here to edit teams.")
                .make()
            gui.item(2, teams).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig teams")
            }
            val starterFood = ItemBuilder(Material.COOKED_BEEF)
                .name("&cStarter Food")
                .addLore("&7Click here to edit starter food.")
                .make()
            gui.item(3, starterFood).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig starterfood")
            }
            val host = ItemBuilder(Material.COMPASS)
                .name("&cHost")
                .addLore("&7Click here to set yourself as the host.")
                .make()
            gui.item(4, host).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig host")
            }
            val events = ItemBuilder(Material.WATCH)
                .name("&cEvents")
                .addLore("&7Click here to edit events.")
                .make()
            gui.item(5, events).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig events")
            }
            val nether = ItemBuilder(Material.NETHER_STAR)
                .name("&cNether")
                .addLore("&7Click here to edit nether options.")
                .make()
            gui.item(6, nether).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig nether")
            }
            val rules = ItemBuilder(Material.PAPER)
                .name("&cRules")
                .addLore("&7Click here to edit rules.")
                .make()
            gui.item(7, rules).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig rules")
            }
        } else if (args[0].lowercase() == "options") {
            gui = GuiBuilder().rows(2).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
            size = 17
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "options") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "&a"
                    else "&c"
                    itemMeta.displayName = Chat.colored("${color}${option.name}")
                    itemMeta.lore = Chat.scenarioTextWrap(Chat.colored("&7${option.description}"), 40)
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
        } else if (args[0].lowercase() == "nether") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
            size = 8
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "nether") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "&a"
                    else "&c"
                    itemMeta.displayName = Chat.colored("${color}${option.name}")
                    itemMeta.lore = Chat.scenarioTextWrap(Chat.colored("&7${option.description}"), 40)
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
            gui = GuiBuilder().rows(2).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
            size = 17
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "rules") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "&a"
                    else "&c"
                    itemMeta.displayName = Chat.colored("${color}${option.name}")
                    itemMeta.lore = Chat.scenarioTextWrap(Chat.colored("&7${option.description}"), 40)
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
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
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
                Chat.colored("&8Left Click&7 to add &aone&7."),
                Chat.colored("&8Right Click&7 to subtract &cone&7.")
            )
            pvpMeta.lore = listOf(
                Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7."),
                "",
                Chat.colored("&8Left Click&7 to add &aone&7."),
                Chat.colored("&8Right Click&7 to subtract &cone&7.")
            )
            muMeta.lore = listOf(
                Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7."),
                "",
                Chat.colored("&8Left Click&7 to add &aone&7."),
                Chat.colored("&8Right Click&7 to subtract &cone&7.")
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
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.events.final-heal") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this timer is already at 1 minute.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.events.final-heal", SettingsFeature.instance.data!!.getInt("game.events.final-heal") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Final Heal happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7.")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
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
                        Chat.colored(Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.events.pvp") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this timer is already at 1 minute.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.events.pvp", SettingsFeature.instance.data!!.getInt("game.events.pvp") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7PvP happens in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
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
                        Chat.colored(Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.events.meetup") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this timer is already at 1 minute.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.events.meetup", SettingsFeature.instance.data!!.getInt("game.events.meetup") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Meetup happens in &c${SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
        } else if (args[0].lowercase() == "host") {
            SettingsFeature.instance.data!!.set("game.host", player.name)
            SettingsFeature.instance.saveData()
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &e${player.name}&7 has set themself as the host."))
            player.playSound(player.location, Sound.LEVEL_UP, 10.toFloat(), 1.toFloat())
            return true
        } else if (args[0].lowercase() == "starterfood") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
            size = 8
            val starterFood = ItemStack(Material.COOKED_BEEF)
            val starterFoodMeta = starterFood.itemMeta
            starterFoodMeta.displayName = Chat.colored("&4Starter Food")
            starterFoodMeta.lore = listOf(
                Chat.colored(Chat.colored("&7Starter Food ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.starterfood")}")),
                "",
                Chat.colored("&8Left Click&7 to add &aone&7."),
                Chat.colored("&8Right Click&7 to subtract &cone&7.")
            )
            starterFood.itemMeta = starterFoodMeta
            gui.item(4, starterFood).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    SettingsFeature.instance.data!!.set("game.starterfood", SettingsFeature.instance.data!!.getInt("game.starterfood") + 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Starter Food ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.starterfood")}")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.starterfood") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 1 starter food.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.starterfood", SettingsFeature.instance.data!!.getInt("game.starterfood") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Starter Food ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.starterfood")}")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
        } else if (args[0].lowercase() == "teams") {
            gui = GuiBuilder().rows(3).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
            size = 26
            val teamSize = ItemStack(Material.IRON_SWORD, SettingsFeature.instance.data!!.getInt("game.teamSize"))
            val teamSizeMeta = teamSize.itemMeta
            teamSizeMeta.displayName = Chat.colored("&cTeam Size")
            teamSizeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            teamSizeMeta.lore = listOf(
                Chat.colored("&7Team Size ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.teamSize")}"),
                "",
                Chat.colored("&8Left Click&7 to add &aone&7."),
                Chat.colored("&8Right Click&7 to subtract &cone&7.")
            )
            teamSize.itemMeta = teamSizeMeta
            gui.item(10, teamSize).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.teamSize") >= 64) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't add, this count is already at 64.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.teamSize", SettingsFeature.instance.data!!.getInt("game.teamSize") + 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    val item = ItemStack(it.currentItem.type, SettingsFeature.instance.data!!.getInt("game.teamSize"))
                    meta.displayName = Chat.colored("&cTeam Size")
                    meta.lore = listOf(
                        Chat.colored("&7Team Size ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.teamSize")}"),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem = item
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.teamSize") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't add, this count is already at 1.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.teamSize", SettingsFeature.instance.data!!.getInt("game.teamSize") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    val item = ItemStack(it.currentItem.type, SettingsFeature.instance.data!!.getInt("game.teamSize"))
                    meta.displayName = Chat.colored("&cTeam Size")
                    meta.lore = listOf(
                        Chat.colored("&7Team Size ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.teamSize")}"),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem = item
                    it.currentItem.itemMeta = meta
                }
            }
            val teamManagement: ItemStack = if (SettingsFeature.instance.data!!.getBoolean("game.ffa")) {
                ItemStack(Material.WOOL, 1, 14)
            } else {
                ItemStack(Material.WOOL, 1, 5)
            }
            val teamManagementMeta = teamManagement.itemMeta
            teamManagementMeta.displayName = Chat.colored("&cTeam Management")
            var status = if (SettingsFeature.instance.data!!.getBoolean("game.ffa")) {
                "&cDisabled"
            } else {
                "&aEnabled"
            }
            teamManagementMeta.lore = listOf(
                Chat.colored("&7Status: $status"),
                "",
                Chat.colored("&8Left Click&7 to toggle.")
            )
            teamManagement.itemMeta = teamManagementMeta
            gui.item(12, teamManagement).onClick runnable@ {
                it.isCancelled = true
                status = if (SettingsFeature.instance.data!!.getBoolean("game.ffa")) {
                    "&aEnabled"
                } else {
                    "&cDisabled"
                }
                if (SettingsFeature.instance.data!!.getBoolean("game.ffa")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team management on")
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team management off")
                }
                val wool: ItemStack = if (SettingsFeature.instance.data!!.getBoolean("game.ffa")) {
                    ItemStack(Material.WOOL, 1, 14)
                } else {
                    ItemStack(Material.WOOL, 1, 5)
                }
                it.currentItem = wool
                val meta = it.currentItem.itemMeta
                meta.lore = listOf(
                    Chat.colored("&7Status: $status"),
                    "",
                    Chat.colored("&8Left Click&7 to toggle.")
                )
                meta.displayName = Chat.colored("&cTeam Management")
                it.currentItem.itemMeta = meta
            }
            val randomizeTeams = ItemStack(Material.EYE_OF_ENDER)
            val randomizeTeamsMeta = randomizeTeams.itemMeta
            randomizeTeamsMeta.displayName = Chat.colored("&cRandomize Teams")
            randomizeTeamsMeta.lore = listOf(
                Chat.colored("&7Click to randomize all players into teams of &c${SettingsFeature.instance.data!!.getInt("game.teamSize")}&7."),
                "",
                Chat.colored("&4&lWARNING&7 All players that are not"),
                Chat.colored("&7going to play must be in Spectator mode.")
            )
            randomizeTeams.itemMeta = randomizeTeamsMeta
            gui.item(14, randomizeTeams).onClick runnable@ {
                it.isCancelled = true
                Bukkit.dispatchCommand(player, "team randomize")
            }

            val resetTeams = ItemStack(Material.BARRIER)
            val resetTeamsMeta = randomizeTeams.itemMeta
            resetTeamsMeta.displayName = Chat.colored("&cReset Teams")
            resetTeamsMeta.lore = listOf(
                Chat.colored("&7Click to reset all teams."),
                "",
                Chat.colored("&4&lWARNING&7 This is probably a bad idea!")
            )
            resetTeams.itemMeta = resetTeamsMeta
            gui.item(16, resetTeams).onClick runnable@ {
                it.isCancelled = true
                Bukkit.dispatchCommand(player, "team reset")
            }
        } else if (args[0].lowercase() == "rates") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config")).owner(sender)
            size = 8
            val flintRates = ItemStack(Material.FLINT)
            val flintRatesMeta = flintRates.itemMeta
            flintRatesMeta.displayName = Chat.colored("&4Flint Rates")
            flintRatesMeta.lore = listOf(
                Chat.colored(Chat.colored("&7Flint Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.flint")}%")),
                "",
                Chat.colored("&8Left Click&7 to add &aone&7."),
                Chat.colored("&8Right Click&7 to subtract &cone&7.")
            )
            flintRates.itemMeta = flintRatesMeta
            gui.item(3, flintRates).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.rates.flint") >= 100) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't add, this count is already at 100%.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.rates.flint", SettingsFeature.instance.data!!.getInt("game.rates.flint") + 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Flint Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.flint")}%")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.rates.flint") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 1%.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.rates.flint", SettingsFeature.instance.data!!.getInt("game.rates.flint") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Flint Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.flint")}%")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            val appleRates = ItemStack(Material.APPLE)
            val appleRatesMeta = appleRates.itemMeta
            appleRatesMeta.displayName = Chat.colored("&4Apple Rates")
            appleRatesMeta.lore = listOf(
                Chat.colored(Chat.colored("&7Apple Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.apple")}%")),
                "",
                Chat.colored("&8Left Click&7 to add &aone&7."),
                Chat.colored("&8Right Click&7 to subtract &cone&7.")
            )
            appleRates.itemMeta = appleRatesMeta
            gui.item(5, appleRates).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.rates.apple") >= 100) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 100%.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.rates.apple", SettingsFeature.instance.data!!.getInt("game.rates.apple") + 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Apple Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.apple")}%")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
                    )
                    it.currentItem.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (SettingsFeature.instance.data!!.getInt("game.rates.apple") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 1%.")
                        return@runnable
                    }
                    SettingsFeature.instance.data!!.set("game.rates.apple", SettingsFeature.instance.data!!.getInt("game.rates.apple") - 1)
                    SettingsFeature.instance.saveData()
                    val meta = it.currentItem.itemMeta
                    meta.lore = listOf(
                        Chat.colored(Chat.colored("&7Apple Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.apple")}%")),
                        "",
                        Chat.colored("&8Left Click&7 to add &aone&7."),
                        Chat.colored("&8Right Click&7 to subtract &cone&7.")
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
        gui!!.item(size, back).onClick runnable@ {
            it.isCancelled = true
            sender.closeInventory()
            if (args.isEmpty()) {
                Bukkit.getServer().dispatchCommand(player as CommandSender, "uhc")
            } else {
                Bukkit.getServer().dispatchCommand(player as CommandSender, "editconfig")
            }
        }
        player.openInventory(gui.make())
        return true
    }
}