import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class Error {
    public static void main(String[] args) {
        subscribe();
        onErrorReturn();
        onErrorResume();
        onErrorMap();
        retry();
        doOnError();
        tryWithResource();
        doFinally();
    }

    private static void subscribe() {
        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .subscribe(System.out::println, System.err::println);
    }

    private static void onErrorReturn() {
        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .onErrorReturn(0)
                .subscribe(System.out::println);
    }

    private static void onErrorResume() {
        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException) {
                        return Mono.just(0);
                    } else if (e instanceof IllegalArgumentException) {
                        return Mono.just(-1);
                    }
                    return Mono.empty();
                })
                .subscribe(System.out::println);
    }

    private static void onErrorMap() {
        Flux.just(1)
                .flatMap(k -> Flux.interval(Duration.ofMillis(100)).take(k))   // 1
                .onErrorMap(original -> new IllegalStateException("SLA exceeded", original)); // 2
    }

    private static void retry() {
        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .retry(1)
                .subscribe(System.out::println);
    }

    private static void doOnError() {
        Flux.just(1, 2)
                .flatMap(k -> Flux.interval(Duration.ofMillis(100)).take(k))
                .doOnError(e -> {
//                    log
                })
                .onErrorResume(e -> Mono.just(0L));
    }

    private static void tryWithResource() {
        Flux.using(
                () -> Collections.singletonList(1),    // 1
                resource -> Flux.just(resource.get(0)),   // 2
                List::clear   // 3
        );
    }

    private static void doFinally() {
        LongAdder statsCancel = new LongAdder();    // 1

        Flux<String> flux =
                Flux.just("foo", "bar")
                        .doFinally(type -> {
                            if (type == SignalType.CANCEL)  // 2
                                statsCancel.increment();  // 3
                        })
                        .take(1);   // 4
    }


}