package com.yet.tetris.feature.history.utils

import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.feature.history.DateFilter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun applyHistoryDateFilter(
    games: List<GameRecord>,
    filter: DateFilter,
    now: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): List<GameRecord> =
    when (filter) {
        DateFilter.ALL -> games
        DateFilter.TODAY -> {
            val today = now.toLocalDateTime(timeZone).date
            games.filter { game ->
                Instant
                    .fromEpochMilliseconds(game.timestamp)
                    .toLocalDateTime(timeZone)
                    .date == today
            }
        }

        DateFilter.THIS_WEEK -> {
            val weekAgo = now.minus(7.days)
            games.filter { game -> game.timestamp >= weekAgo.toEpochMilliseconds() }
        }

        DateFilter.THIS_MONTH -> {
            val currentMonth = now.toLocalDateTime(timeZone).month
            val currentYear = now.toLocalDateTime(timeZone).year
            games.filter { game ->
                val gameDateTime =
                    Instant.Companion
                        .fromEpochMilliseconds(game.timestamp)
                        .toLocalDateTime(timeZone)
                gameDateTime.month == currentMonth && gameDateTime.year == currentYear
            }
        }
    }
