package chapter2;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyScopeDemo {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> result = allSuccessful(Stream.of(1, 2, 3, 4).map(ExecuteAllStrategyDemo.MyTask::new).collect(Collectors.toList()));
        result.forEach(System.out::println);
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

    static <T> List<T> allSuccessful(List<Callable<T>> tasks) throws InterruptedException {
        try (var scope = new MyScope<T>()) {
            for (var task : tasks) scope.fork(task);
            return scope.join()
                    .results().toList();
        }
    }
}
