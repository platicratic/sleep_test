package com.noom.interview.fullstack.sleep.service.service

import com.noom.interview.fullstack.sleep.api.dto.SleepAverageDto
import com.noom.interview.fullstack.sleep.api.dto.SleepCreationDto
import com.noom.interview.fullstack.sleep.api.dto.SleepDto
import com.noom.interview.fullstack.sleep.domain.entity.SleepLogEntity
import com.noom.interview.fullstack.sleep.domain.entity.UserEntity
import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.service.mapper.toDto
import org.springframework.stereotype.Service
import java.time.*

@Service
class SleepService(
    private val userRepository: UserRepository,
    private val sleepLogRepository: SleepLogRepository,
) {
    fun createSleep(userId: Long, sleepCreationDto: SleepCreationDto): SleepLogEntity {
        val user: UserEntity = findUserById(userId)

        return sleepLogRepository.save(
            SleepLogEntity(
                id = 0L,
                startSleep = LocalDateTime.ofInstant(sleepCreationDto.timeInBedStart, ZoneOffset.UTC),
                endSleep = LocalDateTime.ofInstant(sleepCreationDto.timeInBedEnd, ZoneOffset.UTC),
                morningMood = sleepCreationDto.morningMoodType,
                user = user,
            )
        )
    }

    fun getLastNightSleepData(userId: Long): SleepDto {
        val user: UserEntity = findUserById(userId)

        val lastSleepLog: SleepLogEntity? = sleepLogRepository.findAllByUserId(user.id).maxByOrNull { it.endSleep }
        if (lastSleepLog == null) {
            throw NoSuchElementException("Could not find any sleep data of last night for user: ${user.id}")
        }

        return lastSleepLog.toDto(calculateTimeInBed(lastSleepLog))
    }

    fun getSleepAverages(userId: Long, days: Long): SleepAverageDto {
        val user: UserEntity = findUserById(userId)
        val dateRangeStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay().minusDays(days)
        val dateRangeEnd = LocalDate.now(ZoneOffset.UTC).plusDays(1).atStartOfDay().minusNanos(1)

        val sleepAverages: List<SleepLogEntity> = sleepLogRepository.findByUserIdAndBetweenRanges(
            userId = user.id,
            rangeStart = dateRangeStart,
            rangeEnd = dateRangeEnd
        )

        return SleepAverageDto(
            dateRangeStart = dateRangeStart.toLocalDate(),
            dateRangeEnd = dateRangeEnd.toLocalDate(),
            averageTimeInBed = calculateAverage(sleepAverages.map { calculateTimeInBed(it) }),
            averageTimeInBedStart = calculateAverage(sleepAverages.map { it.startSleep }),
            averageTimeInBedEnd = calculateAverage(sleepAverages.map { it.endSleep }),
            morningMoodFrequency = calculateMoodFrequency(sleepAverages.map { it.morningMood })
        )
    }

    fun calculateAverage(items: List<Duration>): Duration {
        return items.fold(Duration.ZERO) { acc, item -> acc + item }.dividedBy(items.size.toLong())
    }

    fun calculateAverage(items: List<LocalDateTime>): LocalTime {
        return LocalTime.ofSecondOfDay(
            items
            .map {it.toLocalTime().toSecondOfDay() }
            .fold(0) { acc, item -> acc + item } / items.size.toLong())
    }

    fun calculateMoodFrequency(moods: List<MorningMoodType>): Map<MorningMoodType, Int> {
        return MorningMoodType.values().associateWith { 0 } + moods.groupingBy { it }.eachCount()
    }

    private fun findUserById(userId: Long): UserEntity {
        if (!userRepository.existsById(userId)) {
            throw NoSuchElementException("User with id: $userId does not exist")
        }
        return userRepository.getReferenceById(userId)
    }

    private fun calculateTimeInBed(sleepLog: SleepLogEntity): Duration {
        return Duration.between(sleepLog.startSleep, sleepLog.endSleep)
    }
}
