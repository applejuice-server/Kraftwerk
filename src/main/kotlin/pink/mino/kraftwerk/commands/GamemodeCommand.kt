package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
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
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (SpecFeature.instance.isSpec(sender)) {
            Chat.sendMessage(sender, "&cYou can't use this command while spectating.")
            return false
        }

        if (args.isEmpty()) {
            val player = sender
            when (cmd.name) {
                "gmsp" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSpectator§7.")
                    } else {
                        player.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                    }
                }
                "gmc" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cCreative§7.")
                    } else {
                        player.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                    }
                }
                "gma" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cAdventure§7.")
                    } else {
                        player.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cAdventure§7.")
                    }
                }
                "gms" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSurvival§7.")
                    } else {
                        player.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(player, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                    }
                }
            }
        } else {
            when (args[0]) {
                "s" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSurvival§7.")
                    } else {
                        sender.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                    }
                }
                "0" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSurvival§7.")
                    } else {
                        sender.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                    }
                }
                "survival" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSurvival§7.")
                    } else {
                        sender.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cSurvival§7.")
                    }
                }

                "c" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cCreative§7.")
                    } else {
                        sender.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                    }
                }
                "1" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cCreative§7.")
                    } else {
                        sender.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                    }
                }
                "creative" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cCreative§7.")
                    } else {
                        sender.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cCreative§7.")
                    }
                }

                "a" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cAdventure§7.")
                    } else {
                        sender.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cAdventure§7.")
                    }
                }
                "2" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cAdventure§7.")
                    } else {
                        sender.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cAdventure§7.")
                    }
                }
                "adventure" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cAdventure§7.")
                    } else {
                        sender.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cAdventure§7.")
                    }
                }

                "3" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSpectator§7.")
                    } else {
                        sender.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                    }
                }
                "sp" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSpectator§7.")
                    } else {
                        sender.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                    }
                }
                "spectator" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "&cPlayer not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} &f${sender.name}&7 has set your gamemode to §cSpectator§7.")
                    } else {
                        sender.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(sender, "${Chat.prefix} &7Set your gamemode to §cSpectator§7.")
                    }
                }
            }
        }

        return true
    }

}