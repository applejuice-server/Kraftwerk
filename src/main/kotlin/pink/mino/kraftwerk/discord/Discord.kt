package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import pink.mino.kraftwerk.features.SettingsFeature
import javax.security.auth.login.LoginException


class Discord : ListenerAdapter() {
    companion object {
        var instance: JDA? = null

        fun main() {
            if (SettingsFeature.instance.data!!.getString("discord.token") == null) {
                throw(LoginException("No token found in config.yml"))
            }
            val jda = JDABuilder.createLight(
                SettingsFeature.instance.data!!.getString("discord.token"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES
            )
                .enableCache(CacheFlag.VOICE_STATE)
                .build()
            instance = jda
        }
    }
}