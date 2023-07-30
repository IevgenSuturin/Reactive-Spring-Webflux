package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    public void namesFlux() {
        //given

        //when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
//                .expectNext("Alex", "Ben", "Chloe")
//                .expectNextCount(3)
                .expectNext("Alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_map() {
        int stringLength = 3;

        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFlux)
                .expectNext("Alex", "Ben", "Chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        StepVerifier.create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMono_flatmap() {
        int stringLength = 3;
        Mono<List<String>> namesMono = fluxAndMonoGeneratorService.namesMono_flatmap(stringLength);

        StepVerifier.create(namesMono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter() {
        int stringLength = 3;
        Mono<String> nameMono = fluxAndMonoGeneratorService.namesMono_map_filter(stringLength);

        StepVerifier.create(nameMono)
                .expectNext("ALEX")
                .verifyComplete();

    }

    @Test
    void namesMono_flatMapMany() {
        int stringLength = 3;
        Flux<String> nameFlux = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);

        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();

    }

    @Test
    void namesFlux_transform() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_1() {
        int stringLength = 6;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        int stringLength = 6;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {

        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_concat();

        StepVerifier.create(lettersFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatWithFlux() {

        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_concatWithFlux();

        StepVerifier.create(lettersFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatWithMono() {

        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_concatWithMono();

        StepVerifier.create(lettersFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_merge();

        StepVerifier.create(lettersFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith() {
        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_mergeWith();

        StepVerifier.create(lettersFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_mergeWithMono() {
        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_mergeWithMono();

        StepVerifier.create(lettersFlux)
                .expectNext("A", "D")
                .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {
        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_mergeSequential();

        StepVerifier.create(lettersFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_zip();

        StepVerifier.create(lettersFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1() {
        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_zip_1();

        StepVerifier.create(lettersFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void explore_zipWith() {
        Flux<String> lettersFlux = fluxAndMonoGeneratorService.explore_zipWith();

        StepVerifier.create(lettersFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zipWithMono() {
        Mono<String> lettersFlux = fluxAndMonoGeneratorService.explore_zipWithMono();

        StepVerifier.create(lettersFlux)
                .expectNext("AD")
                .verifyComplete();
    }
}