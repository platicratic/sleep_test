package com.noom.interview.fullstack.sleep.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.noom.interview.fullstack.sleep"])
class SleepApplication {
	companion object {
		const val UNIT_TEST_PROFILE = "unittest"
		const val INTEGRATION_TEST_PROFILE = "integrationtest"
	}
}

fun main(args: Array<String>) {
	runApplication<SleepApplication>(*args)
}
