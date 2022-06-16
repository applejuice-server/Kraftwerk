package pink.mino.kraftwerk.utils

class HealthChatColorer {
    companion object {
        fun returnHealth(health: Double): String {
            var c = ""
            when {
                health >= 90 -> {
                    c = "§2"
                }
                health >= 80 -> {
                    c = "§a"
                }
                health >= 70 -> {
                    c = "§6"
                }
                health >= 35 -> {
                    c = "§e"
                }
                health >= 0 -> {
                    c = "§c"
                }
                else -> {
                    c = "§8"
                }
            }
            return c
        }
    }
}