package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature

class MemberJoin : ListenerAdapter() {
    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
            val member = e.member
            val guild = e.guild
            JavaPlugin.getPlugin(Kraftwerk::class.java).discordInstance.getTextChannelById(757930533505335348)!!.sendMessage("Welcome ${member.asMention} to the :beverage_box: **applejuice Discord**!").queue()
            guild.getRoleById(793406242013839381)?.let { guild.addRoleToMember(member.id, it).queue() }
            guild.getRoleById(761262435956162571)?.let { guild.addRoleToMember(member.id, it).queue() }
        }
    }
}