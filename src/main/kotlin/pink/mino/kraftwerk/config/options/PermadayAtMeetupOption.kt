package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class PermadayAtMeetupOption : ConfigOption(
    "Permaday at Meetup",
    "The game will be set to permanently day at meetup.",
    "specials",
    "permadayatmeetup",
    Material.DAYLIGHT_DETECTOR
)