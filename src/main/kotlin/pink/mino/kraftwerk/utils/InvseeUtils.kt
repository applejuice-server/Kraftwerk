package pink.mino.kraftwerk.utils

import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.math.floor

/**
 * @author mrcsm
 * 2022-06-16
 */
class InvseeUtils {

    fun getPotionName(type: PotionEffectType): String {
        return when (type.name.lowercase(Locale.getDefault())) {
            "speed" -> "Speed"
            "slow" -> "Slowness"
            "fast_digging" -> "Haste"
            "slow_digging" -> "Mining Fatigue"
            "increase_damage" -> "Strength"
            "heal" -> "Instant Health"
            "harm" -> "Instant Damage"
            "jump" -> "Jump Boost"
            "confusion" -> "Nausea"
            "regeneration" -> "Regeneration"
            "damage_resistance" -> "Resistance"
            "fire_resistance" -> "Fire Resistance"
            "water_breathing" -> "Water Breathing"
            "invisibility" -> "Invisibility"
            "blindness" -> "Blindness"
            "night_vision" -> "Night Vision"
            "hunger" -> "Hunger"
            "weakness" -> "Weakness"
            "poison" -> "Poison"
            "wither" -> "Wither"
            "health_boost" -> "Health Boost"
            "absorption" -> "Absorption"
            "saturation" -> "Saturation"
            else -> "?"
        }
    }

    private val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    private val romanLiterals = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")

    fun integerToRoman(number: Int): String {
        var number = number
        val s = StringBuilder()
        for (i in values.indices) {
            while (number >= values[i]) {
                number -= values[i]
                s.append(romanLiterals[i])
            }
        }
        return s.toString()
    }

    fun potionDurationToString(ticks: Int): String {
        var ticks = ticks
        val hours = floor((ticks / 3600).toDouble()).toInt()
        ticks -= hours * 3600
        val minutes = floor((ticks / 60).toDouble()).toInt()
        ticks -= minutes * 60
        val seconds = ticks
        val output = StringBuilder()
        if (ticks > 1638) {
            return "**:**"
        }
        if (hours > 0) {
            return "**:**"
        }
        if (minutes > 0) {
            if (minutes < 10) {
                output.append("0$minutes").append(":")
            } else {
                output.append(minutes).append(":")
            }
        }
        if (minutes == 0) {
            output.append("00:")
        }
        if (seconds < 10) {
            output.append("0$seconds")
        } else {
            output.append(seconds)
        }
        return output.toString()
    }

}