package pink.mino.kraftwerk.commands

import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat

class GamemodeCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.gamemode")) {
                Chat.sendMessage(sender, "${Chat.prefix} &cYou don't have permission to use this command.")
                return false
            }
        }
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (SpecFeature.instance.isSpec(sender)) {
            Chat.sendMessage(sender, "${Chat.prefix} &cYou can't use this command while spectating.")
            return false
        }

        if (args.isEmpty()) {
            val player = sender
            when (cmd.name) {
                "gmsp" -> {
                    player.gameMode = GameMode.SPECTATOR
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                }
                "gmc" -> {
                    player.gameMode = GameMode.CREATIVE
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                }
                "gma" -> {
                    player.gameMode = GameMode.ADVENTURE
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cAdventure§7.")
                }
                "gms" -> {
                    player.gameMode = GameMode.SURVIVAL
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                }
            }
        } else {
            when (args[0]) {
                "s" -> {
                    val player = sender
                    player.gameMode = GameMode.SURVIVAL
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                }
                "0" -> {
                    val player = sender
                    player.gameMode = GameMode.SURVIVAL
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                }
                "survival" -> {
                    val player = sender
                    player.gameMode = GameMode.SURVIVAL
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                }

                "c" -> {
                    val player = sender
                    player.gameMode = GameMode.CREATIVE
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                }
                "1" -> {
                    val player = sender
                    player.gameMode = GameMode.CREATIVE
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                }
                "creative" -> {
                    val player = sender
                    player.gameMode = GameMode.CREATIVE
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                }

                "a" -> {
                    val player = sender
                    player.gameMode = GameMode.ADVENTURE
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cAdventure§7.")
                }
                "2" -> {
                    val player = sender
                    player.gameMode = GameMode.ADVENTURE
                    Chat.sendMessage(player, "${Chat.prefix} 7Set your gamemode to §cAdventure§7.")
                }
                "adventure" -> {
                    val player = sender
                    player.gameMode = GameMode.ADVENTURE
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cAdventure§7.")
                }

                "3" -> {
                    val player = sender
                    player.gameMode = GameMode.SPECTATOR
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                }
                "sp" -> {
                    val player = sender
                    player.gameMode = GameMode.SPECTATOR
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                }
                "spectator" -> {
                    val player = sender
                    player.gameMode = GameMode.SPECTATOR
                    Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                }
            }
        }

        return true
    }

}