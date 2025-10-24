package com.yet.tetris.utils

/**
 * Format milliseconds to MM:SS format
 */
fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = ms / 1000 / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

/**
 * Format timestamp to date string (MM/DD HH:MM)
 */
fun formatDate(timestamp: Long): String {
    val date = js("new Date(timestamp)")
    val month = (date.getMonth() + 1).toString().padStart(2, '0')
    val day = date.getDate().toString().padStart(2, '0')
    val hours = date.getHours().toString().padStart(2, '0')
    val minutes = date.getMinutes().toString().padStart(2, '0')
    return "$month/$day $hours:$minutes"
}
