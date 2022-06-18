package pink.mino.kraftwerk.utils

import kotlin.math.floor

class MiscUtils {
    companion object {
        fun timeToString(ticks: Long): String {
            var t = ticks
            val hours = floor(t / 3600.toDouble()).toInt()
            t -= hours * 3600
            val minutes = floor(t / 60.toDouble()).toInt()
            t -= minutes * 60
            val seconds = t.toInt()
            val output = StringBuilder()
            if (hours > 0) {
                output.append(hours).append('h')
                if (minutes == 0) {
                    output.append(minutes).append('m')
                }
            }
            if (minutes > 0) {
                output.append(minutes).append('m')
            }
            output.append(seconds).append('s')
            return output.toString()
        }
    }
}