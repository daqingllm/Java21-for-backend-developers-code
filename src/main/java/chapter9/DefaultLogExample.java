package chapter9;

public class DefaultLogExample {
    private static final System.Logger LOGGER = System.getLogger(DefaultLogExample.class.getName());
    public static void main(String[] args) {
        LOGGER.log(System.Logger.Level.DEBUG, "Debug messsage.");
        LOGGER.log(System.Logger.Level.INFO, "Hello, World!");
        LOGGER.log(System.Logger.Level.ERROR, LOGGER.getClass().getSimpleName());
    }
}
