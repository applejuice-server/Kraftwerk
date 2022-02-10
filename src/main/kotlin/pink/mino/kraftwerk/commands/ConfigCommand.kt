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
import org.bukkit.inventory.meta.SkullMeta
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder


class ConfigCommand : CommandExecutor {

    private fun getOption(option: String): String {
        val op = SettingsFeature.instance.data!!.getString("game.options.${option}").toBoolean()
        return if (op) {
            "Enabled"
        } else {
            "Disabled"
        }
    }

    private fun getEventTime(event: String): Int {
        return SettingsFeature.instance.data!!.getInt("game.events.${event}")
    }

    private fun getRule(rule: String): String {
        val op = SettingsFeature.instance.data!!.getString("game.rules.${rule}").toBoolean()
        return if (op) {
            "Allowed"
        } else {
            "Not allowed"
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        val gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "&4UHC Config"))

        sender.sendMessage(Chat.colored("${Chat.prefix} Opening the UHC configuration..."))
        val options = ItemStack(Material.GOLDEN_APPLE)
        val optionsMeta = options.itemMeta
        optionsMeta.displayName = ChatColor.translateAlternateColorCodes('&', "&4Options")
        optionsMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Absorption ${Chat.dash} &c${getOption("absorption")}"),
            Chat.colored("&7Notch Apples ${Chat.dash} &c${getOption("notchapples")}"),
            Chat.colored("&7Golden Heads ${Chat.dash} &c${getOption("goldenheads")}"),
            Chat.colored("&7Horses ${Chat.dash} &c${getOption("horses")}"),
            Chat.colored("&7Split Enchants ${Chat.dash} &c${getOption("splitenchants")}"),
            Chat.colored("&7Fire Weapons ${Chat.dash} &c${getOption("fireweapons")}"),
            Chat.colored("&7Bookshelves ${Chat.dash} &c${getOption("bookshelves")}"),
            Chat.colored("&7AntiStone ${Chat.dash} &c${getOption("antistone")}"),
            Chat.colored("&7AntiBurn ${Chat.dash} &c${getOption("antiburn")}"),
            Chat.colored(Chat.line),
        )
        options.itemMeta = optionsMeta
        gui.item(20, options).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig options")
            }
            it.isCancelled = true
        }

        val rules = ItemStack(Material.BOOK_AND_QUILL)
        val rulesMeta = rules.itemMeta
        rulesMeta.displayName = ChatColor.translateAlternateColorCodes('&', "&4Rules")
        rulesMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Crossteaming ${Chat.dash} &c${getRule("crossteaming")}"),
            Chat.colored("&7Stalking ${Chat.dash} &c${getRule("stalking")}"),
            Chat.colored("&7Stealing ${Chat.dash} &c${getRule("stealing")}"),
            Chat.colored("&7Skybasing ${Chat.dash} &c${getRule("skybasing")}"),
            Chat.colored("&7Running at Meetup ${Chat.dash} &c${getRule("runningatmu")}"),
            Chat.colored(Chat.line),
        )
        rules.itemMeta = rulesMeta
        gui.item(22, rules).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig rules")
            }
            it.isCancelled = true
        }

        val miningRules = ItemStack(Material.DIAMOND_PICKAXE)
        val miningRulesMeta = miningRules.itemMeta
        miningRulesMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        miningRulesMeta.displayName = ChatColor.translateAlternateColorCodes('&', "&4Mining Rules")
        miningRulesMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Stripmining ${Chat.dash} &c${getRule("stripmining")}"),
            Chat.colored("&7Rollarcoastering ${Chat.dash} &c${getRule("rollarcoastering")}"),
            Chat.colored("&7Pokeholing ${Chat.dash} &c${getRule("pokeholing")}"),
            Chat.colored(Chat.line)
        )
        miningRules.itemMeta = miningRulesMeta
        gui.item(23, miningRules).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig rules")
            }
            it.isCancelled = true
        }
        val border = ItemStack(Material.EMERALD)
        val borderMeta = miningRules.itemMeta
        borderMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        borderMeta.displayName = ChatColor.translateAlternateColorCodes('&', "&4Scenarios")
        borderMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Border ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("pregen.border")}x${SettingsFeature.instance.data!!.getInt("pregen.border")}"),
            Chat.colored(Chat.line)
        )
        border.itemMeta = borderMeta
        gui.item(24, border).onClick runnable@{
            it.isCancelled = true
        }
        val events = ItemStack(Material.WATCH)
        val eventsMeta = events.itemMeta
        eventsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        eventsMeta.displayName = Chat.colored("&4Events")
        eventsMeta.lore = listOf(
            Chat.colored(Chat.line),
            "",
            Chat.colored("&7Final Heal ${Chat.dash} &c${getEventTime("final-heal")} min."),
            Chat.colored("&7PvP ${Chat.dash} &c${getEventTime("pvp") + getEventTime("final-heal")} min."),
            Chat.colored("&7Meetup ${Chat.dash} &c${getEventTime("pvp") + getEventTime("final-heal") + getEventTime("meetup")} min."),
            "",
            Chat.colored(Chat.line)
        )
        events.itemMeta = eventsMeta
        gui.item(30, events).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig events")
            }
            it.isCancelled = true
        }
        val matchpost = ItemStack(Material.PAPER)
        val matchpostMeta = matchpost.itemMeta
        matchpostMeta.displayName = Chat.colored("&4Matchpost")
        if (sender.hasPermission("uhc.staff")) {
            matchpostMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7Coming soon."),
                "",
                Chat.colored(Chat.line),
                Chat.colored("&7Set the matchpost using &c/matchpost <id>")
            )
        } else {
            matchpostMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7Coming soon."),
                "",
                Chat.colored(Chat.line)
            )
        }
        matchpost.itemMeta = matchpostMeta
        gui.item(31, matchpost).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig matchpost")
            }
            it.isCancelled = true
        }
        val potions = ItemStack(Material.GLASS_BOTTLE)
        val potionsMeta = potions.itemMeta
        potionsMeta.displayName = Chat.colored("&4Potions")
        potionsMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Coming soon."),
            Chat.colored(Chat.line)
        )
        potions.itemMeta = potionsMeta
        gui.item(32, potions).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig potions")
            }
            it.isCancelled = true
        }
        val netherConfig = ItemStack(Material.NETHERRACK)
        val netherConfigMeta = netherConfig.itemMeta
        netherConfigMeta.displayName = Chat.colored("&4Nether Config")
        netherConfigMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Coming soon."),
            Chat.colored(Chat.line)
        )
        netherConfig.itemMeta = netherConfigMeta
        gui.item(21, netherConfig).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig nether")
            }
            it.isCancelled = true
        }
        val starterFood = ItemStack(Material.COOKED_BEEF)
        val starterFoodMeta = starterFood.itemMeta
        starterFoodMeta.displayName = Chat.colored("&4Starter Food")
        starterFoodMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Starter Food ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.starterfood")}"),
            Chat.colored(Chat.line)
        )
        starterFood.itemMeta = starterFoodMeta
        gui.item(29, starterFood).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig starterfood")
            }
            it.isCancelled = true
        }
        val rates = ItemStack(Material.FLINT)
        val ratesMeta = rates.itemMeta
        ratesMeta.displayName = Chat.colored("&4Apple/Flint Rates")
        ratesMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Apple Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.apple")}%"),
            Chat.colored("&7Flint Rates ${Chat.dash} &c${SettingsFeature.instance.data!!.getInt("game.rates.flint")}%"),
            Chat.colored(Chat.line)
        )
        rates.itemMeta = ratesMeta
        gui.item(33, rates).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig rates")
            }
            it.isCancelled = true
        }
        val host = ItemStack(Material.SKULL_ITEM, 1, 3)
        val hostMeta = host.itemMeta as SkullMeta
        hostMeta.displayName = Chat.colored("&4Host")
        hostMeta.owner = SettingsFeature.instance.data!!.getString("game.host")
        if (sender.hasPermission("uhc.staff")) {
            hostMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7The host for this game is &c${SettingsFeature.instance.data!!.getString("game.host")}&7."),
                "",
                Chat.colored(Chat.line),
                Chat.colored("&cClick to set yourself as host.")
            )
        } else {
            hostMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7The host for this game is &c${SettingsFeature.instance.data!!.getString("game.host")}&7."),
                "",
                Chat.colored(Chat.line)
            )
        }
        host.itemMeta = hostMeta
        gui.item(13, host).onClick runnable@{
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender as CommandSender, "editconfig host")
            }
            it.isCancelled = true
        }
        sender.playSound(sender.location, Sound.LEVEL_UP, 10.toFloat(), 10.toFloat())
        sender.openInventory(gui.make())
        return true
    }

}