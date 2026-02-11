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
        validateExistingSleepLog(userId, sleepCreationDto)

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

    fun findSleepLogsOfDate(userId: Long, date: LocalDate): List<SleepLogEntity> {
        val dateRangeStart = date.atStartOfDay()
        val dateRangeEnd = date.plusDays(1).atStartOfDay().minusNanos(1)

        return sleepLogRepository.findByUserIdAndBetweenRanges(userId = userId, dateRangeStart, dateRangeEnd)
    }

    fun validateExistingSleepLog(userId: Long, sleepCreationDto: SleepCreationDto) {
        val date = LocalDate.ofInstant(sleepCreationDto.timeInBedEnd, ZoneOffset.UTC)
        val existingSleepLogs = findSleepLogsOfDate(userId = userId, date = date)

        if (existingSleepLogs.isNotEmpty()) {
            throw IllegalArgumentException("There is already an existing sleep log at this day.")
        }
    }

    fun getLastNightSleepData(userId: Long): SleepDto {
        val user: UserEntity = findUserById(userId)

        val date = LocalDate.now(ZoneOffset.UTC)
        val lastSleepLogs: List<SleepLogEntity> = findSleepLogsOfDate(userId, date)
        if (lastSleepLogs.isEmpty()) {
            throw NoSuchElementException(
                "Could not find any sleep data of last night (${date}) for user: ${user.id}. " +
                        "Please create a sleep log for last night and try again."
            )
        } else if (lastSleepLogs.size > 1) {
            throw Exception("To many sleep logs for last night sleep for user: ${user.id}")
        }

        return lastSleepLogs.single().toDto(calculateTimeInBed(lastSleepLogs.single()))
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
        val itemsInSeconds: List<Long> = items.map {
            val seconds = it.toLocalTime().toSecondOfDay().toLong()
            if (seconds < HOURS12) seconds + HOURS24 else seconds
        }

        val avg = itemsInSeconds
            .fold(0) { acc: Long, item: Long -> acc + item } / items.size.toLong() % HOURS24

        return LocalTime.ofSecondOfDay(avg)
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

    companion object {
        const val HOURS24: Long = 24 * 60 * 60
        const val HOURS12: Long = 12 * 60 * 60
    }
}
