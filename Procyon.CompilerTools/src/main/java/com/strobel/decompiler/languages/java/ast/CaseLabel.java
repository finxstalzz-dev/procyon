/*
 * CaseLabel.java
 *
 * Copyright (c) 2013 Mike Strobel
 *
 * This source code is based on Mono.Cecil from Jb Evain, Copyright (c) Jb Evain;
 * and ILSpy/ICSharpCode from SharpDevelop, Copyright (c) AlphaSierraPapa.
 *
 * This source code is subject to terms and conditions of the Apache License, Version 2.0.
 * A copy of the license can be found in the License.html file at the root of this distribution.
 * By using this source code in any fashion, you are agreeing to be bound by the terms of the
 * Apache License, Version 2.0.
 *
 * You must not remove this notice, or any other, from this software.
 */

package com.strobel.decompiler.languages.java.ast;

import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class CaseLabel extends AstNode {
    public final static TokenRole CASE_KEYWORD_ROLE = new TokenRole("case", TokenRole.FLAG_KEYWORD);
    public final static TokenRole DEFAULT_KEYWORD_ROLE = new TokenRole("default", TokenRole.FLAG_KEYWORD);
    public final static TokenRole WHEN_KEYWORD_ROLE = new TokenRole("when", TokenRole.FLAG_KEYWORD);

    public CaseLabel() {
    }

    public CaseLabel(final Expression value) {
        setExpression(value);
    }

    public final AstType getPatternType() {
        return getChildByRole(Roles.TYPE);
    }
    public final void setPatternType(final AstType t) { setChildByRole(Roles.TYPE, t); }
    public final String getPatternVariable() {
        final Identifier id = getChildByRole(Roles.PATTERN_VARIABLE);
        return id.isNull() ? null : id.getName();
    }
    public final void setPatternVariable(final String name) {
        if (name == null) setChildByRole(Roles.PATTERN_VARIABLE, Identifier.NULL);
        else setChildByRole(Roles.PATTERN_VARIABLE, Identifier.create(name));
    }
    public final Expression getGuardExpression() {
        return getChildByRole(Roles.CONDITION);
    }
    public final void setGuardExpression(final Expression e) { setChildByRole(Roles.CONDITION, e); }
    public final AstNodeCollection<ParameterDeclaration> getRecordPatternComponents() {
        return getChildrenByRole(Roles.RECORD_PATTERN_COMPONENT);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.UNKNOWN;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Role<? extends CaseLabel> getRole() {
        return (Role<? extends CaseLabel>) super.getRole();
    }

    public final JavaTokenNode getColonToken() {
        return getChildByRole(Roles.COLON);
    }

    public final Expression getExpression() {
        return getChildByRole(Roles.EXPRESSION);
    }

    public final void setExpression(final Expression value) {
        setChildByRole(Roles.EXPRESSION, value);
    }

    @Override
    public <T, R> R acceptVisitor(final IAstVisitor<? super T, ? extends R> visitor, final T data) {
        return visitor.visitCaseLabel(this, data);
    }

    @Override
    public boolean matches(final INode other, final Match match) {
        return other instanceof CaseLabel &&
               !other.isNull() &&
               getExpression().matches(((CaseLabel) other).getExpression(), match) &&
               getPatternType().matches(((CaseLabel) other).getPatternType(), match) &&
               getGuardExpression().matches(((CaseLabel) other).getGuardExpression(), match);
    }
}
