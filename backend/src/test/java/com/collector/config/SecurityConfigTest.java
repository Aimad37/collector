package com.collector.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_shouldBeCreated() {
        SecurityConfig config = new SecurityConfig();

        assertNotNull(config.corsConfigurationSource());
    }
}