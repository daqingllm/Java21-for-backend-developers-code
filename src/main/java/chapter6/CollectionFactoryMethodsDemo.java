package chapter6;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionFactoryMethodsDemo {

    public static void main(String[] args) {
        var list = List.of("a", "b", "c");
        System.out.println(list);

        var set = Set.of("a", "b", "c");
        System.out.println(set);

        var map = Map.of("a", 1, "b", 2, "c", 3);
        System.out.println(map);

        var map2 = Map.ofEntries(
                Map.entry("a", 1),
                Map.entry("b", 2),
                Map.entry("c", 3)
        );
        System.out.println(map2);
    }
}
