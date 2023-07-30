package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.model.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {
    private static String MOVIES_INFO_URL = "/v1/movieinfos";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoService;

    @Test
    void getAllMoviesInfo() {
        when(moviesInfoService.getAllMovieInfos()).thenReturn(Flux.fromIterable(getMockedMovieInfos()));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        String movieInfoId = "abc";
        MovieInfo movieInfo = getMockedMovieInfos().get(2);
        when(moviesInfoService.getMovieInfoById(movieInfoId)).thenReturn(Mono.just(movieInfo));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL+"/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void addMovieInfo() {
        MovieInfo movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(moviesInfoService.addMovieInfo(isA(MovieInfo.class)))
                .thenReturn(Mono.just(new MovieInfo("mockedId", "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult ->
                        {
                            MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                            assert savedMovieInfo!=null;
                            assert savedMovieInfo.getMovieInfoId() != null;
                            assertEquals("mockedId", savedMovieInfo.getMovieInfoId());
                        }
                );
    }

    @Test
    void addMovieInfo_validation() {
        MovieInfo movieInfo = new MovieInfo(null, "",
                -2005, List.of(""), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    String responseBody = stringEntityExchangeResult.getResponseBody();
                    String expectedErrorMessage = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be positive";
                    assertEquals(expectedErrorMessage, responseBody);
                });
//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult ->
//                        {
//                            MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                            assert savedMovieInfo!=null;
//                            assert savedMovieInfo.getMovieInfoId() != null;
//                            assertEquals("mockedId", savedMovieInfo.getMovieInfoId());
//                        }
//                )
    }
    @Test
    void updateMovieInfo() {
        MovieInfo movieInfo = getMockedMovieInfos().get(2);

        String movieInfoId = "abc";
        when(moviesInfoService.updateMovieInfo(isA(MovieInfo.class), movieInfoId)).thenReturn(Mono.just(movieInfo));


        webTestClient
                .put()
                .uri(MOVIES_INFO_URL+"/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult ->
                        {
                            MovieInfo updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                            assert updatedMovieInfo!=null;
                            assert updatedMovieInfo.getMovieInfoId() != null;
                            assertEquals("Dark Knight Rises", updatedMovieInfo.getName());
                        }
                );
    }

    @Test
    void deleteMovieInfo() {
        String movieInfoId = "abc";

        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL+"/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }


    private List<MovieInfo> getMockedMovieInfos() {
        return List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

    }
}
