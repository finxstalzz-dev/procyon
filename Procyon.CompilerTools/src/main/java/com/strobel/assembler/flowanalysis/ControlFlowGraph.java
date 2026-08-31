/*
 * ControlFlowGraph.java
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

package com.strobel.assembler.flowanalysis;

import com.strobel.core.ArrayUtilities;
import com.strobel.core.BooleanBox;
import com.strobel.core.ExceptionUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.functions.Block;
import com.strobel.functions.Function;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.regex.Pattern;

public final class ControlFlowGraph {
    private final List<ControlFlowNode> _nodes;

    public final ControlFlowNode getEntryPoint() {
        return _nodes.get(0);
    }

    public final ControlFlowNode getRegularExit() {
        return _nodes.get(1);
    }

    public final ControlFlowNode getExceptionalExit() {
        return _nodes.get(2);
    }

    public final List<ControlFlowNode> getNodes() {
        return _nodes;
    }

    public ControlFlowGraph(final ControlFlowNode... nodes) {
        _nodes = ArrayUtilities.asUnmodifiableList(VerifyArgument.noNullElements(nodes, "nodes"));

        assert nodes.length >= 3;
        assert getEntryPoint().getNodeType() == ControlFlowNodeType.EntryPoint;
        assert getRegularExit().getNodeType() == ControlFlowNodeType.RegularExit;
        assert getExceptionalExit().getNodeType() == ControlFlowNodeType.ExceptionalExit;
    }

    public final void resetVisited() {
        for (final ControlFlowNode node : _nodes) {
            node.setVisited(false);
        }
    }

    public final void computeDominance() {
        computeDominance(new BooleanBox());
    }

    public final void computeDominance(final BooleanBox cancelled) {
        for (final ControlFlowNode n : _nodes) {
            n.setImmediateDominator(null);
            n.getDominatorTreeChildren().clear();
            n.getDominanceFrontier().clear();
        }

        final ControlFlowNode entryPoint = getEntryPoint();
        entryPoint.setImmediateDominator(entryPoint);

        final List<ControlFlowNode> rpo = new ArrayList<>();
        resetVisited();
        buildReversePostOrder(entryPoint, rpo);
        java.util.Collections.reverse(rpo);

        final List<ControlFlowNode> order = new ArrayList<>();
        for (final ControlFlowNode n : rpo) {
            if (n != entryPoint) {
                order.add(n);
            }
        }
        for (final ControlFlowNode n : _nodes) {
            if (!rpo.contains(n)) {
                order.add(n);
            }
        }

        boolean changed = true;
        while (changed) {
            if (cancelled.get()) {
                throw new CancellationException();
            }
            changed = false;
            for (final ControlFlowNode b : order) {
                ControlFlowNode newIdom = null;
                for (final ControlFlowNode p : b.getPredecessors()) {
                    if (p.getImmediateDominator() != null) {
                        newIdom = p;
                        break;
                    }
                }
                if (newIdom == null) {
                    continue;
                }
                for (final ControlFlowNode p : b.getPredecessors()) {
                    if (p == newIdom || p.getImmediateDominator() == null) {
                        continue;
                    }
                    newIdom = findCommonDominator(p, newIdom);
                    if (newIdom == null) {
                        break;
                    }
                }
                if (newIdom != null && b.getImmediateDominator() != newIdom) {
                    b.setImmediateDominator(newIdom);
                    changed = true;
                }
            }
        }

        entryPoint.setImmediateDominator(null);

        for (final ControlFlowNode node : _nodes) {
            final ControlFlowNode idom = node.getImmediateDominator();
            if (idom != null) {
                idom.getDominatorTreeChildren().add(node);
            }
        }
    }

    private void buildReversePostOrder(final ControlFlowNode entry, final List<ControlFlowNode> out) {
        final java.util.Deque<ControlFlowNode> stack = new java.util.ArrayDeque<>();
        final java.util.Deque<java.util.Iterator<ControlFlowNode>> itStack = new java.util.ArrayDeque<>();
        stack.push(entry);
        itStack.push(entry.getSuccessors().iterator());
        entry.setVisited(true);
        while (!stack.isEmpty()) {
            final java.util.Iterator<ControlFlowNode> it = itStack.peek();
            if (it.hasNext()) {
                final ControlFlowNode succ = it.next();
                if (!succ.isVisited()) {
                    succ.setVisited(true);
                    stack.push(succ);
                    itStack.push(succ.getSuccessors().iterator());
                }
            }
            else {
                out.add(stack.pop());
                itStack.pop();
            }
        }
    }

    public final void computeDominanceFrontier() {
        resetVisited();

        getEntryPoint().traversePostOrder(
            new Function<ControlFlowNode, Iterable<ControlFlowNode>>() {
                @Override
                public final Iterable<ControlFlowNode> apply(final ControlFlowNode input) {
                    return input.getDominatorTreeChildren();
                }
            },
            new Block<ControlFlowNode>() {
                @Override
                public void accept(final ControlFlowNode n) {
                    final Set<ControlFlowNode> dominanceFrontier = n.getDominanceFrontier();

                    dominanceFrontier.clear();

                    for (final ControlFlowNode s : n.getSuccessors()) {
                        if (s.getImmediateDominator() != n) {
                            dominanceFrontier.add(s);
                        }
                    }

                    for (final ControlFlowNode child : n.getDominatorTreeChildren()) {
                        for (final ControlFlowNode p : child.getDominanceFrontier()) {
                            if (p.getImmediateDominator() != n) {
                                dominanceFrontier.add(p);
                            }
                        }
                    }
                }
            }
        );
    }

    public static ControlFlowNode findCommonDominator(final ControlFlowNode a, final ControlFlowNode b) {
        if (a == null || b == null) {
            return null;
        }
        final Set<ControlFlowNode> path1 = new LinkedHashSet<>();

        ControlFlowNode node1 = a;
        ControlFlowNode node2 = b;

        while (node1 != null && path1.add(node1)) {
            node1 = node1.getImmediateDominator();
        }

        while (node2 != null) {
            if (path1.contains(node2)) {
                return node2;
            }
            node2 = node2.getImmediateDominator();
        }

        return null;
    }

    public final List<ControlFlowNode> getUnreachableNodes() {
        final List<ControlFlowNode> result = new ArrayList<>();
        for (final ControlFlowNode n : _nodes) {
            if (n.getNodeType() == ControlFlowNodeType.EntryPoint) {
                continue;
            }
            if (n.getImmediateDominator() == null && n.getIncoming().isEmpty()) {
                result.add(n);
            }
        }
        return java.util.Collections.unmodifiableList(result);
    }

    public final boolean containsEdge(final ControlFlowNode from, final ControlFlowNode to, final JumpType type) {
        for (final ControlFlowEdge e : from.getOutgoing()) {
            if (e.getTarget() == to && e.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public final void export(final File path) {
        final PlainTextOutput output = new PlainTextOutput();

        output.writeLine("digraph g {");
        output.indent();

        final Set<ControlFlowEdge> edges = new LinkedHashSet<>();

        for (final ControlFlowNode node : _nodes) {
            output.writeLine("\"%s\" [", nodeName(node));
            output.indent();

            output.writeLine(
                "label = \"%s\\l\"",
                escapeGraphViz(node.toString())
            );

            output.writeLine(", shape = \"box\"");

            output.unindent();
            output.writeLine("];");

            edges.addAll(node.getIncoming());
            edges.addAll(node.getOutgoing());

            final ControlFlowNode endFinallyNode = node.getEndFinallyNode();

            if (endFinallyNode != null) {
                output.writeLine("\"%s\" [", nodeName(endFinallyNode));
                output.indent();

                output.writeLine(
                    "label = \"%s\"",
                    escapeGraphViz(endFinallyNode.toString())
                );

                output.writeLine("shape = \"box\"");

                output.unindent();
                output.writeLine("];");

                edges.addAll(endFinallyNode.getIncoming());
                edges.addAll(endFinallyNode.getOutgoing());
//                edges.add(new ControlFlowEdge(node, endFinallyNode, JumpType.EndFinally));
            }
        }

        for (final ControlFlowEdge edge : edges) {
            final ControlFlowNode from = edge.getSource();
            final ControlFlowNode to = edge.getTarget();

            output.writeLine("\"%s\" -> \"%s\" [", nodeName(from), nodeName(to));
            output.indent();

            switch (edge.getType()) {
                case Normal:
                    break;

                case LeaveTry:
                    output.writeLine("color = \"blue\"");
                    break;

                case EndFinally:
                    output.writeLine("color = \"red\"");
                    break;

                case JumpToExceptionHandler:
                    output.writeLine("color = \"gray\"");
                    break;

                default:
                    output.writeLine("label = \"%s\"", edge.getType());
                    break;
            }

            output.unindent();
            output.writeLine("];");
        }

        output.unindent();
        output.writeLine("}");

        try (final OutputStreamWriter out = new FileWriter(path)) {
            out.write(output.toString());
        }
        catch (IOException e) {
            throw ExceptionUtilities.asRuntimeException(e);
        }
    }

    private static String nodeName(final ControlFlowNode node) {
        String name = "node" + node.getBlockIndex();

        if (node.getNodeType() == ControlFlowNodeType.EndFinally) {
            name += "_ef";
        }

        return name;
    }

    private final static Pattern SAFE_PATTERN = Pattern.compile("^[\\w\\d]+$");

    private static String escapeGraphViz(final String text) {
        return escapeGraphViz(text, false);
    }

    private static String escapeGraphViz(final String text, final boolean quote) {
        if (SAFE_PATTERN.matcher(text).matches()) {
            return quote ? "\"" + text + "\""
                         : text;
        }
        else {
            return (quote ? "\"" : "") +
                   text.replace("\\", "\\\\")
                       .replace("\r", "")
                       .replace("\n", "\\l")
                       .replace("|", "\\|")
                       .replace("{", "\\{")
                       .replace("}", "\\}")
                       .replace("<", "\\<")
                       .replace(">", "\\>")
                       .replace("\"", "\\\"") +
                   (quote ? "\"" : "");
        }
    }
}
