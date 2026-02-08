package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.api.dto.SleepCreationDto
import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import com.noom.interview.fullstack.sleep.server.SleepApplication
import com.noom.interview.fullstack.sleep.server.SleepApplication.Companion.INTEGRATION_TEST_PROFILE
import com.noom.interview.fullstack.sleep.service.service.SleepService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.Instant

@DataJpaTest
@ActiveProfiles(INTEGRATION_TEST_PROFILE)
@ContextConfiguration(classes = [SleepApplication::class])
@ComponentScan(basePackages = ["com.noom.interview.fullstack.sleep.service", "com.noom.interview.fullstack.sleep.domain"])
// TODO find a better configuration of the integration test
class SleepServiceIntegrationTest {
    @Autowired
    lateinit var sleepService: SleepService

    @Test
    fun testCreationSleepLog () {
        // given
        val sleepCreationDto = SleepCreationDto(
            timeInBedStart = Instant.parse("2024-02-07T22:00:00Z"),
            timeInBedEnd = Instant.parse("2024-02-08T10:30:00Z"),
            morningMoodType = MorningMoodType.OK,
        )
        
        // when
        val sleepLogEntity = sleepService.createSleep(sleepCreationDto)

        // then
        Assertions.assertTrue(sleepLogEntity.id > 0)
        Assertions.assertEquals(Instant.parse("2024-02-07T22:00:00Z"), sleepLogEntity.startSleep)
        Assertions.assertEquals(Instant.parse("2024-02-08T10:30:00Z"), sleepLogEntity.endSleep)
        Assertions.assertEquals(MorningMoodType.OK, sleepLogEntity.morningMood)
    }
}