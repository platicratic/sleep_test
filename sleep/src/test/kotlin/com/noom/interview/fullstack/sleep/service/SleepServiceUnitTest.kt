package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.domain.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.server.SleepApplication
import com.noom.interview.fullstack.sleep.server.SleepApplication.Companion.UNIT_TEST_PROFILE
import com.noom.interview.fullstack.sleep.service.service.SleepService
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ActiveProfiles(UNIT_TEST_PROFILE)
@ContextConfiguration(classes = [SleepApplication::class])
class SleepServiceUnitTest {
    private val sleepLogRepository: SleepLogRepository = mockk()
    private val sleepService = SleepService(sleepLogRepository)

    @Test
    fun test() {
        // given

        // when

        // then
    }
}
