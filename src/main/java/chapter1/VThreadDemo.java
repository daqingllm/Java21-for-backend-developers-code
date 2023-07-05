package chapter1;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class VThreadDemo {

    public static void main(String[] args) {
        var a = new AtomicInteger(0);
        // 创建一个固定200个线程的线程池
//        try (var vs = Executors.newFixedThreadPool(200)) {
        try (var vs = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> futures = new ArrayList<>();
            var begin = System.currentTimeMillis();
            // 向线程池提交1000个sleep 1s的任务
            for (int i=0; i<1_000_000; i++) {
                var future = vs.submit(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    return a.addAndGet(1);
                });
                futures.add(future);
            }
            // 获取这100万个任务的结果
            for (var future : futures) {
                var i = future.get();
                if (i % 10000 == 0) {
                    System.out.print(i + " ");
                }
            }
            // 打印总耗时
            System.out.println("Exec finished.");
            System.out.printf("Exec time: %dms.%n", System.currentTimeMillis() - begin);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
