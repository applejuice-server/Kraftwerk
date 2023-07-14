package pink.mino.kraftwerk.discord.listeners

import me.lucko.helper.Schedulers
import me.lucko.helper.utils.Log
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import org.bukkit.Bukkit
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import java.awt.Color

class SlashCommand : ListenerAdapter() {
    override fun onSlashCommand(event: SlashCommandEvent) {
        if (event.guild == null) return
        val member = event.member
        val guild = event.guild
        Log.info("Handling command ${event.name} for ${event.member!!.user.name}#${event.member!!.user.discriminator} in the Discord server.")
        when (event.name) {
            "online" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Online players", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                embed.setDescription("There are currently **${Bukkit.getServer().onlinePlayers.size} players** online.")
                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
            }
            "ip" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — IP Address", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                embed.setDescription("The IP address to the server is :beverage_box: `applejuice.games`.")
                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
            }
            "togglealerts" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Toggle alerts", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                if (member!!.roles.contains(event.jda.getRoleById(1129405126889713692))) {
                    if (guild != null) {
                        guild.getRoleById(1129405126889713692)?.let { guild.removeRoleFromMember(member.id, it) }?.queue()
                    }
                    embed.setDescription("You have disabled matchpost notifications.")
                } else {
                    if (guild != null) {
                        guild.getRoleById(1129405126889713692)?.let { guild.addRoleToMember(member.id, it) }?.queue()
                    }
                    embed.setDescription("You have enabled matchpost notifications.")
                }
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
            "wl" -> {
                val player = event.getOption("ign")!!.asString
                val target = Bukkit.getOfflinePlayer(player)
                if (target == null) event.reply("Invalid player!").setEphemeral(true).queue()
                if (SettingsFeature.instance.data!!.getBoolean("whitelist.requests")) {
                    Schedulers.sync().run runnable@ {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl add $player")
                    }
                    event.reply("**${MarkdownSanitizer.escape(player)}** has been whitelisted on the server, connect using `applejuice.games`.").queue()
                } else {
                    event.reply("Sorry, but whitelists are not available at this time!").setEphemeral(true).queue()
                }
            }
            "scenarios" -> {
                val list = ArrayList<String>()
                for (scenario in ScenarioHandler.scenarios) {
                    list.add(scenario.name)
                }
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Scenario List", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                embed.setDescription("Scenarios: `${list.joinToString(", ")}`")
                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
            }
            else -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue()
        }
    }
}