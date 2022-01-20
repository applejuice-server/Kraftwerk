package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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