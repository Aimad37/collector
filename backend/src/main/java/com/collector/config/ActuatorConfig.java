package com.collector.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    // Compteur custom : nombre d'annonces créées
    @Bean
    public Counter annoncesCreees(MeterRegistry registry) {
        return Counter.builder("collector.annonces.creees")
                .description("Nombre total d annonces créées")
                .register(registry);
    }

    // Compteur custom : nombre d'annonces validées
    @Bean
    public Counter annoncesValidees(MeterRegistry registry) {
        return Counter.builder("collector.annonces.validees")
                .description("Nombre total d annonces validées")
                .register(registry);
    }

    // Compteur custom : nombre d'annonces rejetées
    @Bean
    public Counter annoncesRejetees(MeterRegistry registry) {
        return Counter.builder("collector.annonces.rejetees")
                .description("Nombre total d annonces rejetées")
                .register(registry);
    }
}