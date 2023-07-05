package chapter6;

import java.util.*;

public class SequencedCollectionDemo {

    public static void main(String[] args) {
        List.of(1,2,3,4,5,6,7,8,9).reversed().forEach(System.out::print);
        System.out.println("======");

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.removeFirst();
        list.addFirst(3);
        System.out.println(list);
        System.out.println("======");

        SequencedSet<Integer> set = new LinkedHashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        System.out.println(set);
        set.addFirst(3);
        System.out.println(set);
        System.out.println("======");

        SequencedMap<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(1, true);
        map.put(2, false);
        map.put(3, true);
        System.out.println(map);
        map.putFirst(3, false);
        System.out.println(map);
        System.out.println("======");

        var unmodifiableSequencedMap = Collections.unmodifiableSequencedMap(map);
        System.out.println(unmodifiableSequencedMap);
    }
}
