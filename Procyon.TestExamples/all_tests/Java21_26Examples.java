package examples;
public class Java21_26Examples {
    public record User(String name, int age) {}
    public void recordPattern(Object o) {
        if (o instanceof User(String name, int age)) {
            System.out.println(name + age);
        }
        String s = switch (o) {
            case User(String n, int a) when a > 18 -> "adult:" + n;
            case User(String n, int a) -> "young:" + n;
            case String str when str.length()>2 -> str;
            case String _ -> "empty";
            default -> "other";
        };
    }
    public void unnamedVar() {
        try { var x = 1; System.out.println(x); } catch (Exception _) { System.out.println("err"); }
    }
    public void stringConcat() {
        String a = "hello", b = "world";
        String c = a + " " + b + "!";
        System.out.println(c);
    }
    public void tryFinallyDefAssign() {
        boolean result = false;
        try { result = true; System.out.println("try"); }
        catch (Exception e) { result = false; }
        finally { System.out.println(result); }
    }
    public sealed interface Expr permits Add, Const {}
    public record Add(Expr l, Expr r) implements Expr {}
    public record Const(int v) implements Expr {}
}
