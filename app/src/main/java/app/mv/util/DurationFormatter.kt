package app.mv.util

import java.util.concurrent.TimeUnit

 fun formatDuration(duration: Long?): String {
    if (duration == null || duration <= 0) return ""

    val hours = TimeUnit.MILLISECONDS.toHours(duration)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
