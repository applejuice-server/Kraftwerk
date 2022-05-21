package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class MemberLeaveVC : ListenerAdapter() {

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val vc: VoiceChannel = event.channelLeft as VoiceChannel
        event.guild.getTextChannelById(756953696038027425)!!.sendMessage("called leave").queue runnable@ {
            it.delete().queueAfter(5, TimeUnit.SECONDS)
        }

        if (!vc.name.contains("'s Channel", true)) {
            return
        }

        if (vc.members.size == 0) {
            vc.delete().queue()
        }
    }

}