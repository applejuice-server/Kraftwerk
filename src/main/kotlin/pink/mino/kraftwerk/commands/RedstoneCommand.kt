package pink.mino.kraftwerk.commands

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker

class PickupFeature : Listener {
    companion object {
        val instance = PickupFeature()
    }

    @EventHandler
    fun onPlayerPickup(e: PlayerPickupItemEvent) {
        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(e.player.uniqueId)
        if (PerkChecker.checkPerks(e.player).contains(Perk.TOGGLE_PICKUPS)) {
            if (profile!!.disableLapisPickup) {
                if (e.item.itemStack.type == Material.INK_SACK && e.item.itemStack.durability == 4.toShort() && e.player.inventory.containsAtLeast(ItemStack(Material.INK_SACK, 64, 4), 64)) {
                    e.isCancelled = true
                }
            }
            if (profile.disableRedstonePickup) {
                if (e.item.itemStack.type == Material.REDSTONE && e.player.inventory.containsAtLeast(ItemStack(Material.REDSTONE, 64), 64)) {
                    e.isCancelled = true
                }
            }
        }
    }
}

class RedstoneCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return false
        }
        if (!PerkChecker.checkPerks(sender).contains(Perk.TOGGLE_PICKUPS)) {
            Chat.sendMessage(sender, "&cYou must be &6Gold&c to use this command. Buy it at &ehttps://applejuice.tebex.io")
            return false
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.disableRedstonePickup) {
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.disableRedstonePickup = false
            Chat.sendMessage(sender, "${Chat.prefix} &7You have enabled &cRedstone&7 pickups!")
        } else {
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.disableRedstonePickup = true
            Chat.sendMessage(sender, "${Chat.prefix} &7You have disabled &cRedstone&7 pickups!")
        }
        return true
    }
}