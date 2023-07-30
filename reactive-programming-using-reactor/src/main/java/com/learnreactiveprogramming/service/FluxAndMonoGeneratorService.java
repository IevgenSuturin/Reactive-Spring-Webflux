package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        // in reality flux might be coming from service call
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .log();
    }
    public Flux<String> namesFlux_map(int stringLength) {
        // filter the string whose length is greater than 3
        // in reality flux might be coming from service call
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .filter(name -> name.length() > stringLength)
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFlux_immutability() {
        Flux<String> namesFlux = Flux.fromIterable(List.of("Alex", "Ben", "Chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log();
    }

    public Mono<List<String>> namesMono_flatmap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log(); //Mono <List of A, L, E, X>
    }

        public   Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    private Mono<List<String >> splitStringMono(String string) {
        String[] strings = string.split("");
        return Mono.just(Arrays.asList(strings));
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {
        // filter the string whose length is greater than 3
        // in reality flux might be coming from service call
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .filter(name -> name.length() > stringLength)
                .map(String::toUpperCase)
                // A, L, E, X, C, H, L , O, E
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFlux_transform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name ->
                name.map(String::toUpperCase).filter(s -> s.length()>stringLength);
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .transform(filterMap)
                .flatMap(this::splitString)// A, L, E, X, C, H, L , O, E
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name ->
                name.map(String::toUpperCase)
                        .filter(s -> s.length()>stringLength)
                        .flatMap(this::splitString);

        Flux<String> defaultFlux = Flux.just("default").transform(filterMap);

        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .transform(filterMap) // A, L, E, X, C, H, L , O, E
                .switchIfEmpty(defaultFlux) // D, E, F, A, U, T
                .log();
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        // filter the string whose length is greater than 3
        // in reality flux might be coming from service call
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .filter(name -> name.length() > stringLength)
                .map(String::toUpperCase)
                // A, L, E, X, C, H, L , O, E
                .flatMap(this::splitString_withDelay)
                .log();
    }

    public Flux<String> namesFlux_concatmap(int stringLength) {
        // filter the string whose length is greater than 3
        // in reality flux might be coming from service call
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .filter(name -> name.length() > stringLength)
                .map(String::toUpperCase)
                // A, L, E, X, C, H, L , O, E
                .concatMap(this::splitString_withDelay)
                .log();
    }

    public Flux<String> explore_concat() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatWithFlux() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_concatWithMono() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");

        return aMono.concatWith(bMono).log();
    }

    public Flux<String> explore_merge() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_mergeWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

//        return Flux.merge(abcFlux, defFlux).log();
        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_mergeWithMono() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> dMono = Mono.just("D");

//        return Flux.merge(abcFlux, defFlux).log();
        return aMono.mergeWith(dMono).log();
    }

    public Flux<String> explore_mergeSequential() {
        Flux<String> abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> explore_zip() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
    }


    public Flux<String> explore_zip_1() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");
        Flux<String> _123Flux = Flux.just("1", "2", "3");
        Flux<String> _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }


    public Flux<String> explore_zipWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second).log();
    }

    public Mono<String> explore_zipWithMono() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> dMono = Mono.just("D");

//        return Flux.merge(abcFlux, defFlux).log();
        return aMono.zipWith(dMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }

    private Flux<String> splitString(String name) {
        String[] letters = name.split("");
        return Flux.fromArray(letters);
    }

    private Flux<String> splitString_withDelay(String name) {
        String[] letters = name.split("");
//        int delay = new Random().nextInt(1000);
        int delay = 1000;
        return Flux.fromArray(letters)
                .delayElements(Duration.ofMillis(delay));
    }

    public Mono<String> nameMono() {
        return Mono.just("Alex")
                .log();
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> System.out.println("Flux name is: " + name));

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> System.out.println("Mono name is: " + name));
    }
}
