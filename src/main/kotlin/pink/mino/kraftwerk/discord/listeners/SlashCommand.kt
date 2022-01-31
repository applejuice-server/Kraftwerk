package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
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
                embed.setColor(Color.getColor("#ff3d3d"))
                embed.setAuthor("applejuice — Online players", "https://cdn.discordapp.com/attachments/756953696038027425/937807184698429460/beverage-box_1f9c3.png")
                embed.setDescription("There are currently **${Bukkit.getServer().onlinePlayers.size} players** online.")
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
            "ip" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color.getColor("#ff3d3d"))
                embed.setAuthor("applejuice — IP Address", "https://cdn.discordapp.com/attachments/756953696038027425/937807184698429460/beverage-box_1f9c3.png")
                embed.setDescription("The IP address to the server is `78.108.218.25:25572` or :beverage_box: `applejuice.bar`.")
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
            "togglealerts" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color.getColor("#ff3d3d"))
                embed.setAuthor("applejuice — Toggle alerts", "https://cdn.discordapp.com/attachments/756953696038027425/937807184698429460/beverage-box_1f9c3.png")
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
            else -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue()
        }
    }
}