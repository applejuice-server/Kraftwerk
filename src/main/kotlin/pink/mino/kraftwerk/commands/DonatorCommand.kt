package pink.mino.kraftwerk.commands

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker

class DonatorCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return true
        }
        val perks = PerkChecker.checkPerks(sender)
        if (perks.isEmpty()) {
            pink.mino.kraftwerk.utils.Chat.sendMessage(sender, "&cYou do not have any perks, buy some on the store at &eapplejuice.tebex.io&7!")
            return true
        }
        val gui = GuiBuilder().rows(perks.size / 9 + 1).name("&2&lDonator Perks")
        for ((index, perk) in perks.withIndex()) {
            when (perk) {
                Perk.BODY_SPEC -> {
                    val item = ItemBuilder(Material.IRON_CHESTPLATE)
                        .name("&2&lBody Spectating")
                        .addLore("&7Dying in a UHC automatically will make you spectate a random teammate.")
                        .addLore("&8Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.EMOTES -> {
                    val item = ItemBuilder(Material.REDSTONE)
                        .name("&2&lEmotes")
                        .addLore("&7You are provided some chat emotes to spice up chat!")
                        .addLore(" ")
                        .addLore(" &7&lEmotes:")
                        .addLore(" &7 - &e:star:")
                        .addLore(" &7 - &c<3")
                        .addLore(" &7 - &e:shrug:")
                        .addLore(" &7 - &eo/")
                        .addLore("&8Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.MOB_EGGS -> {
                    val item = ItemBuilder(Material.EGG)
                        .name("&2&lMob Eggs")
                        .addLore("&7You are provided with an egg that spawns a random mob when tossed, how fun!")
                        .addLore("&8Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.PROJECTILE_PARTICLES -> {
                    val item = ItemBuilder(Material.ARROW)
                        .name("&2&lMob Eggs")
                        .addLore("&7You may customize the arrow particles that are spawned when you shoot a bow!")
                        .addLore("&8Use /particles to see the available particles!")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.RIDE_PLAYERS -> {
                    val item = ItemBuilder(Material.SKULL_ITEM)
                        .name("&2&lRide Players")
                        .addLore("&7You may ride a player by right-clicking them!")
                        .addLore("&8Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.STATS_RESET -> {
                    val item = ItemBuilder(Material.NAME_TAG)
                        .name("&2&lStats Reset")
                        .addLore("&7You may reset your stats in the stats interface.")
                        .addLore("&8Reset your stats in /stats")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.TEAM_COLORS -> {
                    val item = ItemBuilder(Material.WOOL)
                        .name("&2&lTeam Colors")
                        .addLore("&7You can choose your team's color.")
                        .addLore("&8Change your team color using /team color")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.TOGGLE_PICKUPS -> {
                    val item = ItemBuilder(Material.LAPIS_BLOCK)
                        .name("&2&lToggle Pickups")
                        .addLore("&7You may toggle your pickups for certain ores!")
                        .addLore("&8Toggle pickups using &c/redstone&8 & or &1/lapis&8!")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
            }
        }
        sender.openInventory(gui.make())
        return true
    }
}