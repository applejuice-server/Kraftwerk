package pink.mino.kraftwerk.commands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PlayerUtils
import kotlin.math.floor

class NearbyCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (!SpecFeature.instance.getSpecs().contains(sender.name)) {
            Chat.sendMessage(sender, "&cYou aren't a spectator!")
            return false
        }
        val entites = (sender as Player).getNearbyEntities(100.0, 100.0, 100.0)
        val players = ArrayList<Player>()
        for (entity in entites) {
            if (entity.type == EntityType.PLAYER) {
                if (!SpecFeature.instance.isSpec(entity as Player)) players.add(entity)
            }
        }
        Chat.sendMessage(sender, Chat.line)
        Chat.sendCenteredMessage(sender, "${Chat.primaryColor}&lNearby Players")
        if (players.isEmpty()) {
            Chat.sendCenteredMessage(sender, "&7No players nearby!")
        } else {
            for (player in players) {
                val text = TextComponent(Chat.colored("${Chat.primaryColor}${floor(player.location.x)}, ${floor(player.location.y)}, ${floor(player.location.z)} &8- ${Chat.secondaryColor}${PlayerUtils.getPrefix(player)}${player.name} &8(${Chat.primaryColor}${floor(sender.location.distance(player.location))}m&8)"))
                text.clickEvent = ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND,
                    "/tp ${player.name}"
                )
                Chat.sendMessage(sender, "${Chat.primaryColor}${floor(player.location.x)}, ${floor(player.location.y)}, ${floor(player.location.z)} &8- ${Chat.secondaryColor}${PlayerUtils.getPrefix(player)}${player.name} &8(${Chat.primaryColor}${floor(sender.location.distance(player.location))}m&8)")
            }
        }
        Chat.sendMessage(sender, Chat.line)
        return true
    }
}