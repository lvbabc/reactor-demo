import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Test {
    public static void main(String[] args) {
        Flux.just(5, 10)
                .flatMap(x ->
                        Flux.interval(Duration.of(x*10, ChronoUnit.MILLIS),
                                Duration.of(100, ChronoUnit.MILLIS))
                                .take(x))
                .toStream()
                .forEach(System.out::println);

        StepVerifier.create(
                Flux.just("flux", "mono")
                        .flatMap(s -> Flux.fromArray(s.split("\\s*"))   // 1
                                .delayElements(Duration.ofMillis(100))) // 2
                        .doOnNext(System.out::print)) // 3
                .expectNextCount(8) // 4
                .verifyComplete();

    }
}
