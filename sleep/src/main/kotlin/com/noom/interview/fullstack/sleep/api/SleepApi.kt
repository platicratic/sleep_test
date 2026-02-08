package com.noom.interview.fullstack.sleep.api

import com.noom.interview.fullstack.sleep.api.dto.SleepCreationDto
import com.noom.interview.fullstack.sleep.api.dto.SleepDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

interface SleepApi {
    @PostMapping("api/sleep")
    fun createSleep(
        @RequestParam("user_id") userId: Long,
        @RequestBody sleepCreationDto: SleepCreationDto
    )

    @GetMapping("api/sleep")
    fun getLastNightSleep(): SleepDto
}