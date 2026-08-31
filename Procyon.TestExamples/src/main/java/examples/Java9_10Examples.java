package examples;
import java.util.*;
public class Java9_10Examples {
    public void varExample() {
        var list = List.of("a","b");
        var s = "hello";
        var map = Map.of("k", 1);
        System.out.println(list + s + map);
    }
    public void tryWithResources() throws Exception {
        try (var r = new java.io.StringReader("hi")) { System.out.println(r.read()); }
    }
    public void diamond() {
        List<String> l = new ArrayList<>();
    }
    public void privateInterfaceMethod() {
        System.out.println(PrivateInterface.staticMethod());
    }
    interface PrivateInterface {
        private static String staticMethod() { return "private"; }
        default void foo() { bar(); }
        private void bar() { System.out.println("bar"); }
    }
}
