package chapter2;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExecuteAllStrategyDemo {

    public static void main(String[] args) throws InterruptedException {
        List<Future<Integer>> futures = executeAll(Stream.of(1, 2, 3, 4).map(MyTask::new).collect(Collectors.toList()));
        futures.forEach(f -> {
            try {
                System.out.println(f.get());
            } catch (ExecutionException | InterruptedException e) {
                System.out.println(f.exceptionNow());
            }
        });
    }

    record MyTask(int id) implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            if (id % 2 == 0) {
                Thread.sleep(Duration.ofSeconds(1));
                return id;
            }
            throw new RuntimeException("exception: " + id);
        }
    }

    static <T> List<Future<T>> executeAll(List<Callable<T>> tasks)
            throws InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<? extends Supplier<Future<T>>> futures = tasks.stream()
                    .map(task -> asFuture(task))
                    .map(scope::fork)
                    .toList();
            scope.join();
            return futures.stream().map(Supplier::get).toList();
        }
    }

    static <T> Callable<Future<T>> asFuture(Callable<T> task) {
        return () -> {
            try {
                return CompletableFuture.completedFuture(task.call());
            } catch (Exception ex) {
                return CompletableFuture.failedFuture(ex);
            }
        };
    }
}
