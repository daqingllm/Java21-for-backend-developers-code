package chapter9;

import java.io.*;
import java.util.function.BinaryOperator;

public class DeserializationFilterDemo {

    public static void main(String[] args) {
        // 动态设置反序列化过滤器factory
        var filterInThread = new FilterInThread();
        ObjectInputFilter.Config.setSerialFilterFactory(filterInThread);

        // 允许 chapter9.* 包下的class和java.base模块下的class被反序列化，其他class反序列化都会被拒绝
        // 修改 chapter9.* 来测试
        var filter = ObjectInputFilter.Config.createFilter("chapter9.*;java.base/*;!*");
        filterInThread.doWithSerialFilter(filter, () -> {
            var test = new Test("1");
            serializeObject(test, "test.ser");
            var test2 = deserializeObject("test.ser");
            System.out.println(test2);
        });
    }

    // 序列化对象到文件
    private static void serializeObject(Object object, String fileName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            fileOutputStream.close();
            System.out.println("Object serialized and saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从文件反序列化对象
    private static Test deserializeObject(String fileName) {
        Test person = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            person = (Test) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            System.out.println("Object deserialized from " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return person;
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

    static class Test implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;

        public Test(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Test{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
