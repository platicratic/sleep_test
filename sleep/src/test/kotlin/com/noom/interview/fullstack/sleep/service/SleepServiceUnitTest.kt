package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.api.dto.SleepDto
import com.noom.interview.fullstack.sleep.domain.entity.SleepLogEntity
import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.server.SleepApplication
import com.noom.interview.fullstack.sleep.server.SleepApplication.Companion.UNIT_TEST_PROFILE
import com.noom.interview.fullstack.sleep.service.service.SleepService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.sql.Date
import java.time.Duration
import java.time.Instant

@SpringBootTest
@ActiveProfiles(UNIT_TEST_PROFILE)
@ContextConfiguration(classes = [SleepApplication::class])
class SleepServiceUnitTest {
    private val sleepLogRepository: SleepLogRepository = mockk()
    private val sleepService = SleepService(sleepLogRepository)

    @Test
    fun testGetLastNightSleep() {
        // given
        every { sleepLogRepository.findAll() } returns listOf(
            SleepLogEntity(
                id = 1,
                startSleep = Instant.parse("2026-02-06T23:00:00Z"),
                endSleep = Instant.parse("2026-02-07T06:00:00Z"),
                morningMood = MorningMoodType.BAD
            ),
            SleepLogEntity(
                id = 1,
                startSleep = Instant.parse("2026-02-08T22:00:00Z"),
                endSleep = Instant.parse("2026-02-09T08:10:00Z"),
                morningMood = MorningMoodType.GOOD
            )
        )

        // when
        val lastNightSleep: SleepDto = sleepService.getLastNightSleepData()

        // then
        Assertions.assertEquals(Instant.parse("2026-02-09T08:10:00Z"), lastNightSleep.date)
        Assertions.assertEquals(Duration.ofMinutes(610), lastNightSleep.timeInBed)
        Assertions.assertEquals(Instant.parse("2026-02-08T22:00:00Z"), lastNightSleep.timeInBedStart)
        Assertions.assertEquals(Instant.parse("2026-02-09T08:10:00Z"), lastNightSleep.timeInBedEnd)
        Assertions.assertEquals(MorningMoodType.GOOD, lastNightSleep.morningMoodType)
    }
}
