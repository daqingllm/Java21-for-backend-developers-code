package chapter2;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class FlowDemo {

    public static void main(String[] args) throws InterruptedException {
        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        EndSubscriber<String> subscriber = new EndSubscriber<>();
        publisher.subscribe(subscriber);
        List<String> items = List.of("1", "x", "2", "x", "3", "x");

        // when
        System.out.println(publisher.getNumberOfSubscribers());
        items.forEach(publisher::submit);
        publisher.close();

        // then
        Thread.sleep(Duration.ofSeconds(1));
        subscriber.consumedElements.forEach(System.out::println);
    }

    public static class EndSubscriber<T> implements Flow.Subscriber<T> {
        private Flow.Subscription subscription;
        public List<T> consumedElements = new LinkedList<>();

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1); // 每次消费1个消息
        }

        @Override
        public void onNext(T item) {
            System.out.println("Got : " + item);
            consumedElements.add(item);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("Done");
        }
    }
}
