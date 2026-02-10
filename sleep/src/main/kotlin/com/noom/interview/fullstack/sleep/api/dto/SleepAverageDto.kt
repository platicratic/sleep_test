package com.noom.interview.fullstack.sleep.api.dto

import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class SleepAverageDto(
    val dateRangeStart: LocalDate,
    val dateRangeEnd: LocalDate,
    val averageTimeInBed: Duration,
    val averageTimeInBedStart: LocalTime,
    val averageTimeInBedEnd: LocalTime,
    val morningMoodFrequency: Map<MorningMoodType, Int>,
)
