package examples;
import java.util.*;
import java.util.function.*;
public class Java8Examples {
    public void lambda() {
        Runnable r = () -> System.out.println("lambda");
        Function<String,Integer> f = s -> s.length();
        r.run();
    }
    public void methodRef() {
        List<String> l = Arrays.asList("a","b");
        l.forEach(System.out::println);
    }
    public void defaultMethod() {
        MyInterface i = new MyImpl();
        i.defaultMethod();
    }
    interface MyInterface { default void defaultMethod() { System.out.println("default"); } }
    static class MyImpl implements MyInterface {}
    public void streamApi() {
        List<String> l = Arrays.asList("a","bb");
        long c = l.stream().filter(s -> s.length()>1).count();
    }
}
