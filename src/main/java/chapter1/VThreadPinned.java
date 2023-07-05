package chapter1;

import jdk.jfr.consumer.RecordingStream;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class VThreadPinned {

    public static void main(String[] args) throws InterruptedException {
        Thread.ofVirtual().name("VirtualThreadPinnedMonitor").start(() -> {
            try (var rs = new RecordingStream()) {
                rs.enable("jdk.VirtualThreadPinned").withoutThreshold();
                rs.onEvent("jdk.VirtualThreadPinned", System.out::println);
                rs.start();
            }
        });


        while (true) {
            Thread.startVirtualThread(() -> {
                synchronized (new Object()) {
                    try {
                        Thread.sleep(Duration.ofSeconds(1));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
