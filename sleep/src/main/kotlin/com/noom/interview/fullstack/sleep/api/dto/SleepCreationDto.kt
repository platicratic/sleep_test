package com.noom.interview.fullstack.sleep.api.dto

import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import java.time.Instant

data class SleepCreationDto(
    val timeInBedStart: Instant,
    val timeInBedEnd: Instant,
    val morningMoodType: MorningMoodType,
)