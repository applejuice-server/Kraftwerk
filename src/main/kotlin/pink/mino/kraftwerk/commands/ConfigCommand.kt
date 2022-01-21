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

    private fun getRule(rule: String): String {
        val op = SettingsFeature.instance.data!!.getString("game.rules.${rule}").toBoolean()
        return if (op) {
            "Allowed"
        } else {
            "Not allowed"
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        val player = sender as Player
        val gui = GuiBuilder().rows(3).name(ChatColor.translateAlternateColorCodes('&', "&4UHC Config"))

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
            Chat.colored(Chat.line),
        )
        options.itemMeta = optionsMeta
        gui.item(10, options).onClick runnable@{
            if (player.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(player as CommandSender, "editconfig options")
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
        gui.item(11, rules).onClick runnable@{
            if (player.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(player as CommandSender, "editconfig rules")
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
        gui.item(12, miningRules).onClick runnable@{
            if (player.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(player as CommandSender, "editconfig rules")
            }
            it.isCancelled = true
        }

        val events = ItemStack(Material.WATCH)
        val eventsMeta = events.itemMeta
        eventsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        eventsMeta.displayName = Chat.colored("&4Events")
        eventsMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Final Heal ${Chat.dash} &c"),
            Chat.colored("&7PvP ${Chat.dash} &c"),
            Chat.colored("&7Meetup ${Chat.dash} &c"),
            Chat.colored(Chat.line)
        )
        events.itemMeta = eventsMeta
        gui.item(13, events).onClick runnable@{
            if (player.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(player as CommandSender, "editconfig events")
            }
            it.isCancelled = true
        }
        val matchpost = ItemStack(Material.PAPER)
        val matchpostMeta = matchpost.itemMeta
        matchpostMeta.displayName = Chat.colored("&4Matchpost")
        matchpostMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7Coming soon."),
            Chat.colored(Chat.line)
        )
        matchpost.itemMeta = matchpostMeta
        gui.item(14, matchpost).onClick runnable@{
            if (player.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(player as CommandSender, "editconfig matchpost")
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
        gui.item(15, potions).onClick runnable@{
            if (player.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(player as CommandSender, "editconfig potions")
            }
            it.isCancelled = true
        }
        val host = ItemStack(Material.SKULL_ITEM, 1, 3)
        val hostMeta = host.itemMeta as SkullMeta
        hostMeta.displayName = Chat.colored("&4Host")
        hostMeta.owner = "minota"
        hostMeta.lore = listOf(
            Chat.colored(Chat.line),
            Chat.colored("&7The host for this game is "),
            Chat.colored(Chat.line)
        )
        host.itemMeta = hostMeta
        gui.item(16, host).onClick runnable@{
            if (player.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(player as CommandSender, "editconfig host")
            }
            it.isCancelled = true
        }
        player.playSound(player.location, Sound.LEVEL_UP, 10.toFloat(), 10.toFloat())
        player.openInventory(gui.make())
        return true
    }

}