package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder

class ScenarioManagerCommand : CommandExecutor {

    fun between(variable: Int, minValueInclusive: Int, maxValueInclusive: Int): Boolean {
        return variable in minValueInclusive..maxValueInclusive
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you aren't technically a player.")
            return false
        }
        if (!sender.hasPermission("uhc.staff.scenarios")) {
            Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
            return false
        }
        val page = if (args.isEmpty()) {
            0
        } else {
            if (args[0].toIntOrNull() == null) {
                Chat.sendMessage(sender, "&cInvalid page number!")
                return false
            }
            args[0].toInt()
        }
        val gui = GuiBuilder().rows(4).name(Chat.colored("&4Scenario Manager"))
        var i = 0
        for ((index, scenario) in ScenarioHandler.getScenarios().withIndex()) {
            if (page == 0) {
                if (index < 27) {
                    val item = ItemStack(scenario.material)
                    var meta = item.itemMeta
                    var color: String = if (scenario.enabled) "&a"
                    else "&c"
                    meta.displayName = Chat.colored("${color}${scenario.name}")
                    meta.lore = listOf(
                        Chat.colored("&7${scenario.description}")
                    )
                    item.itemMeta = meta
                    gui.item(i, item).onClick runnable@ {
                        ScenarioHandler.getScenario(scenario.id)?.toggle()
                        color = if (scenario.enabled) "&a"
                        else "&c"
                        meta = it.currentItem.itemMeta
                        meta.displayName = Chat.colored("${color}${scenario.name}")
                        it.currentItem.itemMeta = meta
                        it.isCancelled = true
                    }
                    i++
                }
            } else {
                if (between(index, ((page - 1) * 27), ((page - 1) * 27) + 26)) {
                    val item = ItemStack(scenario.material)
                    var meta = item.itemMeta
                    var color: String = if (scenario.enabled) "&a"
                    else "&c"
                    meta.displayName = Chat.colored("${color}${scenario.name}")
                    meta.lore = listOf(
                        Chat.colored("&7${scenario.description}")
                    )
                    item.itemMeta = meta
                    gui.item(i, item).onClick runnable@ {
                        ScenarioHandler.getScenario(scenario.id)?.toggle()
                        color = if (scenario.enabled) "&a"
                        else "&c"
                        meta = it.currentItem.itemMeta
                        meta.displayName = Chat.colored("${color}${scenario.name}")
                        it.currentItem.itemMeta = meta
                        it.isCancelled = true
                    }
                    i++
                }
            }
        }
        val next = ItemBuilder(Material.ARROW)
            .name("&eNext")
            .addLore("&7Go to the next page.")
            .make()
        val back = ItemBuilder(Material.ARROW)
            .name("&eBack")
            .addLore("&7Go to the previous page.")
            .make()
        if (page > 0) {
            gui.item(32, next).onClick runnable@ {
                it.isCancelled = true
                sender.closeInventory()
                Bukkit.dispatchCommand(sender, "sm ${page + 1}")
            }
            gui.item(30, back).onClick runnable@ {
                it.isCancelled = true
                sender.closeInventory()
                Bukkit.dispatchCommand(sender, "sm ${page - 1}")
            }
        } else {
            gui.item(31, next).onClick runnable@ {
                it.isCancelled = true
                sender.closeInventory()
                if (page == 0) Bukkit.dispatchCommand(sender, "sm ${page + 2}")
                else Bukkit.dispatchCommand(sender, "sm ${page + 1}")
            }
        }
        sender.openInventory(gui.make())
        sender.sendMessage(Chat.colored("${Chat.prefix} Opening scenario manager menu..."))
        sender.playSound(sender.location, Sound.CLICK, 10.toFloat(), 10.toFloat())
        return true
    }
}