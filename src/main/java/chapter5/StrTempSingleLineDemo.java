package chapter5;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StrTempSingleLineDemo {
    public static void main(String[] args) {
        demo1();
        demo2();
        demo3();
        demo4();
        demo5();
    }

    private static void demo1() {
        var x = 10;
        var y = 20;
        String s = STR."\{y} plus \{x} equals \{x + y}";
        System.out.println(s);
    }

    private static void demo2() {
        // 嵌入表达式为字符串
        String firstName = "Bill";
        String lastName  = "Duck";
        String fullName  = STR."\{firstName} \{lastName}";
        System.out.println(fullName);
        String sortName  = STR."\{lastName}, \{firstName}";
        System.out.println(sortName);

        record Req(String date, String time, String ipAddress) {}
        Req req = new Req("2021-09-01", "12:00", "1.1.1.1");
        // 嵌入表达式可以调用方法或取字段
        String s = STR."You have a \{getOfferType()} waiting for you!";
        System.out.println(s);
        String t = STR."Access at \{req.date} \{req.time} from \{req.ipAddress}";
        System.out.println(t);
    }

    private static String getOfferType() {
        return "special offer";
    }

    private static void demo3() {
        String filePath = "tmp.dat";
        File file     = new File(filePath);
        String old = "The file " + filePath + " " + (file.exists() ? "does" : "does not") + " exist";

        // \{file.exists() ? "does" : "does not"} 这段是嵌入表达式
        String msg = STR."The file \{filePath} \{file.exists() ? "does" : "does not"} exist";
        System.out.println(msg);
    }

    private static void demo4() {
        String time = STR."The time is \{
            // The java.time.format package is very useful
            DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .format(LocalTime.now())
        } right now";
        System.out.println(time);
    }

    private static void demo5() {
        String[] fruit = {"apple", "banana", "cherry"};
        String s = STR."\{fruit[0]}, \{
            STR."\{fruit[1]}, \{fruit[2]}"
        }";
        System.out.println(s);
    }
}
