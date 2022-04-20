package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder

class HotbarCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you aren't technically a player.")
            return false
        }
        if (GameState.currentState == GameState.LOBBY) {
            if (sender.world.name == "Arena") {
                sender.sendMessage(Chat.colored("&8[&4Arena&8]&7 &7You can't use this command in the Arena."))
                return false
            }
            val gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Hotbar Editor"))
            gui.item(0, ItemStack(Material.DIAMOND_SWORD))
            gui.item(1, ItemStack(Material.FISHING_ROD))
            gui.item(2, ItemStack(Material.BOW))
            gui.item(3, ItemStack(Material.COBBLESTONE, 64))
            gui.item(4, ItemStack(Material.WATER_BUCKET))
            gui.item(5, ItemStack(Material.LAVA_BUCKET))
            gui.item(6, ItemStack(Material.GOLDEN_CARROT, 16))
            gui.item(7, ItemStack(Material.GOLDEN_APPLE, 5))
            val goldenHeads = ItemBuilder(Material.GOLDEN_APPLE)
                .name("&6Golden Head")
                .setAmount(3)
                .make()
            gui.item(8, goldenHeads)
            Chat.sendMessage(sender, "${Chat.prefix} Opening hotbar editor...")
            sender.openInventory(gui.make())
        } else {
            Chat.sendMessage(sender, "&cYou can't use this command at the moment.")
        }
        return true
    }
}