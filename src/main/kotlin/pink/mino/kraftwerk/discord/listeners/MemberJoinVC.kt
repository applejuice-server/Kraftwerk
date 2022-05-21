package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk

class MemberJoinVC : ListenerAdapter() {

    val CREATE_VC_ID: Long = 977594072724283405L
    val BOT_CMD_ID: Long = 937811643818201098L
    val VC_CHANNELS_ID: Long = 977593512059084810L

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val vcJoined = event.channelJoined
        val member = event.member

        if (vcJoined.idLong != CREATE_VC_ID) return

        var vc: AudioChannel? = null
        for (channel in event.guild.voiceChannels) {
            if (channel.name.contains(member.effectiveName + member.user.discriminator)) {
                vc = channel
                break
            }
        }
        if (vc != null) {
            event.guild.moveVoiceMember(member, vc).queue()
            return
        }

        event.guild.createVoiceChannel("${member.nickname}" + "'s Channel", event.guild.getCategoryById(VC_CHANNELS_ID)).queue()

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), runnable@ {
            var newVC: AudioChannel? = null
            for (channel in event.guild.voiceChannels) {
                if (channel.name.contains(member.effectiveName + member.user.discriminator)) {
                    newVC = channel
                    break
                }
            }
            if (newVC != null) {
                event.guild.moveVoiceMember(member, newVC).queue()
            }
            event.guild.getTextChannelById(BOT_CMD_ID)!!.sendMessage(member.asMention + ", your channel has been created. Change the user limit using /limit if you wish.").queue()
        }, 10L)
    }
}