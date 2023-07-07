package chapter9;

public class StackWalkerDemo {

    public static void main(String[] args) {
        func1();
        func2();
    }
    public static void inv() {
        StackWalker.StackFrame frame = StackWalker.getInstance().walk(stackFrameStream ->
                stackFrameStream.filter(f -> !f.getClassName().equals("org.example.Main") ||
                        !f.getMethodName().equals("inv")).findFirst().get());
        System.out.println(frame.getClassName() + ":" + frame.getMethodName());
    }
    public static void func1() {
        inv();
    }
    public static void func2() {
        inv();
    }
}
