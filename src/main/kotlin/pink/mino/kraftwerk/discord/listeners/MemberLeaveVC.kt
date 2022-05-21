package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MemberLeaveVC : ListenerAdapter() {

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val vc: AudioChannel = event.channelLeft

        if (!vc.name.contains("'s Channel")) {
            return
        }

        if (vc.members.size == 0) {
            vc.delete().queue()
        }
    }

}