/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Defines the JDK-specific HTTP server API.
 *
 * A basic high-level server API for building embedded HTTP servers.
 * The main components are
 * <ul>
 * <li>the {@link com.sun.net.httpserver.HttpExchange} class that describes a
 * request and response pair,</li>
 * <li>the {@link com.sun.net.httpserver.HttpHandler} interface to handle
 * incoming requests,</li>
 * <li>the {@link com.sun.net.httpserver.HttpServer} class to listen for
 * connections and dispatch requests to the handlers,</li>
 * <li>the {@link com.sun.net.httpserver.HttpContext} class that maps a URI path
 * to a {@code HttpHandler},</li>
 * <li>the {@link com.sun.net.httpserver.Filter} class that allows pre- and post-
 * processing of requests.</li>
 *
 * Additionally, {@link com.sun.net.httpserver.SimpleFileServer} offers a
 * out-of-the-box simple HTTP file server. A default implementation is provided
 * via the main entry point of this module.
 *
 * @uses com.sun.net.httpserver.spi.HttpServerProvider
 *
 * @moduleGraph
 * @since 9
 */
module jdk.httpserver {

    exports com.sun.net.httpserver;
    exports com.sun.net.httpserver.spi;

    uses com.sun.net.httpserver.spi.HttpServerProvider;
}
