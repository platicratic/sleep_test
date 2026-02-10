package com.noom.interview.fullstack.sleep.api

import com.noom.interview.fullstack.sleep.api.dto.SleepAverageDto
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
    fun getLastNightSleep(@RequestParam("user_id") userId: Long): SleepDto

    /**
     *   Returns the sleep averages for specified number of days
     *
     *   @param userId the user you want to retrieve sleep averages
     *   @param days number of last days averages, if omitted it defaults to last 30 days averages
     */
    @GetMapping("api/sleep-averages")
    fun getSleepAverages(@RequestParam("user_id") userId: Long, @RequestParam("days", defaultValue = "30") days: Long): SleepAverageDto
}