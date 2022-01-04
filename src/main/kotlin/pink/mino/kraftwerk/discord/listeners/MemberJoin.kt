package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MemberJoin : ListenerAdapter() {
    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        val member = e.member
        val bot = e.jda
        val channel: TextChannel? = bot.getTextChannelById(757930533505335348)
        channel!!.sendMessage("Welcome ${member.asMention} to the **Xestra UHC Discord**!").queue()
    }
}