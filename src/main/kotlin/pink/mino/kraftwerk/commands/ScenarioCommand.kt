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

class ScenarioCommand : CommandExecutor {
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
        val gui = GuiBuilder().rows(3).name(Chat.colored("&8Active Scenarios"))
        for ((index, scenario) in ScenarioHandler.getActiveScenarios().withIndex()) {
            val item = ItemStack(scenario.material)
            val meta = item.itemMeta
            val color: String = if (scenario.enabled) "&a"
            else "&c"
            meta.displayName = Chat.colored("${color}${scenario.name}")
            meta.lore = Chat.scenarioTextWrap(Chat.colored("&7${scenario.description}"), 40)
            item.itemMeta = meta
            gui.item(index, item).onClick runnable@ {
                it.isCancelled = true
            }
        }
        sender.openInventory(gui.make())
        sender.sendMessage(Chat.colored("${Chat.prefix} Opening active scenarios menu..."))
        sender.playSound(sender.location, Sound.CLICK, 10.toFloat(), 10.toFloat())
        return true
    }

}