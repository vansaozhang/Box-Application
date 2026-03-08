package com.aeu.boxapplication.core.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// String Extensions
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return this.matches(Regex("^[+]?[0-9]{8,15}$"))
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

// Date Extensions
fun Date.toFormattedString(pattern: String = "MMM dd, yyyy"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Long.toDate(): Date {
    return Date(this)
}

fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_MONTH, days)
    return calendar.time
}

fun Date.addMonths(months: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, months)
    return calendar.time
}

// Double Extensions
fun Double.toCurrency(currencySymbol: String = "$"): String {
    return String.format("$currencySymbol%.2f", this)
}

// List Extensions
fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> {
    val start = fromIndex.coerceIn(0, size)
    val end = toIndex.coerceIn(0, size)
    return if (start < end) subList(start, end) else emptyList()
}
