import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import reactor.core.publisher.Flux;

/**
 * // 订阅并触发数据流
 * subscribe();
 * // 订阅并指定对正常数据元素如何处理
 * subscribe(Consumer<? super T> consumer);
 * // 订阅并定义对正常数据元素和错误信号的处理
 * subscribe(Consumer<? super T> consumer,
 *           Consumer<? super Throwable> errorConsumer);
 * // 订阅并定义对正常数据元素、错误信号和完成信号的处理
 * subscribe(Consumer<? super T> consumer,
 *           Consumer<? super Throwable> errorConsumer,
 *           Runnable completeConsumer);
 * // 订阅并定义对正常数据元素、错误信号和完成信号的处理，以及订阅发生时的处理逻辑
 * subscribe(Consumer<? super T> consumer,
 *           Consumer<? super Throwable> errorConsumer,
 *           Runnable completeConsumer,
 *           Consumer<? super Subscription> subscriptionConsumer);
 */
public class FluxCreator {
    public static void main(final String[] args) {
        simple();
        generate();
        create();
    }

    private static void simple() {
        System.out.println("*******");
        Flux.empty().subscribe(System.out::println);
        Flux.error(new Exception("some error")).subscribe();
        Flux.just("Hello", "World").subscribe(System.out::println);
        Flux.range(1, 10).subscribe(System.out::println);
        Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);

        Integer[] array = new Integer[]{1, 2, 3, 4, 5, 6};

        Flux.fromArray(array).subscribe(System.out::println);
        Flux.fromIterable(Arrays.asList(array)).subscribe(System.out::println);
        Flux.fromStream(Arrays.stream(array)).subscribe(System.out::println);
    }

    private static void generate() {
        System.out.println("*******");
        Flux.generate(sink -> {
            sink.next("Hello");
            sink.complete();
        }).subscribe(System.out::println);

        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);
    }

    private static void create() {
        System.out.println("*******");
        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }
}
