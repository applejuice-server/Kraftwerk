package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.Settings

class ConfigCommand : CommandExecutor {

    private fun getOption(option: String): String {
        if(Settings.instance.data!!.getString("game.${option}").isNullOrEmpty()) {
            Settings.instance.data!!.set("game.${option}", false)
            Settings.instance.saveData()
        }
        val op = Settings.instance.data!!.getString("game.${option}").toBoolean()

        return if (op) {
            "Enabled"
        } else {
            "Disabled"
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
            Chat.colored("&7Notch Apples ${Chat.dash} &c${getOption("notch-apples")}"),
            Chat.colored("&7Golden Heads ${Chat.dash} &c${getOption("golden-heads")}"),
            Chat.colored("&7Horses ${Chat.dash} &c${getOption("horses")}"),
            Chat.colored("&7Split Enchants ${Chat.dash} &c${getOption("split-enchants")}"),
            Chat.colored("&7Double Arrows ${Chat.dash} &c${getOption("double-arrows")}"),
            Chat.colored("&7Fire Weapons ${Chat.dash} &c${getOption("fire-weapons")}"),
            Chat.colored("&7Bookshelves ${Chat.dash} &c${getOption("bookshelves")}"),
            Chat.colored(Chat.line),
        )
        options.itemMeta = optionsMeta
        gui.item(10, options).onClick runnable@{
            it.isCancelled = true
        }

        player.openInventory(gui.make())
        return true
    }

}