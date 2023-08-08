package com.reproduce.sample;

import java.time.Duration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@RestController
public class SampleController {

	@Autowired
	private ReactorResourceFactory reactorResourceFactory;
	private WebClient.Builder webClientBuilder;

	@PostConstruct
	public void setup() {
		this.webClientBuilder = WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(reactorResourceFactory, httpClient -> {
				ConnectionProvider connectionProvider = ConnectionProvider.builder("pool1")
					.maxConnections(500)
					.pendingAcquireMaxCount(1000)
					.maxIdleTime(Duration.ofMillis(8000L))
					.maxLifeTime(Duration.ofMillis(8000L))
					.lifo()
					.build();

				return HttpClient.create(connectionProvider)
					.compress(true)
					.responseTimeout(
						Duration.ofMillis(1000L)
					);
			}))
			.baseUrl("http://127.0.0.1:8082")
			.defaultHeaders(headers -> {
				headers.setContentType(MediaType.TEXT_PLAIN);
			});
	}

	@GetMapping("/test")
	public Flux<String> test() {
		return Flux.range(1, 1000)
			.flatMap(i -> callWebClientApi());
	}

	public Mono<String> callWebClientApi() {
		return webClientBuilder
			.build()
			.post()
			.uri(
				uriBuilder -> uriBuilder
					.path("/target")
					.build()
			)
			.body(BodyInserters.fromValue("hi"))
			.retrieve()
			.bodyToMono(String.class);
	}
}
