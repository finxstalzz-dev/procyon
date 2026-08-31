/*
 * RecordAttribute.java
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
 * Record attribute (JEP 395 - Records)
 */
public final class RecordAttribute extends SourceAttribute {
    private final List<RecordComponentInfo> _components;

    public RecordAttribute(final int length, final List<RecordComponentInfo> components) {
        super(AttributeNames.Record, length);
        _components = VerifyArgument.notNull(components, "components");
    }

    public List<RecordComponentInfo> getComponents() {
        return _components;
    }

    public static final class RecordComponentInfo {
        private final String _name;
        private final String _descriptor;
        private final SourceAttribute[] _attributes;

        public RecordComponentInfo(
                final String name,
                final String descriptor,
                final SourceAttribute[] attributes) {
            _name = VerifyArgument.notNull(name, "name");
            _descriptor = VerifyArgument.notNull(descriptor, "descriptor");
            _attributes = attributes;
        }

        public String getName() {
            return _name;
        }

        public String getDescriptor() {
            return _descriptor;
        }

        public SourceAttribute[] getAttributes() {
            return _attributes;
        }
    }
}