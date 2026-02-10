package com.noom.interview.fullstack.sleep.api.dto

import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class SleepDto(
    val date: LocalDate,
    val timeInBed: Duration,
    val timeInBedStart: LocalDateTime,
    val timeInBedEnd: LocalDateTime,
    val morningMoodType: MorningMoodType,
)
