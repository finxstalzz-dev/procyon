package examples;
public class Java15_17Examples {
    public record Point(int x, int y) {
        public Point { if (x<0) throw new IllegalArgumentException(); }
        public int sum() { return x + y; }
    }
    public sealed interface Shape permits Circle, Rectangle {}
    public static final class Circle implements Shape { double r; }
    public static final class Rectangle implements Shape { double w,h; }
    public void sealedTest(Shape s) {
        if (s instanceof Circle c) { System.out.println(c.r); }
        else if (s instanceof Rectangle r) { System.out.println(r.w); }
    }
    public void patternSwitch(Object o) {
        String res = switch (o) {
            case String s -> "str:" + s;
            case Integer i -> "int:" + i;
            case null -> "null";
            default -> "other";
        };
    }
    public void textBlock2() {
        String s = """
            hello
            """;
    }
}
