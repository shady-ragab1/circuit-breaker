package com.shady.circuitbreaker;


import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Repeat;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CircuitBreakerApplicationTests {

	private static ClientAndServer mockServer;

//	private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerApplicationTests.class);

	@Autowired
	TestRestTemplate template;


	@Test
	void contextLoads() {
	}

	/*@BeforeAll
	public static void init() {

		mockServer = ClientAndServer.startClientAndServer(8989);


		System.setProperty("logging.level.root", "OFF");
		//System.setProperty("mockserver.logLevel", "OFF");
		//System.setProperty("-Dmockserver.logLevel", "OFF");
		//System.setProperty("logging.level.org.springframework", "OFF");
		System.setProperty("spring.cloud.gateway.routes[0].id", "account-service");
		System.setProperty("spring.cloud.gateway.routes[0].uri", "http://localhost:" + mockServer.getLocalPort());
		System.setProperty("spring.cloud.gateway.routes[0].predicates[0]", "Path=/account/**");
		//System.setProperty("spring.cloud.gateway.routes[0].filters[0]", "RewritePath=/account/(?.*), /$\\{path}");
		System.setProperty("spring.cloud.gateway.routes[0].filters[0]", "RewritePath=/account/(?<path>.*), /$\\{path}");
		System.setProperty("spring.cloud.gateway.routes[0].filters[1].name", "CircuitBreaker");
		System.setProperty("spring.cloud.gateway.routes[0].filters[1].args.name", "exampleSlowCircuitBreaker");
		//MockServerClient client = new MockServerClient(mockServer.getRemoteAddress().getHostName(), mockServer.getLocalPort());
		MockServerClient client = new MockServerClient("localhost", mockServer.getLocalPort());
		client.when(HttpRequest.request()
				.withPath("/1"))
				.respond(response()
						.withBody("{\"id\":1,\"number\":\"1234567890\"}")
						.withHeader("Content-Type", "application/json"));
		client.when(HttpRequest.request()
				.withPath("/2"))
				.respond(response()
						.withBody("{\"id\":2,\"number\":\"1234567891\"}")
						.withDelay(TimeUnit.MILLISECONDS, 2000)
						.withHeader("Content-Type", "application/json"));
	}*/

	@BeforeAll
	public static void init() {

		mockServer = ClientAndServer.startClientAndServer(8989);


		System.setProperty("logging.level.root", "OFF");
		//System.setProperty("mockserver.logLevel", "OFF");
		//System.setProperty("-Dmockserver.logLevel", "OFF");
		//System.setProperty("logging.level.org.springframework", "OFF");
		System.setProperty("spring.cloud.gateway.routes[0].id", "account-service");
		System.setProperty("spring.cloud.gateway.routes[0].uri", "http://localhost:" + mockServer.getLocalPort());
		System.setProperty("spring.cloud.gateway.routes[0].predicates[0]", "Path=/account/**");
		//System.setProperty("spring.cloud.gateway.routes[0].filters[0]", "RewritePath=/account/(?.*), /$\\{path}");
		System.setProperty("spring.cloud.gateway.routes[0].filters[0]", "RewritePath=/account/(?<path>.*), /$\\{path}");
		System.setProperty("spring.cloud.gateway.routes[0].filters[1].name", "CircuitBreaker");
		System.setProperty("spring.cloud.gateway.routes[0].filters[1].args.name", "exampleSlowCircuitBreaker");
		//MockServerClient client = new MockServerClient(mockServer.getRemoteAddress().getHostName(), mockServer.getLocalPort());
		MockServerClient client = new MockServerClient("localhost", mockServer.getLocalPort());

		client.when(HttpRequest.request()
				.withPath("/1"))
				.respond(response()
						.withBody("{\"id\":1,\"number\":\"1234567890\"}")
						.withHeader("Content-Type", "application/json"));
		client.when(HttpRequest.request()
				.withPath("/2"), Times.exactly(5))
				.respond(response()
						.withBody("{\"id\":2,\"number\":\"1234567891\"}")
						.withDelay(TimeUnit.MILLISECONDS, 200)
						.withHeader("Content-Type", "application/json"));
		client.when(HttpRequest.request()
				.withPath("/2"))
				.respond(response()
						.withBody("{\"id\":2,\"number\":\"1234567891\"}")
						.withHeader("Content-Type", "application/json"));
	}

	@AfterAll
	public static void terminate(){
		mockServer.stop();
	}


	//@BenchmarkOptions(warmupRounds = 0, concurrency = 5, benchmarkRounds = 200)
	@RepeatedTest(15)
	public void testAccountService(RepetitionInfo repetitionInfo) {
		int gen= repetitionInfo.getCurrentRepetition() % 2;
		gen++;
		ResponseEntity r = template.exchange("/account/{id}", HttpMethod.GET, null, Account.class, gen);
		//LOGGER.info("{}. Received: status->{}, payload->{}, call->{}", repetitionInfo.getCurrentRepetition(), r.getStatusCodeValue(), r.getBody(), gen);
		System.out.format("%d. Received: status->%d, payload->%s, call->%d%n", repetitionInfo.getCurrentRepetition(), r.getStatusCodeValue(), r.getBody(), gen);
	}


}
