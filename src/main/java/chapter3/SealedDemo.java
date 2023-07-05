package chapter3;

public class SealedDemo {

    public abstract sealed class Shape
            permits Circle, Rectangle, Square, WeirdShape {}

    public final class Circle extends Shape {}

    public sealed class Rectangle extends Shape
            permits TransparentRectangle, FilledRectangle {}

    public final class TransparentRectangle extends Rectangle {}
    public final class FilledRectangle extends Rectangle {}

    public final class Square extends Shape {}

    public non-sealed class WeirdShape extends Shape {}

    public sealed interface Expr
            permits ConstantExpr, PlusExpr, TimesExpr, NegExpr {}

    public record ConstantExpr(int i)       implements Expr {}
    public record PlusExpr(Expr a, Expr b)  implements Expr {}
    public record TimesExpr(Expr a, Expr b) implements Expr {}
    public record NegExpr(Expr e)           implements Expr {}

    public sealed interface UserService permits UserServiceImpl {
        int getUserIdByPhone(String phone);
    }

    public final class UserServiceImpl implements UserService {
        @Override
        public int getUserIdByPhone(String phone) {
            return 0;
        }
    }
}
