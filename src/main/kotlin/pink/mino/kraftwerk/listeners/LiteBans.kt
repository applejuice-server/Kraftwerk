package pink.mino.kraftwerk.listeners

import litebans.api.Entry
import litebans.api.Events
import net.dv8tion.jda.api.EmbedBuilder
import pink.mino.kraftwerk.discord.Discord
import java.awt.Color

class LiteBans : Events.Listener() {
    override fun entryAdded(entry: Entry) {
        val embed = EmbedBuilder()
        embed.setColor(Color(255, 61, 61))
        embed.setThumbnail("https://static.wikia.nocookie.net/minecraft_gamepedia/images/b/b1/Great_Hammer_gear.png/revision/latest?cb=20200602001421")
        var description = ""
        if (entry.type.lowercase() == "ban") {
            if (entry.isPermanent) {
                description = "${entry.executorName} (`${entry.executorUUID}`) permanently banned `${entry.uuid}`"
            }
        } else if (entry.type.lowercase() == "mute") {

        } else if (entry.type.lowercase() == "warn") {

        } else if (entry.type.lowercase() == "kick") {

        }
        Discord.instance!!.getTextChannelById(943681242681987102)!!.sendMessageEmbeds(embed.build()).queue()
    }
}