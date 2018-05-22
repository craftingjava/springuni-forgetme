package com.springuni.forgetme.core.orm;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcLocalDateTimeProvider")
@EnableJpaRepositories("com.springuni.forgetme")
public class JpaConfig {

}
