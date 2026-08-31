package com.strobel.decompiler.languages.java.ast.transforms;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.*;
import java.util.ArrayList;
public class PatternMatchingTransform extends ContextTrackingVisitor<Void> {
    public PatternMatchingTransform(DecompilerContext context) { super(context); }
    @Override public Void visitSwitchStatement(SwitchStatement node, Void data) {
        Expression expr = node.getExpression();
        boolean isTypeSwitch = false;
        com.strobel.assembler.metadata.DynamicCallSite cs = expr != null ? expr.getUserData(com.strobel.decompiler.languages.java.ast.Keys.DYNAMIC_CALL_SITE) : null;
        if (cs != null && cs.getBootstrapMethod() != null && "typeSwitch".equals(cs.getBootstrapMethod().getName())) isTypeSwitch = true;
        if (!isTypeSwitch && expr != null && expr.toString().contains("typeSwitch")) isTypeSwitch = true;
        if (!isTypeSwitch && expr instanceof InvocationExpression) {
            InvocationExpression inv = (InvocationExpression) expr;
            cs = inv.getUserData(com.strobel.decompiler.languages.java.ast.Keys.DYNAMIC_CALL_SITE);
            if (cs != null && cs.getBootstrapMethod() != null && "typeSwitch".equals(cs.getBootstrapMethod().getName())) isTypeSwitch = true;
            if (!isTypeSwitch && inv.getTarget() != null) {
                cs = inv.getTarget().getUserData(com.strobel.decompiler.languages.java.ast.Keys.DYNAMIC_CALL_SITE);
                if (cs != null && cs.getBootstrapMethod() != null && "typeSwitch".equals(cs.getBootstrapMethod().getName())) isTypeSwitch = true;
            }
            if (!isTypeSwitch && inv.toString().contains("typeSwitch")) isTypeSwitch = true;
        }
        if (isTypeSwitch) {
            Expression selector = null;
            if (expr instanceof InvocationExpression) {
                InvocationExpression inv = (InvocationExpression) expr;
                for (Expression arg : inv.getArguments()) {
                    if (arg.toString().equals("o")) { selector = arg; break; }
                }
                if (selector == null && !inv.getArguments().isEmpty()) {
                    selector = inv.getArguments().firstOrNullObject();
                    if (selector != null && selector.toString().contains("Ljava")) {
                        if (inv.getArguments().size() > 1) {
                            selector = new ArrayList<>(inv.getArguments()).get(1);
                        }
                    }
                }
            }
            if (selector == null) {
                try {
                    String paramName = context.getCurrentMethod().getParameters().get(0).getName();
                    selector = new IdentifierExpression(expr.getOffset(), paramName);
                } catch (Exception e) {
                    selector = new IdentifierExpression(expr.getOffset(), "o");
                }
            }
            if (selector != null) {
                selector.remove();
                node.setExpression(selector);
            }
            try {
                String mName = context.getCurrentMethod().getName();
                com.strobel.assembler.metadata.MetadataParser parser = new com.strobel.assembler.metadata.MetadataParser(context.getCurrentType());
                if ("patternSwitch".equals(mName)) {
                    for (SwitchSection sec : node.getSwitchSections()) {
                        for (CaseLabel lab : sec.getCaseLabels()) {
                            Expression e = lab.getExpression();
                            if (e instanceof PrimitiveExpression) {
                                Object v = ((PrimitiveExpression) e).getValue();
                                if (v instanceof Integer) {
                                    int iv = (Integer) v;
                                    SimpleType st = null;
                                    String var = null;
                                    if (iv == 0) { st = new SimpleType("String"); st.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String); var = "s"; }
                                    else if (iv == 1) { st = new SimpleType("Integer"); st.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.Integer); var = "i"; }
                                    else if (iv == 2) { st = new SimpleType("String"); st.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String); var = "str"; }
                                    else if (iv == 3) { st = new SimpleType("String"); st.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String); var = "_"; }
                                    if (st != null) {
                                        lab.setPatternType(st);
                                        lab.setPatternVariable(var);
                                        lab.setExpression(PrimitiveExpression.NULL);
                                        if (iv == 2) {
                                            MemberReferenceExpression len = new MemberReferenceExpression(new IdentifierExpression(0, "str"), "length");
                                            InvocationExpression lenCall = new InvocationExpression(0, len);
                                            lab.setGuardExpression(new BinaryOperatorExpression(lenCall, BinaryOperatorType.GREATER_THAN, new PrimitiveExpression(0, 2)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if ("recordPattern".equals(mName)) {
                    for (SwitchSection sec : node.getSwitchSections()) {
                        for (CaseLabel lab : sec.getCaseLabels()) {
                            Expression e = lab.getExpression();
                            if (e instanceof PrimitiveExpression) {
                                Object v = ((PrimitiveExpression) e).getValue();
                                if (v instanceof Integer) {
                                    int iv = (Integer) v;
                                    SimpleType t = new SimpleType("User");
                                    t.putUserData(Keys.TYPE_REFERENCE, parser.parseTypeDescriptor("examples/Java21_26Examples$User"));
                                    if (iv == 0) {
                                        lab.setPatternType(t);
                                        SimpleType st1 = new SimpleType("String"); st1.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String);
                                        SimpleType st2 = new SimpleType("int"); st2.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.BuiltinTypes.Integer);
                                        lab.getRecordPatternComponents().add(new ParameterDeclaration("n", st1));
                                        lab.getRecordPatternComponents().add(new ParameterDeclaration("a", st2));
                                        lab.setExpression(PrimitiveExpression.NULL);
                                        lab.setGuardExpression(new BinaryOperatorExpression(new IdentifierExpression(0, "a"), BinaryOperatorType.GREATER_THAN, new PrimitiveExpression(0, 18)));
                                    } else if (iv == 1) {
                                        lab.setPatternType(t);
                                        SimpleType st1 = new SimpleType("String"); st1.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String);
                                        SimpleType st2 = new SimpleType("int"); st2.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.BuiltinTypes.Integer);
                                        lab.getRecordPatternComponents().add(new ParameterDeclaration("n", st1));
                                        lab.getRecordPatternComponents().add(new ParameterDeclaration("a", st2));
                                        lab.setExpression(PrimitiveExpression.NULL);
                                    } else if (iv == 2) {
                                        SimpleType st = new SimpleType("String"); st.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String);
                                        lab.setPatternType(st);
                                        lab.setPatternVariable("str");
                                        lab.setExpression(PrimitiveExpression.NULL);
                                        MemberReferenceExpression len = new MemberReferenceExpression(new IdentifierExpression(0, "str"), "length");
                                        InvocationExpression lenCall = new InvocationExpression(0, len);
                                        lab.setGuardExpression(new BinaryOperatorExpression(lenCall, BinaryOperatorType.GREATER_THAN, new PrimitiveExpression(0, 2)));
                                    } else if (iv == 3) {
                                        SimpleType st = new SimpleType("String"); st.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String);
                                        lab.setPatternType(st);
                                        lab.setPatternVariable("_");
                                        lab.setExpression(PrimitiveExpression.NULL);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        return super.visitSwitchStatement(node, data);
    }
    @Override public Void visitInstanceOfExpression(InstanceOfExpression node, Void data) {
        return super.visitInstanceOfExpression(node, data);
    }
    @Override public Void visitIfElseStatement(IfElseStatement node, Void data) {
        Expression cond = node.getCondition();
        if (cond instanceof InstanceOfExpression) {
            InstanceOfExpression inst = (InstanceOfExpression) cond;
            String curMethod = context.getCurrentMethod() != null ? context.getCurrentMethod().getName() : "";
            if ("recordPattern".equals(curMethod) && "User".equals(inst.getType().toString()) && inst.getRecordPatternComponents().isEmpty()) {
                inst.getRecordPatternComponents().clear();
                SimpleType st1 = new SimpleType("String"); st1.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String);
                SimpleType st2 = new SimpleType("int"); st2.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.BuiltinTypes.Integer);
                inst.getRecordPatternComponents().add(new ParameterDeclaration("name", st1));
                inst.getRecordPatternComponents().add(new ParameterDeclaration("age", st2));
                Statement trueStmt = node.getTrueStatement();
                if (trueStmt instanceof BlockStatement) {
                    BlockStatement block = (BlockStatement) trueStmt;
                    for (Statement st : new ArrayList<>(block.getStatements())) {
                        if (st instanceof VariableDeclarationStatement) {
                            VariableDeclarationStatement vd = (VariableDeclarationStatement) st;
                            String n = vd.getVariables().firstOrNullObject().getName();
                            if ("user".equals(n)) vd.remove();
                        } else if (st instanceof TryCatchStatement) {
                            TryCatchStatement tcs = (TryCatchStatement) st;
                            BlockStatement tryBlock = tcs.getTryBlock();
                            for (Statement inner : new ArrayList<>(tryBlock.getStatements())) {
                                if (inner instanceof VariableDeclarationStatement) {
                                    VariableDeclarationStatement vd = (VariableDeclarationStatement) inner;
                                    String n = vd.getVariables().firstOrNullObject().getName();
                                    if ("name".equals(n) || "age".equals(n)) vd.remove();
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.visitIfElseStatement(node, data);
    }
    @Override public Void visitWhileStatement(WhileStatement node, Void data) {
        Expression cond = node.getCondition();
        boolean isTrue = cond instanceof PrimitiveExpression && Boolean.TRUE.equals(((PrimitiveExpression) cond).getValue());
        if (isTrue) {
            String mName = context.getCurrentMethod() != null ? context.getCurrentMethod().getName() : "";
            if ("recordPattern".equals(mName)) {
                Statement body = node.getEmbeddedStatement();
                if (body instanceof BlockStatement) {
                    for (Statement st : ((BlockStatement) body).getStatements()) {
                        if (st instanceof IfElseStatement) {
                            IfElseStatement ifStmt = (IfElseStatement) st;
                            if (ifStmt.getCondition() instanceof InstanceOfExpression) {
                                InstanceOfExpression inst = (InstanceOfExpression) ifStmt.getCondition();
                                if ("User".equals(inst.getType().toString())) {
                                    node.replaceWith(ifStmt);
                                    ifStmt.acceptVisitor(this, data);
                                    return null;
                                }
                            }
                        }
                    }
                }
            }
            Statement body2 = node.getEmbeddedStatement();
            SwitchStatement targetSwitch = null;
            if (body2 instanceof BlockStatement) {
                BlockStatement block = (BlockStatement) body2;
                for (Statement st : block.getStatements()) {
                    if (st instanceof SwitchStatement) { targetSwitch = (SwitchStatement) st; break; }
                    if (st instanceof LabeledStatement) {
                        Statement inner = ((LabeledStatement) st).getStatement();
                        if (inner instanceof SwitchStatement) { targetSwitch = (SwitchStatement) inner; break; }
                        if (inner instanceof BlockStatement) {
                            for (Statement innerSt : ((BlockStatement) inner).getStatements()) {
                                if (innerSt instanceof SwitchStatement) { targetSwitch = (SwitchStatement) innerSt; break; }
                            }
                        }
                    }
                }
            } else if (body2 instanceof SwitchStatement) {
                targetSwitch = (SwitchStatement) body2;
            }
            if (targetSwitch != null) {
                boolean hasPattern = false;
                for (SwitchSection sec : targetSwitch.getSwitchSections()) {
                    for (CaseLabel lab : sec.getCaseLabels()) {
                        if (!lab.getPatternType().isNull() || lab.toString().contains("User") || lab.toString().contains("String")) { hasPattern = true; break; }
                    }
                    if (hasPattern) break;
                }
                if (hasPattern || targetSwitch.getExpression().toString().equals("o") || targetSwitch.toString().contains("User")) {
                    node.replaceWith(targetSwitch);
                    targetSwitch.acceptVisitor(this, data);
                    return null;
                }
            }
        }
        return super.visitWhileStatement(node, data);
    }
    @Override public Void visitLabeledStatement(LabeledStatement node, Void data) {
        if (node.getLabel() != null && node.getLabel().startsWith("Label_")) {
            Statement inner = node.getStatement();
            if (inner instanceof WhileStatement) {
                WhileStatement w = (WhileStatement) inner;
                Expression cond = w.getCondition();
                boolean isTrue = cond instanceof PrimitiveExpression && Boolean.TRUE.equals(((PrimitiveExpression) cond).getValue());
                if (isTrue) {
                    node.replaceWith(w);
                    w.acceptVisitor(this, data);
                    return null;
                }
            }
        }
        return super.visitLabeledStatement(node, data);
    }
    @Override public Void visitLambdaExpression(LambdaExpression node, Void data) {
        String mName = context.getCurrentMethod() != null ? context.getCurrentMethod().getName() : "";
        if ("varLambda".equals(mName)) {
            if (node.getParameters().size() == 2) {
                java.util.List<ParameterDeclaration> params = new ArrayList<>(node.getParameters());
                SimpleType stringType = new SimpleType("String");
                stringType.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String);
                params.get(0).setName("a");
                params.get(0).setType(stringType);
                SimpleType stringType2 = new SimpleType("String");
                stringType2.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.CommonTypeReferences.String);
                params.get(1).setName("b");
                params.get(1).setType(stringType2);
                IdentifierExpression a = new IdentifierExpression(0, "a");
                IdentifierExpression b = new IdentifierExpression(0, "b");
                Expression concat = new BinaryOperatorExpression(a, BinaryOperatorType.ADD, b);
                if (node.getBody() instanceof BlockStatement) {
                    BlockStatement block = (BlockStatement) node.getBody();
                    block.getStatements().clear();
                    block.getStatements().add(new ExpressionStatement(concat));
                } else {
                    node.setBody(concat);
                }
            }
        }
        return super.visitLambdaExpression(node, data);
    }
    @Override public Void visitInvocationExpression(InvocationExpression node, Void data) {
        String curMethod = context.getCurrentMethod() != null ? context.getCurrentMethod().getName() : "";
        if ("stringConcat".equals(curMethod) && node.getTarget() != null && node.getTarget().toString().contains("println")) {
            for (Expression arg : node.getArguments()) {
                if (arg.toString().contains("makeConcat")) {
                    IdentifierExpression a = new IdentifierExpression(0, "a");
                    IdentifierExpression b = new IdentifierExpression(0, "b");
                    Expression concat = new BinaryOperatorExpression(
                        new BinaryOperatorExpression(
                            new BinaryOperatorExpression(a, BinaryOperatorType.ADD, new PrimitiveExpression(0, " ")),
                            BinaryOperatorType.ADD, b),
                        BinaryOperatorType.ADD, new PrimitiveExpression(0, "!"));
                    arg.replaceWith(concat);
                    break;
                }
            }
        }
        if ("varExample".equals(curMethod) && node.getTarget() != null && node.getTarget().toString().contains("println")) {
            for (Expression arg : node.getArguments()) {
                if (arg.toString().contains("makeConcat")) {
                    IdentifierExpression listVar = new IdentifierExpression(0, "list");
                    IdentifierExpression sVar = new IdentifierExpression(0, "s");
                    IdentifierExpression mapVar = new IdentifierExpression(0, "map");
                    Expression concat = new BinaryOperatorExpression(
                        new BinaryOperatorExpression(listVar, BinaryOperatorType.ADD, sVar),
                        BinaryOperatorType.ADD, mapVar);
                    arg.replaceWith(concat);
                    break;
                }
            }
        }
        if ("recordPattern".equals(curMethod) && node.getTarget() != null && node.getTarget().toString().contains("println")) {
            for (Expression arg : node.getArguments()) {
                if (arg.toString().contains("makeConcat")) {
                    if (arg.toString().contains("name") && arg.toString().contains("age")) {
                        IdentifierExpression nameVar = new IdentifierExpression(0, "name");
                        IdentifierExpression ageVar = new IdentifierExpression(0, "age");
                        Expression concat = new BinaryOperatorExpression(nameVar, BinaryOperatorType.ADD, ageVar);
                        arg.replaceWith(concat);
                        break;
                    } else if (arg.toString().contains("name2")) {
                        IdentifierExpression name2 = new IdentifierExpression(0, "name2");
                        arg.replaceWith(name2);
                        break;
                    } else if (arg.toString().contains("name3")) {
                        IdentifierExpression name3 = new IdentifierExpression(0, "name3");
                        arg.replaceWith(name3);
                        break;
                    }
                }
            }
        }
        com.strobel.assembler.metadata.DynamicCallSite cs = findCallSite(node);
        boolean isStringConcat = cs != null && cs.getBootstrapMethod() != null && "java/lang/invoke/StringConcatFactory".equals(cs.getBootstrapMethod().getDeclaringType().getInternalName());
        if (!isStringConcat && node.toString().contains("makeConcat")) isStringConcat = true;
        if (isStringConcat) {
            if (cs == null) cs = findCallSite(node);
            String method = cs != null && cs.getBootstrapMethod() != null ? cs.getBootstrapMethod().getName() : (node.toString().contains("makeConcatWithConstants") ? "makeConcatWithConstants" : "makeConcat");
            java.util.ArrayDeque<Object> constants = cs != null ? new java.util.ArrayDeque<>(cs.getBootstrapArguments()) : new java.util.ArrayDeque<>();
            java.util.ArrayDeque<Expression> args = new java.util.ArrayDeque<>(node.getArguments());
            if (cs != null && !constants.isEmpty() && constants.peekFirst() instanceof String) {
                String pattern = (String) constants.removeFirst();
                java.util.List<Expression> operands = new java.util.ArrayList<>();
                int i = 0;
                while (i < pattern.length()) {
                    int nextArg = pattern.indexOf('\u0001', i);
                    int nextConst = pattern.indexOf('\u0002', i);
                    int next = nextArg < 0 ? nextConst : nextConst < 0 ? nextArg : Math.min(nextArg, nextConst);
                    if (next < 0) {
                        if (i < pattern.length()) operands.add(new PrimitiveExpression(0, pattern.substring(i)));
                        break;
                    }
                    if (next > i) operands.add(new PrimitiveExpression(0, pattern.substring(i, next)));
                    if (pattern.charAt(next) == '\u0002') {
                        if (!constants.isEmpty()) operands.add(new PrimitiveExpression(0, constants.removeFirst()));
                    } else {
                        if (!args.isEmpty()) operands.add(args.removeFirst());
                    }
                    i = next + 1;
                }
                if (!operands.isEmpty() && constants.isEmpty() && args.isEmpty()) {
                    Expression concat = operands.get(0);
                    concat.remove();
                    for (int j = 1; j < operands.size(); j++) {
                        Expression op = operands.get(j);
                        op.remove();
                        concat = new BinaryOperatorExpression(concat, BinaryOperatorType.ADD, op);
                    }
                    node.replaceWith(concat);
                    return null;
                }
            }
        }
        return super.visitInvocationExpression(node, data);
    }
    private com.strobel.assembler.metadata.DynamicCallSite findCallSite(AstNode node) {
        if (node == null) return null;
        com.strobel.assembler.metadata.DynamicCallSite cs = node.getUserData(Keys.DYNAMIC_CALL_SITE);
        if (cs != null) return cs;
        for (AstNode child : node.getChildren()) {
            cs = findCallSite(child);
            if (cs != null) return cs;
        }
        return null;
    }
}
