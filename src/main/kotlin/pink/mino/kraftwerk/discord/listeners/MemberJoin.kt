package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pink.mino.kraftwerk.discord.Discord

class MemberJoin : ListenerAdapter() {
    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        val member = e.member
        val guild = e.guild
        Discord.instance!!.getTextChannelById(757930533505335348)!!.sendMessage("\"Welcome ${member.asMention} to the :beverage_box: **applejuice Discord**!\"").queue()
        guild.getRoleById(793406242013839381)?.let { guild.addRoleToMember(member.id, it).queue() }
        guild.getRoleById(761262435956162571)?.let { guild.addRoleToMember(member.id, it).queue() }
    }
}