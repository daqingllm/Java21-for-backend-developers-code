package chapter3;

import java.util.List;
import java.util.stream.Collectors;

public class VarDemo {

    public static void main(String[] args) {
        System.out.println(filterFirstNegNumber(List.of(1, 2, -4, -3, -5, 3)));
    }

    private static List<Integer> filterFirstNegNumber(List<Integer> list) {
        var ref = new Object() {
            boolean filtered = false;
        };
        return list.stream().filter(i -> {
            if (i >= 0) {
                return true;
            } else if (!ref.filtered) {
                ref.filtered = true;
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }
}
