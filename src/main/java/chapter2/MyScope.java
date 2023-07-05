package chapter2;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

class MyScope<T> extends StructuredTaskScope<T> {

    private final Queue<T> results = new ConcurrentLinkedQueue<>();

    MyScope() { super(null, Thread.ofVirtual().factory()); }

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        if (subtask.state() == Subtask.State.SUCCESS)
            results.add(subtask.get());
    }

    @Override
    public MyScope<T> join() throws InterruptedException {
        super.join();
        return this;
    }

    public Stream<T> results() {
        super.ensureOwnerAndJoined();
        return results.stream();
    }
}
