package pink.mino.kraftwerk.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.awt.Color
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class EndGameCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "${Chat.prefix} &cYou don't have permission to use this command.")
                return false
            }
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "&cYou can't do this right now.")
            return false
        }
        val winners = SettingsFeature.instance.data!!.getStringList("game.winners")
        if (winners.isEmpty()) {
            Chat.sendMessage(sender, "&cYou have no winners set! You need to set them using /winner <player>!")
            return false
        }
        for (player in Bukkit.getOnlinePlayers()) {
            if (winners.contains(player.name)) {
                player.sendTitle(Chat.colored("&6&lVICTORY!"), Chat.colored("&7Congratulations, you won the game!"))
                if (!ConfigOptionHandler.getOption("statless")!!.enabled) JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.wins++
            } else {
                player.sendTitle(Chat.colored("&c&lGAME OVER!"), Chat.colored("&7The game has concluded!"))
            }
        }
        val host = Bukkit.getOfflinePlayer(SettingsFeature.instance.data!!.getString("game.host"))
        val gameTitle = SettingsFeature.instance.data!!.getString("matchpost.host")
        val embed = EmbedBuilder()
        embed.setColor(Color(255, 61, 61))
        embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
        embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
        embed.addField("Winners", winners.joinToString(", ", "", "", -1, "...") {
            "**$it** [${
                SettingsFeature.instance.data!!.getInt(
                    "game.kills.${Bukkit.getOfflinePlayer(it).name}"
                )
            }]"
        }, false)
        for (team in TeamsFeature.manager.getTeams()) {
            for (player in team.players) {
                team.removePlayer(player)
            }
        }
        embed.addField("Matchpost", "https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}", false)
        Discord.instance!!.getTextChannelById(937811334106583040)!!.sendMessageEmbeds(embed.build()).queue()
        if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
            Discord.instance!!.presence.activity = Activity.playing("na.applejuice.bar")
        } else {
            Discord.instance!!.presence.activity = Activity.playing("eu.applejuice.bar")
        }

        SettingsFeature.instance.data!!.set("game.winners", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.list", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.kills", null)
        SettingsFeature.instance.data!!.set("game.nether", false)
        SettingsFeature.instance.data!!.set("whitelist.requests", false)
        SettingsFeature.instance.data!!.set("whitelist.list", ArrayList<String>())
        SettingsFeature.instance.data!!.set("matchpost.opens", null)
        SettingsFeature.instance.data!!.set("matchpost.host", null)
        SettingsFeature.instance.data!!.set("matchpost.posted", null)
        SettingsFeature.instance.saveData()
        Bukkit.broadcastMessage(Chat.colored(Chat.line))
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) SpecFeature.instance.unspec(player)
            SpawnFeature.instance.send(player)
            Chat.sendCenteredMessage(player, "&c&lGAME OVER!")
            Chat.sendMessage(player, " ")
            Chat.sendCenteredMessage(player, "&7Congratulations to the winners: &f${winners.joinToString(", ")}&7!")
            Chat.sendCenteredMessage(player, "&7The server will restart in &f45 seconds&7.")
        }
        for (world in Bukkit.getWorlds()) {
            world.pvp = true
        }
        val world = Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world"))
        Bukkit.getServer().unloadWorld(world.name, true)
        for (file in Bukkit.getServer().worldContainer.listFiles()!!) {
            if (file.name.lowercase() == world.name.lowercase()) {
                Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach { it.delete() }
                file.delete()
                print("Deleted world file for ${world.name}.")
            }
        }
        SettingsFeature.instance.data!!.set("pregen.world", null)
        SettingsFeature.instance.saveData()
        SettingsFeature.instance.worlds!!.set(world.name, null)
        SettingsFeature.instance.saveWorlds()
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
        }, 900L)
        Bukkit.broadcastMessage(Chat.colored(Chat.line))
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl off")
        val log = File("./logs/latest.log")
        Discord.instance!!.getTextChannelById(955314362019635270)!!.sendMessage("**${gameTitle}**").queue()
        Discord.instance!!.getTextChannelById(955314362019635270)!!.sendFile(log, "game.log").queue()

        return true
    }

}