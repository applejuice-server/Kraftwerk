package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import pink.mino.kraftwerk.utils.Chat

class CommandListener : Listener {

    private val blockedCommands = arrayListOf(
        "/me",
        "/minecraft:me",

        "//calculate",
        "//calc",
        "//solve",
        "//eval",
        "//evaluate",

        "/worldedit:/calculate",
        "/worldedit:/calc",
        "/worldedit:/solve",
        "/worldedit:/eval",
        "/worldedit:/evaluate"
    )

    @EventHandler
    fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
        val command = e.message
        for (cmd in blockedCommands) {
            if (cmd in command) {
                e.isCancelled = true
                Chat.sendMessage(e.player, "&cYou don't have permission to perform that command.")
            }
        }
    }
}