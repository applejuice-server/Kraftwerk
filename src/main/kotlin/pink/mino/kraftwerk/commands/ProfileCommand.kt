package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import kotlin.math.floor

class ProfileCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            return false
        }
        val gui = GuiBuilder().rows(3).name("&4&lYour Profile").owner(sender)
        val settings = ItemBuilder(Material.REDSTONE_COMPARATOR)
            .name(" &4&lSettings")
            .addLore(" ")
            .addLore(" &7Click to customize your settings. ")
            .addLore(" ")
            .make()
        val stats = ItemBuilder(Material.DIAMOND)
            .name(" &4&lStatistics")
            .addLore(" ")
            .addLore(" &7Click to view your player stats. ")
            .addLore(" ")
            .make()
        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!
        val xp = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.xp
        val xpNeeded = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.xpNeeded
        val level = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.level
        val progress: Double = if (floor((xp / xpNeeded) * 100) > 0) {
            floor((xp / xpNeeded) * 100)
        } else {
            0.0
        }
        val misc = ItemBuilder(Material.NETHER_STAR)
            .name(" &4&lMisc.")
            .addLore(" ")
            .addLore(" &7Your Level ${Chat.dash} &a${level} &8(&a${progress.toInt()}%&8)")
            .addLore(" &7Chat Mode ${Chat.dash} &f${profile.chatMode}")
            .addLore(" &7Coins ${Chat.dash} &6⚜ ${floor(profile.coins)}")
            .addLore(" ")
            .make()
        gui.item(12, misc).onClick runnable@ {
            it.isCancelled = true
        }
        gui.item(11, settings).onClick runnable@ { inventoryClickEvent ->
            val player = inventoryClickEvent.whoClicked as Player
            val gui = GuiBuilder().rows(1).name("&4&lPlayer Settings").owner(player)
            val disableRedstonePickup = ItemBuilder(Material.REDSTONE)
            if (profile.disableRedstonePickup) {
                disableRedstonePickup.name("&a&lDisable Redstone Pickup")
                    .addLore("&7Currently ${Chat.dash} &aEnabled")
                    .addLore("&8&oClick to toggle!")
            } else {
                disableRedstonePickup.name("&c&lDisable Redstone Pickup")
                    .addLore("&7Currently ${Chat.dash} &cDisabled")
                    .addLore("&8&oClick to toggle!")
            }
            val disableLapisPickup = ItemBuilder(Material.INK_SACK)
                .setDurability(4)
            if (profile.disableLapisPickup) {
                disableLapisPickup.name("&a&lDisable Lapis Pickup")
                    .addLore("&7Currently ${Chat.dash} &aEnabled")
                    .addLore("&8&oClick to toggle!")
            } else {
                disableLapisPickup.name("&c&lDisable Lapis Pickup")
                    .addLore("&7Currently ${Chat.dash} &cDisabled")
                    .addLore("&8&oClick to toggle!")
            }
            val projectileMessages = ItemBuilder(Material.ARROW)
                .name("&e&lProjectile Messages")
                .addLore("&7Currently ${Chat.dash} &f${profile.projectileMessages}")
                .addLore("&8&oClick to toggle!")
                .make()
            val borderPreference = ItemBuilder(Material.BEDROCK)
                .name("&e&lBorder Preference")
                .addLore("&7Currently ${Chat.dash} &f${profile.borderPreference}")
                .addLore("&8&oClick to toggle!")
                .make()
            val deathMessageOnScreen = ItemBuilder(Material.BOOK_AND_QUILL)
            if (profile.deathMessageOnScreen) {
                deathMessageOnScreen.name("&a&lDeath Message on Screen")
                    .addLore("&7Currently ${Chat.dash} &aEnabled")
                    .addLore("&8&oClick to toggle!")
            } else {
                deathMessageOnScreen.name("&c&lDeath Message on Screen")
                    .addLore("&7Currently ${Chat.dash} &cDisabled")
                    .addLore("&8&oClick to toggle!")
            }
            gui.item(3, borderPreference).onClick runnable@ {
                if (profile.borderPreference == "RADIUS") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.borderPreference = "DIAMETER"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to &8'&eDIAMETER&8'&7.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &fDIAMETER"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else if (profile.borderPreference == "DIAMETER") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.borderPreference = "RADIUS"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to &8'&eRADIUS&8'&7.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &fRADIUS"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(4, deathMessageOnScreen.make()).onClick runnable@ {
                if (profile.deathMessageOnScreen) {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.deathMessageOnScreen = false
                    Chat.sendMessage(player, "${Chat.prefix} &cDisabled&l death message on screen.")
                    val meta = deathMessageOnScreen.meta
                    meta.displayName = Chat.colored("&c&lDeath Message on Screen")
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &cDisabled"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.deathMessageOnScreen = true
                    Chat.sendMessage(player, "${Chat.prefix} &aEnabled&l death message on screen.")
                    val meta = deathMessageOnScreen.meta
                    meta.displayName = Chat.colored("&a&lDeath Message on Screen")
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &aEnabled"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(2, projectileMessages).onClick runnable@ {
                if (profile.projectileMessages == "CHAT") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.projectileMessages = "SUBTITLE"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to &8'&eSUBTITLE&8'&7.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &fSUBTITLE"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else if (profile.projectileMessages == "SUBTITLE") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.projectileMessages = "CHAT"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to &8'&eCHAT&8'&7.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &fCHAT"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(0, disableRedstonePickup.make()).onClick runnable@ {
                if (!PerkChecker.checkPerks(player).contains(Perk.TOGGLE_PICKUPS)) {
                    Chat.sendMessage(player, "&cThis setting is locked to &6Gold&c players. &cBuy it at &ehttps://applejuice.tebex.io&c.")
                    return@runnable
                }
                if (profile.disableRedstonePickup) {
                    profile.disableRedstonePickup = false
                    val meta = disableRedstonePickup.meta
                    meta.displayName = Chat.colored("&c&lDisable Redstone Pickup")
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &cDisabled"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                    Chat.sendMessage(player, "${Chat.prefix} Redstone pickup has been &aenabled&7!")
                } else {
                    profile.disableRedstonePickup = true
                    Chat.sendMessage(player, "${Chat.prefix} Redstone pickup has been &cdisabled&7!")
                    val meta = disableRedstonePickup.meta
                    meta.displayName = Chat.colored("&a&lDisable Redstone Pickup")
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &aEnabled"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.disableRedstonePickup = profile.disableRedstonePickup
            }
            gui.item(1, disableLapisPickup.make()).onClick runnable@ {
                if (!PerkChecker.checkPerks(player).contains(Perk.TOGGLE_PICKUPS)) {
                    Chat.sendMessage(player, "&cThis setting is locked to &6Gold&c players. &cBuy it at &ehttps://applejuice.tebex.io&c.")
                    return@runnable
                }
                if (profile.disableLapisPickup) {
                    profile.disableLapisPickup = false
                    Chat.sendMessage(player, "${Chat.prefix} Lapis pickup has been &aenabled&7!")
                    val meta = disableLapisPickup.meta
                    meta.displayName = Chat.colored("&c&lDisable Lapis Pickup")
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &cDisabled"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else {
                    profile.disableLapisPickup = true
                    Chat.sendMessage(player, "${Chat.prefix} Lapis pickup has been &cdisabled&7!")
                    val meta = disableLapisPickup.meta
                    meta.displayName = Chat.colored("&a&lDisable Lapis Pickup")
                    meta.lore = listOf(
                        Chat.colored("&7Currently ${Chat.dash} &aEnabled"),
                        Chat.colored("&8&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.disableLapisPickup = profile.disableLapisPickup
            }
            val back = ItemBuilder(Material.BARRIER)
                .name("&cBack")
                .addLore("Go back to the previous menu.")
                .make()
            gui.item(5, back).onClick runnable@ {
                Bukkit.dispatchCommand(sender, "profile")
            }
            sender.openInventory(gui.make())
        }
        val display = ItemBuilder(Material.REDSTONE)
            .name(" &4&lDisplay")
            .addLore(" ")
            .addLore(" &7Customize certain cosmetic settings for yourself. ")
            .addLore(" ")
            .make()
        gui.item(14, stats).onClick runnable@ {
            Bukkit.dispatchCommand(it.whoClicked as Player, "stats")
        }
        gui.item(15, display).onClick runnable@ {
            val gui = GuiBuilder().rows(3).name("&4&lDisplay Settings").owner(sender)
            val tags = ItemBuilder(Material.NAME_TAG)
                .name(" &4&lTags")
                .addLore("&7Grant yourself suffixes at the end of your name!")
                .make()
            val arenaBlocks = ItemBuilder(Material.COBBLESTONE)
                .name(" &4&lArena Blocks")
                .addLore("&7Customize the blocks given in your arena kit!")
                .make()
            gui.item(12, tags).onClick runnable@ {
                val gui = GuiBuilder().rows(3).name("&4&lTags").owner(sender)
                val profile = Kraftwerk.instance.profileHandler.getProfile(sender.uniqueId)!!
                if (profile.unlockedTags.size == 0) {
                    Chat.sendMessage(sender, "&cYou have no tags unlocked, buy some at the store at &ehttps://applejuice.tebex.io&c!")
                    return@runnable
                }
                var index = 0
                for (v in profile!!.unlockedTags) {
                    index++
                    val tag = Tags.valueOf(v.uppercase())
                    val display = ItemBuilder(tag.item)
                        .name(v.lowercase().replaceFirstChar { it.uppercase() })
                        .addLore("&7Applies ${tag.display}&7 at the end of your name.")
                        .make()
                    gui.item(index, display).onClick runnable@ {
                        profile.selectedTag = v.uppercase()
                        Kraftwerk.instance.profileHandler.saveProfile(profile)
                        Chat.sendMessage(sender, "${Chat.prefix} Your tag has been set to ${tag.display}&7!")
                    }
                }
                index++
                val none = ItemBuilder(Material.BARRIER)
                    .name("&7None!")
                    .addLore("&7Removes the currently applied tag.")
                    .make()
                gui.item(index, none).onClick runnable@ {
                    profile.selectedTag = null
                    Kraftwerk.instance.profileHandler.saveProfile(profile)
                    Chat.sendMessage(sender, "${Chat.prefix} Removed your currently applied tag!")
                }
                sender.openInventory(gui.make())
            }
            gui.item(14, arenaBlocks).onClick runnable@ {

            }
            sender.openInventory(gui.make())
        }
        Chat.sendMessage(sender, "${Chat.prefix} Opening your player profile...")
        sender.openInventory(gui.make())
        return true
    }

}