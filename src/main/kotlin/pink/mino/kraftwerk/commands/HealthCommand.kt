package pink.mino.kraftwerk.commands

import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HealthChatColorer
import kotlin.math.floor

class HealthCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("parafusion"))) {
            Chat.sendMessage(sender, "&cParafusion is enabled, so you can't use this command.")
            return false
        }
        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage("You can't use this command as you technically aren't a player.")
                return false
            }
            val el: EntityLiving = (sender as CraftPlayer).handle
            val health = floor(sender.health / 2 * 10 + el.absorptionHearts / 2 * 10)
            val color = HealthChatColorer.returnHealth(health)
            Chat.sendMessage(sender,
                "${Chat.prefix} ${Chat.secondaryColor}${sender.displayName}${ChatColor.GRAY} is at ${color}${health}%${ChatColor.GRAY}.")
            return true
        } else {
            val target = Bukkit.getServer().getPlayer(args[0])
            if (target == null) {
                sender.sendMessage("${ChatColor.RED}That player is not online or has never logged onto the server.")
            }
            val el: EntityLiving = (target as CraftPlayer).handle
            val health = floor(target.health / 2 * 10 + el.absorptionHearts / 2 * 10)
            val color = HealthChatColorer.returnHealth(health)
            Chat.sendMessage(sender as Player, "${Chat.prefix} ${Chat.secondaryColor}${target.displayName}${ChatColor.GRAY} is at ${color}${health}%${ChatColor.GRAY}.")
            return true
        }
    }
}