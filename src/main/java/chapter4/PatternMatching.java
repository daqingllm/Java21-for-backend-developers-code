package chapter4;

public class PatternMatching {

    public static void main(String[] args) {
        printXCoordOfUpperLeftPointWithPatterns(new Rectangle(new ColoredPoint(new Point(23, 42), Color.RED), new ColoredPoint(new Point(42, 23), Color.BLUE)));
        testStringEnhanced("ok");
    }

    public void onlyForStrings(Object o) throws RuntimeException {
        if (!(o instanceof String s))
            throw new RuntimeException();
        // s 在作用域内
        System.out.println(s);
    }

    public final class CaseInsensitiveString {
        private final String s;
        public CaseInsensitiveString(String s){
            this.s = s;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof CaseInsensitiveString cis) &&
                    cis.s.equalsIgnoreCase(s);
        }
    }

    record Point(int x, int y) {}

    static void printSum(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            System.out.println(x+y);
        }
    }

    enum Color { RED, GREEN, BLUE }
    record ColoredPoint(Point p, Color c) {}
    record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {}

    static void printXCoordOfUpperLeftPointWithPatterns(Rectangle r) {
        if (r instanceof Rectangle(ColoredPoint(Point(var x, _), _), _)) {
            System.out.println("Upper-left corner: " + x);
        }
    }

    static String formatterPatternSwitch(Object obj) {
        return switch (obj) {
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case Double d  -> String.format("double %f", d);
            case String s  -> String.format("String %s", s);
            default        -> obj.toString();
        };
    }

    static void testFooBarNew(String s) {
        switch (s) {
            case null         -> System.out.println("Oops");
            case "Foo", "Bar" -> System.out.println("Great");
            default           -> System.out.println("Ok");
        }
    }

    static void typeTester(Object obj) {
        switch (obj) {
            case null     -> System.out.println("null");
            case String s -> System.out.println("String");
            // enum type
            case Color c  -> System.out.println("Color: " + c.toString());
            // record type
            case Point p  -> System.out.println("Record class: " + p.toString());
            case int[] ia -> System.out.println("Array of ints of length" + ia.length);
            default       -> System.out.println("Something else");
        }
    }

    static void testStringEnhanced(String response) {
        switch (response) {
            case null -> { }
            case "y", "Y" -> {
                System.out.println("You got it");
            }
            case "n", "N" -> {
                System.out.println("Shame");
            }
            case String s
                    when s.equalsIgnoreCase("YES") -> {
                System.out.println("You got it");
            }
            case String s
                    when s.equalsIgnoreCase("NO") -> {
                System.out.println("Shame");
            }
            case String _ -> {
                System.out.println("Sorry?");
            }
        }
    }
}
