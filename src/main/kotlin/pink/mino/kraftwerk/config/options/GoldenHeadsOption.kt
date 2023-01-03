package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.config.ConfigOption
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.BlockRotation
import pink.mino.kraftwerk.utils.ItemBuilder

class GoldenHeadsOption : ConfigOption(
  "Golden Heads",
  "Players drop a head when they die, you can craft golden heads (apples) using them.",
  "options",
  "goldenheads",
  Material.GOLDEN_CARROT
) {

  @EventHandler
  fun onPlayerConsume(e: PlayerItemConsumeEvent) {
    if (!enabled) {
      return
    }
    val player = e.player
    if (e.item.type === Material.GOLDEN_APPLE && e.item.itemMeta.displayName != null && e.item.itemMeta.displayName == "§6Golden Head") {
      player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 1))
    }
  }
  @EventHandler
  fun onPlayerDeath(e: PlayerDeathEvent) {
    if (!enabled) {
      return
    }
    val player = e.entity
    if (player.world.name != "Arena") {
      if (ScenarioHandler.getScenario("goldenretriever")!!.enabled || ScenarioHandler.getScenario("barebones")!!.enabled) {
        return
      }
      if (ScenarioHandler.getScenario("graverobbers")!!.enabled || ScenarioHandler.getScenario("timebomb")!!.enabled) {
        val skull = ItemBuilder(Material.SKULL_ITEM)
          .toSkull()
          .setOwner(player.name)
          .make()
        e.drops.add(skull)
        return
      }
      player.location.block.type = Material.NETHER_FENCE
      player.location.add(0.0, 1.0, 0.0).block.type = Material.SKULL

      val skull: Skull = player.location.add(0.0, 1.0, 0.0).block.state as Skull
      skull.skullType = SkullType.PLAYER
      skull.owner = player.name
      skull.rotation = BlockRotation.getBlockFaceDirection(player.location)
      skull.update()

      val b: Block = player.location.add(0.0, 1.0, 0.0).block
      b.setData(0x1.toByte(), true)
    }
    if (ScenarioHandler.getScenario("champions")!!.enabled) {
      val skull = ItemBuilder(Material.SKULL_ITEM)
        .name("${player.name}'s Head")
        .addLore("&7Gives you beneficial effects...")
        .make()
      e.drops.add(skull)
    }
  }
}