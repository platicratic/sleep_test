package com.noom.interview.fullstack.sleep.domain.repository

import com.noom.interview.fullstack.sleep.domain.entity.SleepLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SleepLogRepository: JpaRepository<SleepLogEntity, Long> {
    fun findAllByUserId(userId: Long): List<SleepLogEntity>

    @Query(
        """
            select sl
            from SleepLogEntity sl
            where sl.user.id = :userId and :rangeStart <= sl.endSleep and sl.endSleep <= :rangeEnd
        """
    )
    fun findByUserIdAndBetweenRanges(
        @Param("userId") userId: Long,
        @Param("rangeStart") rangeStart: LocalDateTime,
        @Param("rangeEnd") rangeEnd: LocalDateTime
    ): List<SleepLogEntity>
}