package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    static String REVIEWS_URL = "/v1/reviews";

    @Test
    void addReview() {
        //given
        Review review = new Review(null, 1L, "Awesome Movie", 9.0);

        //when
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        //then
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult ->
                        {
                            Review savedReview = reviewEntityExchangeResult.getResponseBody();
                            assert savedReview!=null;
                            assert savedReview.getMovieInfoId() != null;
                        }
                );
    }

    @Test
    void addReview_validation() {
        //given
        Review review = new Review(null, null, "Awesome Movie", -9.0);

        //when
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        //then
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("rating.movieInfoId: must not be null, rating.negative : please pass a non-negative value");
    }


    @Test
    void getAllReviewsByMovieInfoId() {
        Long movieInfoId = 1L;
        URI uri = UriComponentsBuilder.fromUriString(REVIEWS_URL)
                .queryParam("movieInfoId", movieInfoId)
                .buildAndExpand()
                .toUri();

        when(reviewReactiveRepository.findAllByMovieInfoId(movieInfoId)).thenReturn(Flux.just(
                new Review("abc1", 1L, "Awesome Movie1", 9.0),
                new Review("abc2", 1L, "Awesome Movie2", 9.5)
                ));

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);

    }

    @Test
    void getAllReviews() {
        when(reviewReactiveRepository.findAll()).thenReturn(Flux.just(
                new Review("abc", 1L, "Awesome Movie", 9.0)
        ));

        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(1);
    }


    @Test
    void updateReviewById() {
        String reviewId= "abc";
        Review storedReview = new Review("abc", 2L, "Excellent Movie", 8.0);
        Review savedReview = new Review("abc", 2L, "Excellent Movie Updated", 8.5);

        when(reviewReactiveRepository.findById("abc")).thenReturn(Mono.just(storedReview));
        when(reviewReactiveRepository.save(storedReview)).thenReturn(Mono.just(savedReview));

        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .bodyValue(savedReview)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult ->
                        {
                            Review updatedReview = reviewEntityExchangeResult.getResponseBody();
                            assert updatedReview != null;
                            assert updatedReview.getReviewId() != null;
                            assertEquals("Excellent Movie Updated", updatedReview.getComment());
                            assertEquals(8.5, updatedReview.getRating());
                        }
                );
    }

    @Test
    void deleteReview() {
        String reviewId = "abc";
        Review review = new Review("abc", 2L, "Excellent Movie", 8.0);

        when(reviewReactiveRepository.deleteById(reviewId)).thenReturn(Mono.empty());
        when(reviewReactiveRepository.findById(reviewId)).thenReturn(Mono.just(review));

        webTestClient
                .delete()
                .uri(REVIEWS_URL+"/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

}
