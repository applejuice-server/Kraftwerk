package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit

class SlashCommand : ListenerAdapter() {
    override fun onSlashCommand(event: SlashCommandEvent) {
        if (event.guild == null) return
        print("Handling command ${event.name} in the Discord server.")
        when (event.name) {
            "online" -> {
                event.reply("There are currently **${Bukkit.getServer().onlinePlayers.size} players** online.").queue()
            }
            "ip" -> {
                event.reply("yeah not right now sorryu but not really sorry :^)").setEphemeral(true).queue()
            }
            else -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue()
        }
    }
}