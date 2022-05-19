package pink.mino.kraftwerk.commands

import me.lucko.helper.utils.Log
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import java.nio.file.Files
import java.nio.file.Path

class RegenArenaCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            Chat.sendMessage(sender, "&cOnly console senders can execute this command.")
            return false
        }
        var world = Bukkit.getWorld("Arena")
        if (world != null) {
            Bukkit.getServer().unloadWorld(world.name, true)
            for (file in Bukkit.getServer().worldContainer.listFiles()!!) {
                if (file.name.lowercase() == world.name.lowercase()) {
                    Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach { it.delete() }
                    file.delete()
                    Log.info("Deleted world file for ${world.name}.")
                }
            }
        }
        val wc = WorldCreator("Arena")
        wc.environment(World.Environment.NORMAL)
        wc.type(WorldType.NORMAL)
        world = wc.createWorld()
        world.difficulty = Difficulty.HARD

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb shape rectangular"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb Arena setcorners 100 100 -100 -100"
        )

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            val border = world.worldBorder
            border.size = 100.toDouble() * 2
            border.setCenter(0.0, 0.0)

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb Arena fill 200"
            )
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb fill confirm"
            )
        }, 5L)

        return true
    }

}