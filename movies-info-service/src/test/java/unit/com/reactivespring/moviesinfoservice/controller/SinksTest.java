package com.reactivespring.moviesinfoservice.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void sink() {
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe( (i) -> System.out.println("Subscriber 1: " + i));

        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe( (i) -> System.out.println("Subscriber 2: " + i));

        replaySink.tryEmitNext(122);

        Flux<Integer> integerFlux3 = replaySink.asFlux();
        integerFlux3.subscribe( (i) -> System.out.println("Subscriber 3: " + i));
    }

    @Test
    void sinks_multicast() {
        Sinks.Many<Object> multicast = Sinks.many().multicast().onBackpressureBuffer();

        multicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Object> integerFlux1 = multicast.asFlux();
        integerFlux1.subscribe( (i) -> System.out.println("Subscriber 1: " + i));

        Flux<Object> integerFlux2 = multicast.asFlux();
        integerFlux2.subscribe( (i) -> System.out.println("Subscriber 2: " + i));

        multicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Object> integerFlux3 = multicast.asFlux();
        integerFlux3.subscribe( (i) -> System.out.println("Subscriber 3: " + i));
    }

    @Test
    void sinks_unicast() {
        Sinks.Many<Object> unicast = Sinks.many().unicast().onBackpressureBuffer();

        unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Object> integerFlux1 = unicast.asFlux();
        integerFlux1.subscribe( (i) -> System.out.println("Subscriber 1: " + i));

        Flux<Object> integerFlux2 = unicast.asFlux();
        integerFlux2.subscribe( (i) -> System.out.println("Subscriber 2: " + i));

        unicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Object> integerFlux3 = unicast.asFlux();
        integerFlux3.subscribe( (i) -> System.out.println("Subscriber 3: " + i));
    }
}
