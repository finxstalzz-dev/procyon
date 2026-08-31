package com.strobel.decompiler.languages.java.ast;

import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class YieldStatement extends Statement {
    public final static TokenRole YIELD_KEYWORD_ROLE = new TokenRole("yield", TokenRole.FLAG_KEYWORD);

    public YieldStatement(final int offset, final Expression expression) {
        super(offset);
        setExpression(expression);
    }

    public final Expression getExpression() {
        return getChildByRole(Roles.EXPRESSION);
    }

    public final void setExpression(final Expression value) {
        setChildByRole(Roles.EXPRESSION, value);
    }

    @Override
    public <T, R> R acceptVisitor(final IAstVisitor<? super T, ? extends R> visitor, final T data) {
        return visitor.visitYieldStatement(this, data);
    }

    @Override
    public boolean matches(final INode other, final Match match) {
        if (other instanceof YieldStatement) {
            final YieldStatement o = (YieldStatement) other;
            return !o.isNull() && getExpression().matches(o.getExpression(), match);
        }
        return false;
    }
}
