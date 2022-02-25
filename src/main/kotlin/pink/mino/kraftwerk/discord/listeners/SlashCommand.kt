package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import pink.mino.kraftwerk.utils.Stats
import java.awt.Color

class SlashCommand : ListenerAdapter() {
    override fun onSlashCommand(event: SlashCommandEvent) {
        if (event.guild == null) return
        val member = event.member
        val guild = event.guild
        print("Handling command ${event.name} for ${event.member!!.user.name}#${event.member!!.user.discriminator} in the Discord server.")
        when (event.name) {
            "online" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Online players", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                embed.setDescription("There are currently **${Bukkit.getServer().onlinePlayers.size} players** online.")
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
            "ip" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — IP Address", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                embed.setDescription("The IP address to the server is `78.108.218.25:25572` or :beverage_box: `applejuice.bar`.")
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
            "togglealerts" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Toggle alerts", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                if (member!!.roles.contains(event.jda.getRoleById(793406242013839381))) {
                    if (guild != null) {
                        guild.getRoleById(793406242013839381)?.let { guild.removeRoleFromMember(member.id, it) }?.queue()
                    }
                    embed.setDescription("You will now not be notified of when games happen.")
                } else {
                    if (guild != null) {
                        guild.getRoleById(793406242013839381)?.let { guild.addRoleToMember(member.id, it) }?.queue()
                    }
                    embed.setDescription("You will now be notified of when games happen.")
                }
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
            "stats" -> {
                val player = event.getOption("player")!!.asString
                val target = Bukkit.getOfflinePlayer(player)
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Stats", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                if (target == null) {
                    embed.setDescription("That's an invalid player, please retry with a valid player.")
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                    return
                }
                val oresList = listOf(
                    "Diamonds Mined » **${Stats.getDiamondsMined(target)}**",
                    "Gold Mined » **${Stats.getGoldMined(target)}**",
                    "Iron Mined » **${Stats.getIronMined(target)}**",
                )
                val generalList = listOf(
                    "Kills » **${Stats.getKills(target)}**",
                    "Deaths » **${Stats.getDeaths(target)}**",
                    "Wins » **${Stats.getWins(target)}**",
                    "Games Played » **${Stats.getGamesPlayed(target)}**"
                )
                embed.addField("General", generalList.joinToString("\n"), false)
                embed.addField("Ores", oresList.joinToString("\n"), false)
                embed.setThumbnail("https://visage.surgeplay.com/bust/${target.uniqueId}")
                event.replyEmbeds(embed.build()).queue()
            }
            else -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue()
        }
    }
}