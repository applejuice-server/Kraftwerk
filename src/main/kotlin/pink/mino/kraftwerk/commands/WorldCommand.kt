package pink.mino.kraftwerk.commands

import me.lucko.helper.utils.Log
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import java.nio.file.Files
import java.nio.file.Path

class WorldCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.whitelist")) {
                sender.sendMessage(Chat.colored("${Chat.prefix} You do not have permission to use this command."))
                return false
            }
        }
        val player = sender as Player
        if (args.isEmpty()) {
            Chat.sendMessage(player, Chat.line)
            Chat.sendCenteredMessage(player, "&c&lWorld Help")
            Chat.sendMessage(player, "${Chat.prefix} &f/world tp <world> &8- &7Teleport to the provided world.")
            Chat.sendMessage(player, "${Chat.prefix} &f/world list &8- &7List all worlds.")
            Chat.sendMessage(player, "${Chat.prefix} &f/world worlds &8- &7List all UHC worlds.")
            Chat.sendMessage(player, "${Chat.prefix} &f/world delete <world> &8- &7Deletes the provided world.")
            Chat.sendMessage(player, Chat.line)
            return false
        } else if (args[0].lowercase() == "list") {
            Chat.sendMessage(player, Chat.line)
            Chat.sendCenteredMessage(player, "&c&lWorld List")
            for (world in Bukkit.getServer().worlds) {
                when (world.environment) {
                    World.Environment.NORMAL -> {
                        Chat.sendMessage(player, "&8• &a${world.name} &8- &f${world.players.size} players")
                    }
                    World.Environment.NETHER -> {
                        Chat.sendMessage(player, "&8• &c${world.name} &8- &f${world.players.size} players")
                    }
                    World.Environment.THE_END -> {
                        Chat.sendMessage(player, "&8• &e${world.name} &8- &f${world.players.size} players")
                    }
                }
            }
            Chat.sendMessage(player, Chat.line)
        } else if (args[0].lowercase() == "tp") {
            if (args.size == 1) {
                Chat.sendMessage(player, "&cYou need to provide a world.")
                return false
            }
            if (Bukkit.getWorld(args[1]) == null) {
                Chat.sendMessage(player, "&cYou need to provide a valid world.")
                return false
            }
            val world = Bukkit.getWorld(args[1])
            player.teleport(world.spawnLocation)
            Chat.sendMessage(player, "${Chat.prefix} Teleported to &c${world.name}&7's spawn.")
        } else if (args[0].lowercase() == "worlds") {
            val gui = GuiBuilder().name("&c&lWorlds").rows(4)
            for ((index, world) in Bukkit.getServer().worlds.withIndex()) {
                var item: ItemBuilder
                when (world.environment) {
                    World.Environment.NORMAL -> {
                        item = ItemBuilder(Material.GRASS)
                            .name("&a${world.name}")
                            .addLore("&7Contains &f${world.players.size} players&7.")
                            .addLore(" ")
                            .addLore("&8Left Click&7 to teleport to this world.")
                            .addLore("&8Right Click&7 to set this world as the current UHC world.")
                    }
                    World.Environment.NETHER -> {
                        item = ItemBuilder(Material.NETHERRACK)
                            .name("&c${world.name}")
                            .addLore("&7Contains &f${world.players.size} players&7.")
                            .addLore(" ")
                            .addLore("&8Left Click&7 to teleport to this world.")
                            .addLore("&8Right Click&7 to set this world as the current UHC world.")
                    }
                    World.Environment.THE_END -> {
                        item = ItemBuilder(Material.ENDER_STONE)
                            .name("&f${world.name}")
                            .addLore("&7Contains &f${world.players.size} players&7.")
                            .addLore(" ")
                            .addLore("&8Left Click&7 to teleport to this world.")
                            .addLore("&8Right Click&7 to set this world as the current UHC world.")
                    }
                }
                if (SettingsFeature.instance.data!!.getString("pregen.world") == world.name) {
                    item.addEnchantment(Enchantment.DURABILITY, 1)
                    item.name("&a${world.name} &8(&7Current UHC World&8)")
                }
                item.noAttributes()
                gui.item(index, item.make()).onClick {
                    it.isCancelled = true
                    if (it.isLeftClick) {
                        sender.teleport(world.spawnLocation)
                        Chat.sendMessage(sender, "${Chat.prefix} Teleported to &c${world.name}&7's spawn.")
                    } else if (it.isRightClick) {
                        SettingsFeature.instance.data!!.set("pregen.world", world.name)
                        SettingsFeature.instance.data!!.set("pregen.border", world.worldBorder.size / 2)
                        Chat.sendMessage(sender, "${Chat.prefix} Set &c${world.name}&7 as the current UHC world.")
                    }
                    SettingsFeature.instance.saveData()
                }
            }
            sender.openInventory(gui.make())
        } else if (args[0] == "delete") {
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.prefix} &7Usage: &f/world delete <world>")
                return false
            }
            val world = Bukkit.getWorld(args[1])
            if (world == null) {
                Chat.sendMessage(sender, "&cThat world doesn't exist.")
                return false
            }
            if (world.name == "Spawn" || world.name == "Arena") {
                Chat.sendMessage(sender, "&cThat world cannot be deleted.")
                return false
            }

            for (p in world.players) {
                SpawnFeature.instance.send(p)
            }

            Bukkit.getServer().unloadWorld(world.name, true)
            for (file in Bukkit.getServer().worldContainer.listFiles()!!) {
                if (file.name.lowercase() == world.name.lowercase()) {
                    Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach { it.delete() }
                    file.delete()
                    Log.info("Deleted world file for ${world.name}.")
                }
            }
            SettingsFeature.instance.worlds!!.set(world.name, null)
            SettingsFeature.instance.saveWorlds()
            Chat.sendMessage(sender, "${Chat.prefix} &7Successfully deleted &f${world.name}&7.")
        }

        return true
    }

}