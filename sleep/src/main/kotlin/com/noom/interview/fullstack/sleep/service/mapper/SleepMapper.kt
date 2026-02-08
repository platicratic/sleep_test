package com.noom.interview.fullstack.sleep.service.mapper

import com.noom.interview.fullstack.sleep.api.dto.SleepDto
import com.noom.interview.fullstack.sleep.domain.entity.SleepLogEntity
import java.sql.Date
import java.time.Duration

fun SleepLogEntity.toDto(duration: Duration): SleepDto {
    return SleepDto(
        date = this.endSleep,
        timeInBed = duration,
        timeInBedStart = this.startSleep,
        timeInBedEnd = this.endSleep,
        morningMoodType = this.morningMood,
    )
}
