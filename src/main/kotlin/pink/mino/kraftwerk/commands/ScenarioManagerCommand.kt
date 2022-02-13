package pink.mino.kraftwerk.commands

import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder

class ScenarioManagerCommand : CommandExecutor {
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
        val gui = GuiBuilder().rows(3).name(Chat.colored("&4Scenario Manager"))
        for ((index, scenario) in ScenarioHandler.getScenarios().withIndex()) {
            val item = ItemStack(scenario.material)
            var meta = item.itemMeta
            var color: String = if (scenario.enabled) "&a"
            else "&c"
            meta.displayName = Chat.colored("${color}${scenario.name}")
            meta.lore = listOf(
                Chat.colored("&7${scenario.description}")
            )
            item.itemMeta = meta
            gui.item(index, item).onClick runnable@ {
                ScenarioHandler.getScenario(scenario.id)?.toggle()
                color = if (scenario.enabled) "&a"
                else "&c"
                meta = it.currentItem.itemMeta
                meta.displayName = Chat.colored("${color}${scenario.name}")
                it.currentItem.itemMeta = meta
                it.isCancelled = true
            }
        }
        sender.openInventory(gui.make())
        sender.sendMessage(Chat.colored("${Chat.prefix} Opening scenario manager menu..."))
        sender.playSound(sender.location, Sound.CLICK, 10.toFloat(), 10.toFloat())
        return true
    }
}