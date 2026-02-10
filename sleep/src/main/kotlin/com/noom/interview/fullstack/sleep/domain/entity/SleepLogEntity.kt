package com.noom.interview.fullstack.sleep.domain.entity

import com.noom.interview.fullstack.sleep.domain.enumeration.MorningMoodType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "sleep_log")
data class SleepLogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val startSleep: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val endSleep: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val morningMood: MorningMoodType = MorningMoodType.OK,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity? = null,
)