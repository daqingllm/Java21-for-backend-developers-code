package chapter2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

public class ScopedValuesDemo {

    static int ADMIN = 0, GUEST = 1;

    public static void main(String[] args) {
        Server.serve(new Request(true), new Response());
    }

    class Server {
        final static ScopedValue<Principal> PRINCIPAL = ScopedValue.newInstance();

        static void serve(Request request, Response response) {
            // 将请求权限记录在PRINCIPAL中，调用Application.handle处理请求
            var level     = (request.isAdmin() ? ADMIN : GUEST);
            var principal = new Principal(level);
            ScopedValue.where(PRINCIPAL, principal)
                    .run(() -> Application.handle(request, response));
        }
    }

    static class Application {
        static void handle(Request request, Response response) {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                // 开启子线程执行findUser()
                Supplier<String> user  = scope.fork(() -> findUser());
                Supplier<Integer> order = scope.fork(() -> fetchOrder());
                try {
                    scope.join().throwIfFailed();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                response.user = user.get();
                response.order = order.get();
            }
        }

        static Integer fetchOrder() {
            return 1;
        }

        // findUser方法调用了DBAccess.open()
        static String findUser() {
            return DBAccess.open();
        }
    }

    class DBAccess {
        static String open() {
            // 获取PRINCIPAL值，判断请求权限，权限不足抛异常
            var principal = Server.PRINCIPAL.get();
            if (!principal.canOpen()) throw new InvalidPrincipalException();
            return "user";
        }
    }

    record Principal(int level) {
        boolean canOpen() {
            return level == 0;
        }
    }
    record Request(boolean isAdmin) {}
    static class Response {
        String user;
        int order;
    }
    static class InvalidPrincipalException extends RuntimeException {}
}
