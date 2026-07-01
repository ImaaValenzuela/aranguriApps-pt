package com.mitimiti.app.utils

/**
 * Formats a Double to a String with a specified number of decimal digits.
 */
fun Double.format(digits: Int): String {
    val raw = this.toString()
    val parts = raw.split(".")
    if (parts.size < 2) {
        return if (digits > 0) {
            raw + "." + "0".repeat(digits)
        } else {
            raw
        }
    }
    val decimals = parts[1]
    return if (digits > 0) {
        if (decimals.length >= digits) {
            parts[0] + "." + decimals.substring(0, digits)
        } else {
            parts[0] + "." + decimals + "0".repeat(digits - decimals.length)
        }
    } else {
        parts[0]
    }
}
