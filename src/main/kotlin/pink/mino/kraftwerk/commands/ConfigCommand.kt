package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder


class ConfigCommand : CommandExecutor {

    private fun getOption(option: String): String {
        val op = SettingsFeature.instance.data!!.getString("game.options.${option}").toBoolean()
        return if (op) {
            "&aEnabled"
        } else {
            "&cDisabled"
        }
    }

    private fun getNether(option: String): String {
        val op = SettingsFeature.instance.data!!.getString("game.nether.${option}").toBoolean()
        return if (op) {
            "&aEnabled"
        } else {
            "&cDisabled"
        }
    }

    private fun getEventTime(event: String): Int {
        return SettingsFeature.instance.data!!.getInt("game.events.${event}")
    }

    private fun getRule(rule: String): String {
        val op = SettingsFeature.instance.data!!.getString("game.rules.${rule}").toBoolean()
        return if (op) {
            "&aAllowed"
        } else {
            "&cNot Allowed"
        }
    }

    private fun getFinalBorder(): Int {
        val op = ScenarioHandler.getScenario("bigcrack")!!.enabled
        return if (op) {
            75
        } else {
            25
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        val gui = GuiBuilder().rows(3).name(ChatColor.translateAlternateColorCodes('&', "&4UHC Config"))

        sender.sendMessage(Chat.colored("${Chat.prefix} Opening the UHC configuration..."))
        val options = ItemBuilder(Material.LAVA_BUCKET)
            .name(" &4&lGeneral Settings ")
            .addLore(" ")
            .addLore(" &7Horses ${Chat.dash} &7${getOption("horses")} ")
            .addLore(" &7Starter Food ${Chat.dash} &f${SettingsFeature.instance.data!!.getInt("game.starterfood")} ")
            .addLore(" ")
            .addLore(" &7Statless ${Chat.dash} &7${getOption("statless")} ")
            .addLore(" &7Double Arrows ${Chat.dash} &7${getOption("doublearrows")}")
            .addLore(" ")
            .addLore(" &7Pearl Damage ${Chat.dash} &7${getOption("pearldamage")} ")
            .addLore(" &7Pearl Cooldown ${Chat.dash} &7${getOption("pearlcooldown")} ")
            .addLore(" ")
            .make()
        gui.item(3, options).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig options")
            }
        }
        val host = ItemBuilder(Material.SKULL_ITEM)
            .toSkull()
            .setOwner(SettingsFeature.instance.data!!.getString("game.host"))
            .name(" &4&lHost ")
            .addLore(" ")
            .addLore(" &7Host ${Chat.dash} &f${SettingsFeature.instance.data!!.getString("game.host")} ")
            .addLore(" ")
            .addLore(" &7Matchpost ${Chat.dash} &fhttps://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")} ")
            .addLore(" &7Game ${Chat.dash} &f${SettingsFeature.instance.data!!.getString("matchpost.host")} ")
            .addLore(" ")
            .make()
        gui.item(4, host).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig host")
            }
            Chat.sendMessage(sender, "${Chat.prefix} Matchpost: &fhttps://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")} ")
        }
        val events = ItemBuilder(Material.WATCH)
            .name(" &4&lEvents")
            .addLore(" ")
            .addLore(" &7Final Heal is in &f${getEventTime("final-heal")} minutes ")
            .addLore(" &7PvP is in &f${getEventTime("pvp") + getEventTime("final-heal")} minutes ")
            .addLore(" &7Meetup is in &f${getEventTime("meetup") + getEventTime("pvp") + getEventTime("final-heal")} minutes ")
            .addLore(" ")
            .make()
        gui.item(5, events).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig events")
            }
        }
        val healConfig = ItemBuilder(Material.GOLDEN_APPLE)
            .name(" &4&lHealing Config")
            .addLore(" ")
            .addLore(" &7Absorption ${Chat.dash} &7${getOption("absorption")} ")
            .addLore(" ")
            .addLore(" &7Notch Apples ${Chat.dash} &7${getOption("notchapples")} ")
            .addLore(" &7Golden Heads ${Chat.dash} &7${getOption("goldenheads")} &8(&a4 ❤&8) ")
            .addLore(" ")
            .make()
        gui.item(10, healConfig).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig options")
            }
        }
        val ratesConfig = ItemBuilder(Material.FLINT)
            .name(" &4&lRates Config")
            .addLore(" ")
            .addLore(" &7Apple Rates ${Chat.dash} &a${SettingsFeature.instance.data!!.getInt("game.rates.apple")}% ")
            .addLore(" &7Flint Rates ${Chat.dash} &a${SettingsFeature.instance.data!!.getInt("game.rates.flint")}% ")
            .addLore(" ")
            .make()
        gui.item(11, ratesConfig).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig rates")
            }
        }
        val ffa = if (SettingsFeature.instance.data!!.getBoolean("game.ffa")) {
            "&cDisabled"
        } else {
            "&aEnabled"
        }
        val teamConfig = ItemBuilder(Material.IRON_SWORD)
            .setAmount(SettingsFeature.instance.data!!.getInt("game.teamSize"))
            .name(" &4&lTeam Config")
            .noAttributes()
            .addLore("")
        if (SettingsFeature.instance.data!!.getInt("game.teamSize") == 1) {
            teamConfig.addLore(" &7Team Size ${Chat.dash} &fFFA ")
        } else {
            teamConfig.addLore(" &7Team Size ${Chat.dash} &fTo${SettingsFeature.instance.data!!.getInt("game.teamSize")} ")
        }
        teamConfig.addLore(" &7Team Management ${Chat.dash} &f${ffa} ").addLore(" ")
        val teamConf = teamConfig.make()
        gui.item(12, teamConf).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig teams")
            }
        }
        val scenarios = ItemBuilder(Material.EMERALD)
            .name(" &4&lScenarios ")
            .addLore(" ")
            .addLore(" &7Scenarios &8(&e${ScenarioHandler.getActiveScenarios().size}&8) ${Chat.dash} ")
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            scenarios.addLore("  &8• &f${scenario.name}")
        }
        scenarios.addLore(" ")
        gui.item(13, scenarios.make()).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "sm")
            }
        }
        val enchanting = ItemBuilder(Material.ENCHANTMENT_TABLE)
            .name(" &4&lEnchanting Config ")
            .addLore(" ")
            .addLore(" &7Enchanting ${Chat.dash} &e1.8 ")
            .addLore("")
            .addLore(" &7Split Enchants ${Chat.dash} ${getOption("splitenchants")} ")
            .addLore(" &7Bookshelves ${Chat.dash} ${getOption("bookshelves")} ")
            .addLore(" &7Fire Weapons ${Chat.dash} ${getOption("fireweapons")} ")
            .addLore(" ")
            .make()
        gui.item(14, enchanting).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig options")
            }
        }
        val border = ItemBuilder(Material.BEDROCK)
            .name(" &4&lBorder Config ")
            .addLore(" ")
            .addLore(" &7Size ${Chat.dash} &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2}x${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} &8(&f±${SettingsFeature.instance.data!!.getInt("pregen.border")}&8) ")
            .addLore(" ")
            .addLore(" &7The border shrinks every &e5 minutes&7. ")
            .addLore(" &7The first shrink will be &f500x500&7. ")
            .addLore(" &7The last shrink will be &f${getFinalBorder()}x${getFinalBorder()}&7. ")
            .addLore(" ")
            .make()
        gui.item(15, border).onClick runnable@ {
            it.isCancelled = true
        }
        var goldRates = SettingsFeature.instance.worlds!!.get("${SettingsFeature.instance.data!!.get("pregen.world")}.orerates.gold")
        goldRates = if (goldRates == "0") {
            "&aVanilla"
        } else {
            "&6${SettingsFeature.instance.worlds!!.get("${SettingsFeature.instance.data!!.get("pregen.world")}.orerates.gold")}% Removed"
        }
        var diaRates = SettingsFeature.instance.worlds!!.get("${SettingsFeature.instance.data!!.get("pregen.world")}.orerates.diamond")
        diaRates = if (diaRates == "0") {
            "&aVanilla"
        } else {
            "&b${SettingsFeature.instance.worlds!!.get("${SettingsFeature.instance.data!!.get("pregen.world")}.orerates.diamond")}% Removed"
        }
        val miningConfig = ItemBuilder(Material.DIAMOND_PICKAXE)
            .name(" &4&lMining Config ")
            .noAttributes()
            .addLore(" ")
            .addLore(" &7Anti-Stone ${Chat.dash} &f${getOption("antistone")} ")
            .addLore(" &7Anti-Burn ${Chat.dash} &f${getOption("antiburn")} ")
            .addLore(" ")
            .addLore(" &7Diamond Ore Rates ${Chat.dash} &f${diaRates} ")
            .addLore(" &7Gold Ore Rates ${Chat.dash} &f${goldRates} ")
            .addLore(" ")
            .addLore(" &7Stripmining ${Chat.dash} &f${getRule("stripmining")} ")
            .addLore(" &7Rollercoastering ${Chat.dash} &f${getRule("rollarcoastering")} ")
            .addLore(" &7Pokeholing ${Chat.dash} &f${getRule("pokeholing")}")
            .addLore(" ")
            .make()
        gui.item(16, miningConfig).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig rules")
            }
        }
        val netherConfig = ItemBuilder(Material.NETHERRACK)
            .name(" &4&lNether Config ")
            .addLore(" ")
            .addLore(" &7Nether ${Chat.dash} &f${getNether("nether")} ")
            .addLore(" &7Nerfed Quartz ${Chat.dash} &f${getNether("nerfedquartz")} ")
            .addLore(" ")
            .addLore(" &7Tier II Potions ${Chat.dash} &f${getNether("tierii")} ")
            .addLore(" &7Strength Potions ${Chat.dash} &f${getNether("strengthpotions")} ")
            .addLore(" &7Splash Potions ${Chat.dash} &f${getNether("splashpotions")} ")
            .addLore(" ")
            .make()
        val editConfig = ItemBuilder(Material.COMMAND)
            .name(" &4&lEdit Config ")
            .addLore(" ")
            .addLore(" &7Click here to edit the UHC configuration. ")
            .addLore(" ")
            .make()

        val pvpConfig = ItemBuilder(Material.BOOK_AND_QUILL)
            .name(" &4&lPvP/Meetup Config ")
            .addLore(" ")
            .addLore(" &7Stalking ${Chat.dash} &f${getRule("stalking")}")
            .addLore(" &7Stealing ${Chat.dash} &f${getRule("stealing")}")
            .addLore(" ")
            .addLore(" &7Crossteaming ${Chat.dash} &f${getRule("crossteaming")}")
            .addLore(" &7iPvP ${Chat.dash} &cNot Allowed")
            .addLore(" ")
            .addLore(" &7Skybasing ${Chat.dash} &f${getRule("skybasing")}")
            .addLore(" &7Running At Meetup ${Chat.dash} &f${getRule("runningatmu")}")
            .addLore(" ")
            .make()
        if (sender.hasPermission("uhc.staff")) {
            gui.item(22, editConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig")
                }
            }
            gui.item(21, pvpConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig rules")
                }
            }
            gui.item(23, netherConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig nether")
                }
            }
        } else {
            gui.item(21, pvpConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig rules")
                }
            }
            gui.item(23, netherConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig nether")
                }
            }
        }



        sender.playSound(sender.location, Sound.LEVEL_UP, 10.toFloat(), 10.toFloat())
        sender.openInventory(gui.make())
        return true
    }

}