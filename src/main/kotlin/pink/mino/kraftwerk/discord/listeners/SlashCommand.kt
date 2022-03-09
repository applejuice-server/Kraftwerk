package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import pink.mino.kraftwerk.features.SettingsFeature
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
                embed.setDescription("The IP address to the server is `104.219.232.42:25575` or :beverage_box: `applejuice.bar`.")
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
            "wl" -> {
                val player = event.getOption("ign")!!.asString
                val target = Bukkit.getOfflinePlayer(player)
                if (target == null) event.reply("Invalid player!").setEphemeral(true).queue()
                if (SettingsFeature.instance.data!!.getBoolean("whitelist.requests")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl add ${player}")
                    event.reply("**${player}** has been whitelisted on the server, connect using `applejuice.bar` or `104.219.232.42:25575`.").queue()
                } else {
                    event.reply("Sorry, but whitelists are not available at this time!").setEphemeral(true).queue()
                }
            }
            else -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue()
        }
    }
}