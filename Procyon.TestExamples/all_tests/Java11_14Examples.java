package examples;
public class Java11_14Examples {
    public void varLambda() {
        java.util.function.BiFunction<String,String,String> f = (var a, var b) -> a + b;
    }
    public void switchExpression(int x) {
        String s = switch (x) {
            case 1 -> "one";
            case 2 -> "two";
            default -> "other";
        };
        System.out.println(s);
    }
    public void switchYield(int x) {
        String s = switch (x) {
            case 1: yield "one";
            default: yield "other";
        };
    }
    public void textBlock() {
        String html = """
            <html>
                <body>hello</body>
            </html>
            """;
        System.out.println(html);
    }
    public void helpText() {
        var txt = """
            line1
            line2
            """;
    }
}
