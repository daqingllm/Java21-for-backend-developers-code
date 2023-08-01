package chapter1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class OverloadRejectedVirtualExecutor implements ExecutorService {

    private static final Logger log = LoggerFactory.getLogger(OverloadRejectedVirtualExecutor.class);

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private volatile boolean overload = false;
    private volatile boolean terminated = false;

    public OverloadRejectedVirtualExecutor() {

        Thread.ofVirtual().name("OverloadMonitor").start(() -> {
            int hb = 0, cpuOverloadCount = 0, memoryOverloadCount = 0;
            while (!terminated) {
                try {
                    Thread.sleep(Duration.ofSeconds(1));
                } catch (InterruptedException e) {
                    log.error("overload rejected virtual executor sleep error", e);
                    overload = false;
                }
                if (++hb >= 60) {
                    hb = 0;
                }

                // 连续3s CPU Load > 0.99，设置为过载
                var operatingSystem = ManagementFactory.getOperatingSystemMXBean();
                if (operatingSystem instanceof com.sun.management.OperatingSystemMXBean osBean) {
                    double cpuLoad = osBean.getCpuLoad();
                    double processCpuLoad = osBean.getProcessCpuLoad();
                    if (processCpuLoad > 0.99) {
                        overload = ++cpuOverloadCount > 2;
                        log.info("CPU_Load: {}% , Process_CPU_Load: {}%", cpuLoad * 100, processCpuLoad * 100);
                    } else {
                        cpuOverloadCount = 0;
                        overload = false;
                    }
                    if (overload) {
                        continue;
                    }
                }

                // 连续3s Memory Used > 99%，设置为过载
                var usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                var maxMemory = Runtime.getRuntime().maxMemory();
                if (100d * usedMemory / maxMemory > 99) {
                    overload = ++memoryOverloadCount > 2;
                } else {
                    memoryOverloadCount = 0;
                    overload = false;
                }
                if (overload) {
                    continue;
                }
            }
        });
        log.info("dubbo virtual thread executor init.");
    }

    private void reject() {
        throw new RejectedExecutionException("Dubbo server is overload now!");
    }

    @Override
    public void execute(Runnable command) {
        if (overload) {
            reject();
        }
        executor.execute(command);
    }

    @Override
    public void shutdown() {
        terminated = true;
        executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        terminated = true;
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        terminated = true;
        return executor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (overload) {
            reject();
        }
        return executor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (overload) {
            reject();
        }
        return executor.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (overload) {
            reject();
        }
        return executor.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (overload) {
            reject();
        }
        return executor.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (overload) {
            reject();
        }
        return executor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        if (overload) {
            reject();
        }
        return executor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (overload) {
            reject();
        }
        return executor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void close() {
        terminated = true;
        executor.close();
    }
}
