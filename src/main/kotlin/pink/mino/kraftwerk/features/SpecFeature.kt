package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class SpecFeature : Listener {
    companion object {
        val instance = SpecFeature()
    }

    fun spec(p: Player) {
        SpawnFeature.instance.send(p)
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null
        p.gameMode = GameMode.CREATIVE

        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        list.add(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()
        updateVisibility()
        Chat.sendMessage(p, "${Chat.prefix} You are now in spectator mode.")
    }

    fun unspec(p: Player) {
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null
        p.gameMode = GameMode.CREATIVE

        SpawnFeature.instance.send(p)
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        list.add(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()
        updateVisibility()
        Chat.sendMessage(p, "${Chat.prefix} You are no longer in spectator mode.")
    }

    fun updateVisibility() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (getSpecs().contains(player.name)) {
                for (p2 in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(p2.name)) {
                        player.showPlayer(p2)
                    } else {
                        player.showPlayer(p2)
                    }
                }
            } else {
                for (p2 in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(p2.name)) {
                        player.hidePlayer(p2)
                    } else {
                        player.showPlayer(p2)
                    }
                }
            }
        }
    }

    fun getSpecs(): List<String> {
        return SettingsFeature.instance.data!!.getStringList("game.specs")
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (getSpecs().contains(e.player.name)) {
            if (GameState.currentState == GameState.INGAME || GameState.currentState == GameState.WAITING) {
                spec(e.player)
                Chat.sendMessage(e.player, "${Chat.prefix} You are still currently in spectator mode.")
            } else if (GameState.currentState == GameState.LOBBY) {
                unspec(e.player)
                Chat.sendMessage(e.player, "${Chat.prefix} You've been automatically placed out of spectator mode.")
            }
        }
        updateVisibility()
    }

    @EventHandler
    fun onItemPickup(e: PlayerPickupItemEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }
}