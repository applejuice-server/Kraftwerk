package pink.mino.kraftwerk.utils

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.Kraftwerk

enum class Tags(
    val display: String,
    val item: Material
) {
    FLOWER("&8[&d✿&8]&r", Material.YELLOW_FLOWER),
    SMILEY("&b(◕‿◕)&r", Material.POTATO_ITEM),
    HEART("&8[&c❤&8]&r", Material.GOLDEN_APPLE),

    MUSICAL("&8[&b♫&8]&r", Material.NOTE_BLOCK),
    STAR("&8[&e✯&8]&r", Material.GOLD_BLOCK),
    YIN_YANG("&8[&f☯&8]&r", Material.ENDER_PEARL),
    PEACE("&8[&d☮&8]&r", Material.BREWING_STAND_ITEM)
}

class GrantTagCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender.name != "minota") {
            if (sender is Player) {
                Chat.sendMessage(sender, "&cThis command can only be executed by console.")
                return false
            }
        }
        val player = Bukkit.getOfflinePlayer(args[0])
        val profile = Kraftwerk.instance.profileHandler.getProfile(player.uniqueId)!!
        profile.unlockedTags.add(args[1].uppercase())
        Kraftwerk.instance.profileHandler.saveProfile(profile)
        if (player.isOnline) {
            Chat.sendMessage(player as Player, "${Chat.prefix} You've been granted the &e${args[1].uppercase()} ${Tags.valueOf(args[1].uppercase()).display}&7 tag.")
        }
        return true
    }
}
