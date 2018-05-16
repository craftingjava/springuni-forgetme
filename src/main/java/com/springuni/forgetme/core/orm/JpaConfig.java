package com.springuni.forgetme.core.orm;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcLocalDateTimeProvider")
public class JpaConfig {

}
