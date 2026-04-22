package com.champeic.weeklyrecipes.data.models

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

fun LocalDate.mondayOfWeek(): LocalDate =
    minus(dayOfWeek.ordinal, DateTimeUnit.DAY)

fun LocalDate.weekDates(): List<LocalDate> {
    val monday = mondayOfWeek()
    return (0..6).map { monday.plus(it, DateTimeUnit.DAY) }
}

fun DayOfWeek.displayName(): String =
    name.lowercase().replaceFirstChar { it.uppercase() }

fun DayOfWeek.shortDisplayName(): String = displayName().take(3)
