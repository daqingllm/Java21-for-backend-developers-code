package chapter5;

import com.alibaba.fastjson.JSONObject;

import java.sql.*;
import java.util.*;

import static java.lang.StringTemplate.RAW;

public class StrTempCustomDemo {

    public static void main(String[] args) {
        raw();
        inner();
        json();
        jsonValidate();
        db();
        local();
    }

    private static void raw() {
        int x = 10, y = 20;
        StringTemplate st = RAW."\{x} plus \{y} equals \{x + y}";
        System.out.println(st.toString());

        List<String> fragments = st.fragments();
        String result = String.join("\\{}", fragments);
        System.out.println(result);

        List<Object> values = st.values();
        System.out.println(values);

        int j = 20;
        for (int i = 0; i < 3; i++) {
            StringTemplate str = RAW."\{i} plus \{j} equals \{i + j}";
            System.out.println(str);
        }
    }

    private static void inner() {
        var INTER = StringTemplate.Processor.of((StringTemplate st) -> {
            StringBuilder sb = new StringBuilder();
            Iterator<String> fragIter = st.fragments().iterator();
            for (Object value : st.values()) {
                sb.append(fragIter.next());
                sb.append(value);
            }
            sb.append(fragIter.next());
            return sb.toString();
        });

        int x = 10, y = 20;
        String s = INTER."\{x} plus \{y} equals \{x + y}";
        System.out.println(s);
    }

    private static void json() {
        var JSON = StringTemplate.Processor.of(
                (StringTemplate st) -> JSONObject.parseObject(st.interpolate())
        );

        String name    = "Joan Smith";
        String phone   = "555-123-4567";
        String address = "1 Maple Drive, Anytown";
        JSONObject doc = JSON."""
        {
            "name":    "\{name}",
            "phone":   "\{phone}",
            "address": "\{address}"
        }
        """;
        System.out.println(doc);
    }

    private static void jsonValidate() {
        class JSONException extends RuntimeException {
            public JSONException(String message) {
                super(message);
            }
        }
        StringTemplate.Processor<JSONObject, JSONException> JSON_VALIDATE =
                (StringTemplate st) -> {
                    String quote = "\"";
                    List<Object> filtered = new ArrayList<>();
                    for (Object value : st.values()) {
                        if (value instanceof String str) {
                            if (str.contains(quote)) {
                                throw new JSONException("Injection vulnerability");
                            }
                            filtered.add(quote + str + quote);
                        } else if (value instanceof Number ||
                                value instanceof Boolean) {
                            filtered.add(value);
                        } else {
                            throw new JSONException("Invalid value type");
                        }
                    }
                    String jsonSource =
                            StringTemplate.interpolate(st.fragments(), filtered);
                    return JSONObject.parseObject(jsonSource);
                };

        String name    = "Joan Smith";
        String phone   = "555-123-4567";
        String address = "1 Maple Drive, Anytown";
        try {
            JSONObject doc = JSON_VALIDATE."""
            {
                "name":    \{name},
                "phone":   \{phone},
                "address": \{address}
            }
            """;
            System.out.println(doc);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private static void db() {
        record QueryProcessor(Connection conn)
                implements StringTemplate.Processor<ResultSet, SQLException> {

            public ResultSet process(StringTemplate st) throws SQLException {
                // 1. Replace StringTemplate placeholders with PreparedStatement placeholders
                String query = String.join("?", st.fragments());

                // 2. Create the PreparedStatement on the connection
                PreparedStatement ps = conn.prepareStatement(query);

                // 3. Set parameters of the PreparedStatement
                int index = 1;
                for (Object value : st.values()) {
                    switch (value) {
                        case Integer i -> ps.setInt(index++, i);
                        case Float f   -> ps.setFloat(index++, f);
                        case Double d  -> ps.setDouble(index++, d);
                        case Boolean b -> ps.setBoolean(index++, b);
                        default        -> ps.setString(index++, String.valueOf(value));
                    }
                }

                // 4. Execute the PreparedStatement, returning a ResultSet
                return ps.executeQuery();
            }
        }

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/test?serverTimezone=UTC",
                    "root",
                    "123456"
            );

            StringTemplate.Processor<ResultSet, SQLException> DB = new QueryProcessor(conn);
            String name = "Smith";
            ResultSet rs = DB."SELECT * FROM Person p WHERE p.last_name = \{name}";
            System.out.println(rs.first());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void local() {
        Locale thaiLocale = Locale.forLanguageTag("th-TH-u-nu-thai");
        FormatProcessor THAI = FormatProcessor.create(thaiLocale);
        for (int i = 1; i <= 10000; i *= 10) {
            String s = THAI. "This answer is %5d\{i}";
            System.out.println(s);
        }
    }
}
