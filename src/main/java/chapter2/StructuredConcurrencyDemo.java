package chapter2;

import com.sun.management.HotSpotDiagnosticMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class StructuredConcurrencyDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(Duration.ofMillis(500));
                HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
                hotSpotDiagnosticMXBean.dumpThreads(System.getProperty("user.dir") + "/threads.json", HotSpotDiagnosticMXBean.ThreadDumpFormat.JSON);
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        var a = new AtomicInteger(0);
        var begin = System.currentTimeMillis();
        // 创建一个ShutdownOnFailure策略的StructuredTaskScope
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<StructuredTaskScope.Subtask<Integer>> subtasks = new ArrayList<>();
            // 启动100万个执行sleep 1s的子任务
            for (int i = 0; i < 1_000_000; i++) {
                subtasks.add(scope.fork(() -> {
                    int x = a.addAndGet(1);
//                    if (x == 10) {
//                        Thread.sleep(Duration.ofMillis(100));
//                        throw new RuntimeException();
//                    }
                    Thread.sleep(Duration.ofSeconds(1));
                    return x;
                }));
            }
            // 等待子任务执行完毕，任意子任务执行失败则抛出异常
//            scope.joinUntil(Instant.ofEpochMilli(100)).throwIfFailed();
            scope.join().throwIfFailed();
            // 打印结果
            for (var task : subtasks) {
                var i = task.get();
                if (i % 10000 == 0) {
                    System.out.print(i + " ");
                }
            }
        } finally {
            // 打印总耗时
            System.out.println("Exec finished.");
            System.out.printf("Exec time: %dms.%n", System.currentTimeMillis() - begin);
        }
    }
}
