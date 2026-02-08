package com.noom.interview.fullstack.sleep.server.controller

import com.noom.interview.fullstack.sleep.api.SleepApi
import com.noom.interview.fullstack.sleep.api.dto.SleepCreationDto
import com.noom.interview.fullstack.sleep.api.dto.SleepDto
import com.noom.interview.fullstack.sleep.service.service.SleepService
import org.springframework.web.bind.annotation.RestController

@RestController
class SleepController(private val sleepService: SleepService) : SleepApi {
    override fun createSleep(userId: Long, sleepCreationDto: SleepCreationDto) {
        sleepService.createSleep(userId, sleepCreationDto)
    }

    override fun getLastNightSleep(): SleepDto {
        return sleepService.getLastNightSleepData()
    }
}