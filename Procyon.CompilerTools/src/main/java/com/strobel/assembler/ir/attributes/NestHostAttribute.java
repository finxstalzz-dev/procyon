/*
 * NestHostAttribute.java
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

import com.strobel.assembler.ir.ConstantPool;
import com.strobel.core.VerifyArgument;

/**
 * NestHost attribute (JEP 181 - Nest-Based Access Control)
 */
public final class NestHostAttribute extends SourceAttribute {
    private final ConstantPool.TypeInfoEntry _hostClass;

    public NestHostAttribute(final int length, final ConstantPool.TypeInfoEntry hostClass) {
        super(AttributeNames.NestHost, length);
        _hostClass = VerifyArgument.notNull(hostClass, "hostClass");
    }

    public ConstantPool.TypeInfoEntry getHostClass() {
        return _hostClass;
    }
}