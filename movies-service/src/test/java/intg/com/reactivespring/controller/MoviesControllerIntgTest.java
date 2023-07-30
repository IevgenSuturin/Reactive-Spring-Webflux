package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084) // spin up http server in port 8084
@TestPropertySource(
        properties = {
                "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
                "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
        }
)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.4.11")
public class MoviesControllerIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void retrieveMovieById() {
        String movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("movieinfo.json")
                )
        );
        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("reviews.json")
                )
        );


        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    Movie movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                });
    }

    @Test
    void retrieveMovieById_404() {
        String movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(
                        aResponse().withStatus(404)
                )
        );
        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("reviews.json")
                )
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("There is no movieInfo available for the passed id: " + movieId);

        WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));
    }

    @Test
    void retrieveMovieById_reviews_404() {
        String movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("movieinfo.json")
                )
        );
        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                .willReturn(
                        aResponse().withStatus(404)
                )
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    Movie movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 0;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                });
    }

    @Test
    void retrieveMovieById_5xx() {
        String movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos/" + movieId))
                        .willReturn(
                                aResponse().withStatus(500)
                                        .withBody("MovieInfo Service Unavailable")
                        )
        );
//        stubFor(
//                get(urlPathEqualTo("/v1/reviews"))
//                        .willReturn(
//                                aResponse()
//                                        .withHeader("Content-Type", "application/json")
//                                        .withBodyFile("reviews.json")
//                        )
//        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in MoviesInfoService: MovieInfo Service Unavailable");

        WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));
    }

    @Test
    void retrieveMovieById_Reviews_5xx() {
        String movieId = "abc";

        stubFor(
                get(urlEqualTo("/v1/movieinfos/" + movieId))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("movieinfo.json")
                        )
        );
        stubFor(
                get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(
                                aResponse().withStatus(500).withBody("Review Service UnAvailable")
                        )
        );

        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in ReviewsService: Review Service UnAvailable");

        WireMock.verify(4, getRequestedFor(urlPathMatching("/v1/reviews*")));
    }
}
