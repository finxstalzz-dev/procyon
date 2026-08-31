/*
 * PermittedSubclassesAttribute.java
 *
 * Copyright (c) 2024
 *
 * This source code is subject to terms and conditions of the Apache License, Version 2.0.
 * A copy of the license can be found in the License.html file at the root of this distribution.
 * By using this source code in any fashion, you are agreeing to be bound by the terms of the
 * Apache License, Version 2.0.
 *
 * You must not remove this notice, or any other, from this software.
 */

package com.strobel.assembler.ir.attributes;

import com.strobel.assembler.Collection;
import com.strobel.assembler.ir.ConstantPool;
import com.strobel.core.VerifyArgument;

import java.util.List;

/**
 * PermittedSubclasses attribute (JEP 360 - Sealed Classes)
 */
public final class PermittedSubclassesAttribute extends SourceAttribute {
    private final List<ConstantPool.TypeInfoEntry> _classes;

    public PermittedSubclassesAttribute(final int length, final List<ConstantPool.TypeInfoEntry> classes) {
        super(AttributeNames.PermittedSubclasses, length);
        _classes = VerifyArgument.notNull(classes, "classes");
    }

    public List<ConstantPool.TypeInfoEntry> getClasses() {
        return _classes;
    }
}