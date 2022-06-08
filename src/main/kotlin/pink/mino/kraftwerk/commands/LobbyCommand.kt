package pink.mino.kraftwerk.commands

import com.google.common.io.ByteStreams
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class LobbyCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return true
        }
        val player = sender
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF("Hub")
        Chat.sendMessage(sender, "${Chat.prefix} Sending you to the &cLobby&7...")
        player.sendPluginMessage(JavaPlugin.getPlugin(Kraftwerk::class.java), "BungeeCord", out.toByteArray())
        return true
    }
}