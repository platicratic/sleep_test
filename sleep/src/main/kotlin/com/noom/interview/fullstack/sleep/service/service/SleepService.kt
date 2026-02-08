package com.noom.interview.fullstack.sleep.service.service

import com.noom.interview.fullstack.sleep.api.dto.SleepCreationDto
import com.noom.interview.fullstack.sleep.api.dto.SleepDto
import com.noom.interview.fullstack.sleep.domain.entity.SleepLogEntity
import com.noom.interview.fullstack.sleep.domain.entity.UserEntity
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.service.mapper.toDto
import org.springframework.stereotype.Service

import java.time.Duration

@Service
class SleepService(
    private val userRepository: UserRepository,
    private val sleepLogRepository: SleepLogRepository,
) {
    fun createSleep(userId: Long, sleepCreationDto: SleepCreationDto): SleepLogEntity {
        val user: UserEntity = userRepository.findById(userId).orElseThrow()

        return sleepLogRepository.save(
            SleepLogEntity(
                id = 0L,
                startSleep = sleepCreationDto.timeInBedStart,
                endSleep = sleepCreationDto.timeInBedEnd,
                morningMood = sleepCreationDto.morningMoodType,
                user = user,
            )
        )
    }

    fun getLastNightSleepData(): SleepDto {
        val lastSleepLog: SleepLogEntity? = sleepLogRepository.findAll().maxByOrNull { it.endSleep }
        if (lastSleepLog == null) {
            throw NoSuchElementException("Could not find any sleep data for last night")
        }

        return lastSleepLog.toDto(calculateTimeInBed(lastSleepLog))
    }

    private fun calculateTimeInBed(sleepLog: SleepLogEntity): Duration {
        return Duration.between(sleepLog.startSleep, sleepLog.endSleep)
    }
}