package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.api.dto.SleepCreationDto
import com.noom.interview.fullstack.sleep.api.dto.SleepDto
import com.noom.interview.fullstack.sleep.domain.entity.SleepLogEntity
import com.noom.interview.fullstack.sleep.domain.entity.UserEntity
import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.server.SleepApplication
import com.noom.interview.fullstack.sleep.server.SleepApplication.Companion.UNIT_TEST_PROFILE
import com.noom.interview.fullstack.sleep.service.service.SleepService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ActiveProfiles(UNIT_TEST_PROFILE)
@ContextConfiguration(classes = [SleepApplication::class])
class SleepServiceUnitTest {
    private val userRepository: UserRepository = mockk()
    private val sleepLogRepository: SleepLogRepository = mockk()
    private val sleepService = SleepService(userRepository, sleepLogRepository)

    companion object {
        private val USER1 = UserEntity(id = 1, firstName = "fName", lastName = "lName")
    }

    @Test
    fun testGetLastNightSleep() {
        // given
        every { userRepository.existsById(any()) } returns true
        every { userRepository.getReferenceById(any()) } returns USER1
        every { sleepLogRepository.findByUserIdAndBetweenRanges(any(), any(), any()) } returns listOf(
            SleepLogEntity(
                id = 1,
                startSleep = LocalDateTime.parse("2026-02-08T22:00:00"),
                endSleep = LocalDateTime.parse("2026-02-09T08:10:00"),
                morningMood = MorningMoodType.GOOD,
                user = USER1
            )
        )

        // when
        val lastNightSleep: SleepDto = sleepService.getLastNightSleepData(1)

        // then
        Assertions.assertEquals(LocalDate.parse("2026-02-09"), lastNightSleep.date)
        Assertions.assertEquals(Duration.ofMinutes(610), lastNightSleep.timeInBed)
        Assertions.assertEquals(LocalDateTime.parse("2026-02-08T22:00:00"), lastNightSleep.timeInBedStart)
        Assertions.assertEquals(LocalDateTime.parse("2026-02-09T08:10:00"), lastNightSleep.timeInBedEnd)
        Assertions.assertEquals(MorningMoodType.GOOD, lastNightSleep.morningMoodType)
    }

    @Test
    fun testCalculateAverageFromDuration() {
        // given
        val durations = listOf(Duration.ofMinutes(50), Duration.ofMinutes(10), Duration.ofMinutes(30))

        // when
        val duration: Duration = sleepService.calculateAverage(durations)

        // then
        Assertions.assertEquals(Duration.ofMinutes(30), duration)
    }

    @Test
    fun testCalculateAverageFromLocalDateTime() {
        // given
        val times = listOf(
            LocalDateTime.of(2026, 1, 1, 8, 30),
            LocalDateTime.of(2026, 1, 10, 7, 20),
            LocalDateTime.of(2026, 1, 7, 9, 10),
        )

        // when
        val time: LocalTime = sleepService.calculateAverage(times)

        // then
        Assertions.assertEquals(LocalTime.of(8, 20), time)
    }

    @Test
    fun testCalculateAverageFromLocalDateTimeOverNight1() {
        // given
        val times = listOf(
            LocalDateTime.of(2026, 1, 1, 22, 0),
            LocalDateTime.of(2026, 1, 2, 2, 0),
            LocalDateTime.of(2026, 1, 2, 3, 0),
        )

        // when
        val time: LocalTime = sleepService.calculateAverage(times)

        // then
        Assertions.assertEquals(LocalTime.of(1, 0), time)
    }

    @Test
    fun testCalculateAverageFromLocalDateTimeOverNight2() {
        // given
        val times = listOf(
            LocalDateTime.of(2026, 1, 1, 21, 0),
            LocalDateTime.of(2026, 1, 2, 23, 0),
            LocalDateTime.of(2026, 1, 2, 3, 0),
        )

        // when
        val time: LocalTime = sleepService.calculateAverage(times)

        // then
        Assertions.assertEquals(LocalTime.of(23, 40), time)
    }

    @Test
    fun testCalculateMoodFrequency() {
        // given
        val moods = listOf(MorningMoodType.BAD, MorningMoodType.OK, MorningMoodType.BAD, MorningMoodType.BAD)

        // when
        val moodMap: Map<MorningMoodType, Int>  = sleepService.calculateMoodFrequency(moods)

        // then
        Assertions.assertEquals(mapOf(MorningMoodType.BAD to 3, MorningMoodType.OK to 1, MorningMoodType.GOOD to 0), moodMap)
    }

    @Test
    fun testValidationOfExistingSleepLog() {
        // given
        val sleepCreationDto = SleepCreationDto(
            timeInBedStart = Instant.parse("2026-02-10T22:00:00Z"),
            timeInBedEnd = Instant.parse("2026-02-11T09:30:00Z"),
            morningMoodType = MorningMoodType.OK,
        )
        every { sleepLogRepository.findByUserIdAndBetweenRanges(any(), any(), any()) } returns listOf(
            SleepLogEntity(
                id = 1,
                startSleep = LocalDateTime.parse("2026-02-06T23:00:00"),
                endSleep = LocalDateTime.parse("2026-02-07T06:00:00"),
                morningMood = MorningMoodType.BAD,
                user = USER1
            ),
        )

        // when + then
        assertThrows<IllegalArgumentException> {
            sleepService.validateExistingSleepLog(1, sleepCreationDto)
        }
    }
}
