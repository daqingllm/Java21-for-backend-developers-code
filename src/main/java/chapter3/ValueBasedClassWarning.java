package chapter3;

public class ValueBasedClassWarning {

    public static void main(String[] args) {
        Double d = new Double(20.0);
        synchronized (d) {}
    }
}