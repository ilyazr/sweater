package ru.zakharov.configs;

import org.flywaydb.core.api.output.CleanResult;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayMigrationStrategyConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        // clean database on startup and then migrate
        return flyway -> {
            CleanResult clean = flyway.clean();
            flyway.migrate();
        };
    }

}
