package com.noom.interview.fullstack.sleep.domain.entity

import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "sleep_log")
data class SleepLogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val startSleep: Instant = Instant.now(),

    @Column(nullable = false)
    val endSleep: Instant = Instant.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val morningMood: MorningMoodType = MorningMoodType.OK,
)