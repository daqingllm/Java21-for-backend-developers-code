package chapter5;

public class TextBlockDemo {
    public static void main(String[] args) {
        String query = """
               SELECT "EMP_ID", "LAST_NAME" FROM "EMPLOYEE_TB"
               WHERE "CITY" = 'INDIANAPOLIS'
               ORDER BY "EMP_ID", "LAST_NAME";
               """;
        System.out.println(query);

        String html = """
                <html>
                    <body>
                        <p>Hello, world</p>
                    </body>
                </html>
                """;
        System.out.println(html);

        String code =
                """
                String text = \"""
                    A text block inside a text block
                \""";
                """;
        System.out.println(code);

        String tutorial1 =
                """
                A common character
                in Java programs
                is \"""";
        System.out.println(tutorial1);

        String tutorial2 =
                """
                The empty string literal
                is formed from " characters
                as follows: \"\"""";
        System.out.println(tutorial2);

        System.out.println("""
                     1 "
                     2 ""
                     3 ""\"
                     4 ""\""
                     5 ""\"""
                     6 ""\"""\"
                     7 ""\"""\""
                     8 ""\"""\"""
                     9 ""\"""\"""\"
                    10 ""\"""\"""\""
                    11 ""\"""\"""\"""
                    12 ""\"""\"""\"""\"
                """);

        String text = """
                Lorem ipsum dolor sit amet, consectetur adipiscing \
                elit, sed do eiusmod tempor incididunt ut labore \
                et dolore magna aliqua.\
                """;
        System.out.println(text);

        String colors = """
                red  \s
                green\s
                blue \s
                """;
        System.out.println(colors);

        String type = "int";
        // String::replace
        String code1 = """
              public void print($type o) {
                  System.out.println(Objects.toString(o));
              }
              """.replace("$type", type);
        System.out.println(code1);

        // String::format
        String code2 = String.format("""
              public void print(%s o) {
                  System.out.println(Objects.toString(o));
              }
              """, type);
        System.out.println(code2);
    }
}
