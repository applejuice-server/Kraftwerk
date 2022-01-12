package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent

class WeatherChange : Listener {
    @EventHandler
    fun onWeatherChange(e: WeatherChangeEvent) {
        e.isCancelled = true
    }
}