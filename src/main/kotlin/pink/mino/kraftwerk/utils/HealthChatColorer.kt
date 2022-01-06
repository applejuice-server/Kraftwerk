package pink.mino.kraftwerk.utils

class HealthChatColorer {
    companion object {
        fun returnHealth(health: Double): String {
            var c = ""
            when {
                health > 90 -> {
                    c = "§2"
                }
                health > 80 -> {
                    c = "§a"
                }
                health > 75 -> {
                    c = "§6"
                }
                health > 50 -> {
                    c = "§e"
                }
                health > 35 -> {
                    c = "§c"
                }
                health.equals(0) -> {
                    c = "§8"
                }
            }
            return c
        }
    }

}