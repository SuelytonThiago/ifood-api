package com.example.msstore.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(initializers = ContainerBase.Initializer.class)
public class ContainerBase {
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        static PostgreSQLContainer<?> postgresDBContainer = new PostgreSQLContainer<>("postgres:latest")
                .withExposedPorts(5432);

        private static void startContainers(){
            Startables.deepStart(Stream.of(postgresDBContainer)).join();
        }

        private static Map<String,String> createConnectionConfiguration(){
            return Map.of(
                    "spring.datasource.url", postgresDBContainer.getJdbcUrl(),
                    "spring.datasource.username",postgresDBContainer.getUsername(),
                    "spring.datasource.password", postgresDBContainer.getPassword());
        }

        @Override
        @SuppressWarnings({"rawtypes, unchecked"})
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testcontainers =
                    new MapPropertySource("testcontainers",(Map) createConnectionConfiguration());
            environment.getPropertySources().addFirst(testcontainers);
        }

    }
}
