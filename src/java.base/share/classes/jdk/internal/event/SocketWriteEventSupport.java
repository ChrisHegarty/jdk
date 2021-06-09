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

package jdk.internal.event;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;
import jdk.internal.access.JavaNetInetAddressAccess;
import jdk.internal.access.SharedSecrets;

/**
 * Low-level support for Socket Write Events.
 */
public final class SocketWriteEventSupport {

    private static final SocketWriteEvent EVENT = new SocketWriteEvent();

    public static final boolean isEnabled() {
        return EVENT.getEventHandler().isEnabled();
    }

    public static final long timestamp() {
        return EVENT.getEventHandler().getTimestamp();
    }

    public static final long duration(long startTime) {
        return EVENT.getEventHandler().getDuration(startTime);
    }

    public static final boolean shouldCommit(long duration) {
        return EVENT.getEventHandler().shouldCommit(duration);
    }

    public static final void write(long start, long duration, String host, String address, int port, long bytes) {
        try {
            var handler = EVENT.getEventHandler();
            handler.writeHandle().invokeExact(handler, start, duration, host, address, port, bytes);
        } catch (Throwable t) {
            throw new InternalError(t);
        }
    }

    public static final void write(long start, long duration, SocketAddress remoteAddress, long bytesWritten) {
        // #### fix up stacktrace capture frames
        long bytes = bytesWritten < 0 ? 0 : bytesWritten;
        if (remoteAddress instanceof InetSocketAddress isa) {
            InetAddress address = isa.getAddress();
            String host = getHostName(address);
            String addr = address.getHostAddress();
            int port = isa.getPort();
            try {
                SocketWriteEventSupport.write(start, duration, host, addr, port, bytes);
            } catch (Throwable t) {
                throw new InternalError(t);
            }
        } else {
            UnixDomainSocketAddress udsa = (UnixDomainSocketAddress) remoteAddress;
            String path = "[" + udsa.getPath().toString() + "]";
            try {
                SocketWriteEventSupport.write(start, duration, "Unix domain socket", path, 0, bytes);
            } catch (Throwable t) {
                throw new InternalError(t);
            }
        }
    }

    private static final JavaNetInetAddressAccess JNINA = SharedSecrets.getJavaNetInetAddressAccess();

    /** Returns the hostname of the given address, or empty string if none. */
    private static final String getHostName(InetAddress address) {
        var h = JNINA.hostnameValue(address);
        return h == null ? "" : h;
    }
}
