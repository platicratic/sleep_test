package com.noom.interview.fullstack.sleep.api.dto

import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import java.time.Duration
import java.time.Instant

data class SleepDto(
    val date: Instant,
    val timeInBed: Duration,
    val timeInBedStart: Instant,
    val timeInBedEnd: Instant,
    val morningMoodType: MorningMoodType,
)
