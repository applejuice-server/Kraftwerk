package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class PlayerConsumeListener : Listener {
    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (e.item.itemMeta.displayName == Chat.colored("&6Golden Head")) {
            e.player.removePotionEffect(PotionEffectType.REGENERATION)
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 10, 1, true, true))
            }, 1L)
        }
    }
}