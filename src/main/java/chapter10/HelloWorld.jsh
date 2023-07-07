// 使用 jshell HelloWorld.jsh 命令运行
class Person {
    private String name;
    public Person(String name) {
        this.name = name;
    }
    public void sayHello() {
        System.out.println("Hello World! " + name);
    }
}
Person person = new Person("Liu liming");
person.sayHello();