package chapter9;

import java.io.ObjectInputFilter;
import java.util.function.BinaryOperator;

public class DeserializationFilterDemo {

    public static void main(String[] args) {
        // 动态设置反序列化过滤器factory
        var filterInThread = new FilterInThread();
        ObjectInputFilter.Config.setSerialFilterFactory(filterInThread);

        // 允许 example.* 包下的class和java.base模块下的class被反序列化，其他class反序列化都会被拒绝
        var filter = ObjectInputFilter.Config.createFilter("example.*;java.base/*;!*");
        filterInThread.doWithSerialFilter(filter, () -> {
//            byte[] bytes = ...;
//            var o = deserializeObject(bytes);
        });
    }

    public static class FilterInThread implements BinaryOperator<ObjectInputFilter> {
        private final ThreadLocal<ObjectInputFilter> filterThreadLocal = new ThreadLocal<>();
        public FilterInThread() {}

        // 过滤器创建方法，每次ObjectInputStream创建时都会被调用
        public ObjectInputFilter apply(ObjectInputFilter curr, ObjectInputFilter next) {
            if (curr == null) {
                var filter = filterThreadLocal.get();
                if (filter != null) {
                    filter = ObjectInputFilter.rejectUndecidedClass(filter);
                }
                if (next != null) {
                    filter = ObjectInputFilter.merge(next, filter);
                    filter = ObjectInputFilter.rejectUndecidedClass(filter);
                }
                return filter;
            } else {
                if (next != null) {
                    next = ObjectInputFilter.merge(next, curr);
                    next = ObjectInputFilter.rejectUndecidedClass(next);
                    return next;
                }
                return curr;
            }
        }

        public void doWithSerialFilter(ObjectInputFilter filter, Runnable runnable) {
            var prevFilter = filterThreadLocal.get();
            try {
                filterThreadLocal.set(filter);
                runnable.run();
            } finally {
                filterThreadLocal.set(prevFilter);
            }
        }
    }
}
