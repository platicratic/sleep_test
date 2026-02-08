package com.noom.interview.fullstack.sleep.domain

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.noom.interview.fullstack.sleep.domain"])
@EntityScan(basePackages = ["com.noom.interview.fullstack.sleep.domain"])
class DomainConfiguration
