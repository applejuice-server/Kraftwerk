package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import org.bukkit.Bukkit
import java.util.*
import javax.security.auth.login.LoginException


class Main : ListenerAdapter() {
    companion object {
        @Throws(LoginException::class)
        @JvmStatic
        fun main() {
            val jda = JDABuilder.createLight(
                "NzI1MTM0MzcwNzI1MTY3MTg0.XvKUAg.OeFOCpV887CJJbyNUqYBv5uMIeI",
                EnumSet.noneOf(GatewayIntent::class.java)
            ) // slash commands don't need any intents
                .addEventListeners(Main())
                .build()

            val commands = jda.updateCommands()

            commands.addCommands(
                CommandData("online", "View how many players are online on the server.")
            )

            commands.queue()
        }

        fun onSlashCommand(event: SlashCommandEvent) {
            if (event.guild == null) return
            when (event.name) {
                "online" -> {
                    event.reply("There are currently **${Bukkit.getServer().onlinePlayers.size} players** online.")
                }
                else -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue()
            }
        }
    }
}