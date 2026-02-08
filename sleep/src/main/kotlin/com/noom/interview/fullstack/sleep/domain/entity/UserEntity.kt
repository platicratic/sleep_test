package com.noom.interview.fullstack.sleep.domain.entity

import javax.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val firstName: String = "",

    @Column(nullable = false)
    val lastName: String = "",
)
