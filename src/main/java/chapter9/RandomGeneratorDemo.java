package chapter9;

import java.util.Comparator;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomGeneratorDemo {

    public static void main(String[] args) {
        list();

        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        randomGenerator.nextInt(100);

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        RandomGenerator.SplittableGenerator splittableGenerator = new SplittableRandom();
        splittableGenerator.splits(20).forEach(generator -> {
            executorService.submit(() -> generator.nextInt(100));
        });
    }

    private static void list() {
        RandomGeneratorFactory.all()
                .sorted(Comparator.comparing(RandomGeneratorFactory::name))
                .forEach(factory -> System.out.printf("%s\t%s\t%s\t%s%n",
                        factory.group(),
                        factory.name(),
                        factory.isJumpable(),
                        factory.isSplittable()));
    }
}
