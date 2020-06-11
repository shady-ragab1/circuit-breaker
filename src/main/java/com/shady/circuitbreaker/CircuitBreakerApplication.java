package com.shady.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/*
reference : https://piotrminkowski.com/2019/12/11/circuit-breaking-in-spring-cloud-gateway-with-resilience4j/
 */

@SpringBootApplication
@Configuration
public class CircuitBreakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircuitBreakerApplication.class, args);
	}


	/*@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		//circuit breaker is open, the call is short circuited
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				//.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()) //default 100, after 100 requests with 50% failure the circuit will open, 503
				.circuitBreakerConfig(CircuitBreakerConfig.custom()
						.slidingWindowSize(10)
						//.failureRateThreshold(66.6F)
						.build())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(200)).build())
				.build());
	}*/


	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.custom()
						.slidingWindowSize(5)
						.permittedNumberOfCallsInHalfOpenState(5)
						.failureRateThreshold(50.0F)
						.waitDurationInOpenState(Duration.ofMillis(30))
						.build())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(200)).build()).build());
	}



}
