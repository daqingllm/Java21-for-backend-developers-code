package chapter4;

public class SwitchExpression {
    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY,
    }

    public static void main(String[] args) {

    }

    private static void switch1(Day day) {
        switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> System.out.println(6);
            case TUESDAY                -> System.out.println(7);
            case THURSDAY, SATURDAY     -> System.out.println(8);
            case WEDNESDAY              -> System.out.println(9);
        }
    }

    private static void switch2(Day day) {
        switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> {
                int temp = 1;
            }
            case TUESDAY                -> {
                int temp = 2;
            }
            case THURSDAY, SATURDAY     -> {
                int temp = 3;
            }
            default -> {
                int temp = 4;
            }
        }
    }

    private static void switch3(Day day) {
        int numLetters = switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> 6;
            case TUESDAY                -> 7;
            case THURSDAY, SATURDAY     -> 8;
            case WEDNESDAY              -> 9;
            default 					-> throw new IllegalStateException("Wat: " + day);
        };
    }

    private static void switch4(Day day) {
        int j = switch (day) {
            case MONDAY  -> 0;
            case TUESDAY -> 1;
            default      -> {
                int k = day.toString().length();
                int result = f(k);
                yield result;
            }
        };
    }

    private static int f(int k) {
        return k;
    }

    private static void switch5(String s) {
        int result = switch (s) {
            case "Foo":
                yield 1;
            case "Bar":
                yield 2;
            default:
                System.out.println("Neither Foo nor Bar, hmmm...");
                yield 0;
        };
    }
}
