/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.jfr.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import static java.util.Objects.requireNonNull;

public final class EventHandlerBootstraps {

    private EventHandlerBootstraps() { }

    /**
     * Finds the MethodHandle for the write method.
     */
    public static MethodHandle writeHandleBootstrap(Lookup lookup,
                                                    String name,
                                                    TypeDescriptor type,
                                                    String writeMethodDescriptor)
        throws Throwable
    {
        requireNonNull(lookup);
        requireNonNull(name);
        requireNonNull(type);
        requireNonNull(writeMethodDescriptor);

        if (!name.equals("writeHandle"))
            throw new IllegalArgumentException("bad name: " + name);
        if (type != MethodHandle.class)
            throw new IllegalArgumentException("bad type: " + type);
        if ((lookup.lookupModes() & Lookup.PRIVATE) == 0)
            throw new IllegalArgumentException("lookup does not have private mode: " + lookup);
        if (writeMethodDescriptor.equals(""))
            throw new IllegalArgumentException("bad writeMethodDescriptor: ");

        MethodType mt = MethodType.fromMethodDescriptorString(writeMethodDescriptor, null);
        MethodHandle mh = lookup.findVirtual(lookup.lookupClass(), "write", mt);
        return mh.asType(mh.type().changeParameterType(0, jdk.internal.event.EventHandler.class));
    }
}
