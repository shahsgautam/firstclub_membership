package com.firstclub.membership.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuration class for controlling data initialization.
 * This configuration checks if data initialization should be performed
 * and provides a simple mechanism to control the process.
 */
@Configuration
@Slf4j
@Profile("!test") // Exclude from test profile
public class DataInitializationConfig {

    @Value("${app.data.initialization.enabled:true}")
    private boolean initializationEnabled;

    @Value("${app.data.initialization.force:false}")
    private boolean forceInitialization;

    @Bean
    public CommandLineRunner dataInitializationChecker(JdbcTemplate jdbcTemplate) {
        return args -> {
            if (!initializationEnabled) {
                log.info("Data initialization is disabled via configuration.");
                return;
            }

            // Check if data already exists
            Long planCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM membership_plans", Long.class);
            Long tierCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM membership_tiers", Long.class);

            if (!forceInitialization && (planCount != null && planCount > 0) || (tierCount != null && tierCount > 0)) {
                log.info(
                        "Database already contains data. Skipping initialization. Use app.data.initialization.force=true to override.");
                return;
            }

            log.info("Data initialization is enabled. SQL script will be executed automatically by Spring Boot.");
            log.info("Check logs for data.sql execution details.");
        };
    }
}
