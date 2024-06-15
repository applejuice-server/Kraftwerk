package pink.mino.kraftwerk.discord.listeners

import me.lucko.helper.utils.Log
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.utils.Chat

class MemberJoin : ListenerAdapter() {
    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        val member = e.member
        val guild = e.guild
        try {
            if (Kraftwerk.instance.welcomeChannelId != null) {
                Discord.instance!!.getTextChannelById(Kraftwerk.instance.welcomeChannelId!!)!!.sendMessage("Welcome ${member.asMention} to the **${Chat.serverName} Discord**!").queue()
            } else {
                Log.info("Can't welcome ${member.effectiveName} (${member.id}) to the Discord server because there's no welcome channel ID configured.")
            }
            if (Kraftwerk.instance.alertsRoleId != null) {
                guild.getRoleById(Kraftwerk.instance.alertsRoleId!!)?.let { guild.addRoleToMember(member.id, it).queue() }
            } else {
                Log.info("Can't give ${member.effectiveName} (${member.id}) their Alerts role because there isn't one configured..")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}