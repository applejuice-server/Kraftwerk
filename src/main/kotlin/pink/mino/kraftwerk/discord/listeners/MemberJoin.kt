package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pink.mino.kraftwerk.discord.Discord

class MemberJoin : ListenerAdapter() {
    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        val member = e.member
        val guild = e.guild
        Discord.instance!!.getTextChannelById(1129308683067736205)!!.sendMessage("Welcome ${member.asMention} to the :beverage_box: **applejuice Discord**!").queue()
        guild.getRoleById(1129405126889713692)?.let { guild.addRoleToMember(member.id, it).queue() }
    }
}