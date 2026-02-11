package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.api.dto.SleepAverageDto
import com.noom.interview.fullstack.sleep.api.dto.SleepCreationDto
import com.noom.interview.fullstack.sleep.domain.entity.SleepLogEntity
import com.noom.interview.fullstack.sleep.domain.entity.UserEntity
import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.domain.repository.UserRepository
import com.noom.interview.fullstack.sleep.server.SleepApplication
import com.noom.interview.fullstack.sleep.server.SleepApplication.Companion.INTEGRATION_TEST_PROFILE
import com.noom.interview.fullstack.sleep.service.service.SleepService
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.*
import java.util.stream.Stream

@DataJpaTest
@ActiveProfiles(INTEGRATION_TEST_PROFILE)
@ContextConfiguration(classes = [SleepApplication::class])
@ComponentScan(basePackages = ["com.noom.interview.fullstack.sleep.service", "com.noom.interview.fullstack.sleep.domain"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SleepServiceIntegrationTest {
    @Autowired
    lateinit var sleepService: SleepService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var sleepLogRepository: SleepLogRepository

    @BeforeAll
    fun setup() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now(ZoneOffset.UTC) } returns LocalDate.of(2026, 2, 10)
        userRepository.save(UserEntity(id = 1, firstName = "fName", lastName = "lName"))
    }

    @AfterAll
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun testCreationSleepLog() {
        // given
        val sleepCreationDto = SleepCreationDto(
            timeInBedStart = Instant.parse("2026-02-07T22:00:00Z"),
            timeInBedEnd = Instant.parse("2026-02-08T10:30:00Z"),
            morningMoodType = MorningMoodType.OK,
        )

        // when
        val sleepLogEntity = sleepService.createSleep(1, sleepCreationDto)

        // then
        Assertions.assertTrue(sleepLogEntity.id > 0)
        Assertions.assertEquals(LocalDateTime.parse("2026-02-07T22:00:00"), sleepLogEntity.startSleep)
        Assertions.assertEquals(LocalDateTime.parse("2026-02-08T10:30:00"), sleepLogEntity.endSleep)
        Assertions.assertEquals(MorningMoodType.OK, sleepLogEntity.morningMood)
        Assertions.assertEquals("lName", sleepLogEntity.user?.lastName)
    }

    @ParameterizedTest
    @MethodSource("sleepAveragesTestCases")
    fun `test get sleep averages`(days: Long, sleepEntries: List<SleepLogEntity>, expected: SleepAverageDto) {
        // given
        sleepLogRepository.saveAll(sleepEntries)

        // when
        val sleepAverageDto: SleepAverageDto = sleepService.getSleepAverages(1, days)

        // then
        Assertions.assertEquals(expected, sleepAverageDto)
    }


    companion object {
        private val USER1 = UserEntity(id = 1, firstName = "fName", lastName = "lName")

        @JvmStatic
        fun sleepAveragesTestCases(): Stream<Array<Any>> = Stream.of(
            // Test for last 30 days
            arrayOf(
                30, // days
                listOf(
                    SleepLogEntity(
                        id = 1L,
                        startSleep = LocalDateTime.parse("2026-02-07T22:00:00"),
                        endSleep = LocalDateTime.parse("2026-02-08T08:00:00"),
                        morningMood = MorningMoodType.OK,
                        user = USER1,
                    ),
                    SleepLogEntity(
                        id = 2L,
                        startSleep = LocalDateTime.parse("2026-02-08T23:00:00"),
                        endSleep = LocalDateTime.parse("2026-02-09T05:00:00"),
                        morningMood = MorningMoodType.BAD,
                        user = USER1,
                    )
                ), // sleep entries
                SleepAverageDto(
                    dateRangeStart = LocalDate.parse("2026-01-11"),
                    dateRangeEnd = LocalDate.parse("2026-02-10"),
                    averageTimeInBed = Duration.parse("PT8H0M"),
                    averageTimeInBedStart = LocalTime.of(22, 30),
                    averageTimeInBedEnd = LocalTime.of(6, 30),
                    morningMoodFrequency = mapOf(MorningMoodType.BAD to 1, MorningMoodType.OK to 1, MorningMoodType.GOOD to 0)
                )
            ),
            // Test for last 5 days
            arrayOf(
                5, // days
                listOf(
                    SleepLogEntity(
                        id = 1L,
                        startSleep = LocalDateTime.parse("2026-02-02T22:00:00"),
                        endSleep = LocalDateTime.parse("2026-02-03T08:00:00"),
                        morningMood = MorningMoodType.OK,
                        user = USER1,
                    ),
                    SleepLogEntity(
                        id = 2L,
                        startSleep = LocalDateTime.parse("2026-02-08T23:00:00"),
                        endSleep = LocalDateTime.parse("2026-02-09T05:00:00"),
                        morningMood = MorningMoodType.BAD,
                        user = USER1,
                    )
                ), // sleep entries
                SleepAverageDto(
                    dateRangeStart = LocalDate.parse("2026-02-05"),
                    dateRangeEnd = LocalDate.parse("2026-02-10"),
                    averageTimeInBed = Duration.parse("PT6H0M"),
                    averageTimeInBedStart = LocalTime.of(23, 0),
                    averageTimeInBedEnd = LocalTime.of(5, 0),
                    morningMoodFrequency = mapOf(MorningMoodType.BAD to 1, MorningMoodType.OK to 0, MorningMoodType.GOOD to 0)
                )
            ),
        )
    }
}