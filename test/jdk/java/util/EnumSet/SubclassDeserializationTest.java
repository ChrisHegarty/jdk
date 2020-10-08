/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/*
 * @test
 * @bug 8254161
 * @summary Prevent instantiation of EnumSet subclasses through deserialization
 * @library /test/lib
 * @compile --enable-preview -source ${jdk.version} SubclassDeserializationTest.java
 * @run testng/othervm --enable-preview SubclassDeserializationTest
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.EnumSet;
import jdk.test.lib.serial.SerialObjectBuilder;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class SubclassDeserializationTest {

    static final Class<InvalidObjectException> IOE = InvalidObjectException.class;

    /** Tests deserialization of RegularEnumSet withOUT the superclass in the byte stream. */
    @Test
    public void testDeserializeRegularEnumSet() throws Exception {
        Class<?> c = Class.forName("java.util.RegularEnumSet");
        byte[] bytes = SerialObjectBuilder.newBuilder("java.util.RegularEnumSet")
                .suid(ObjectStreamClass.lookup(c).getSerialVersionUID())
                .build();
        InvalidObjectException e = expectThrows(IOE, () -> deserialize(bytes));
        assertEquals(e.getStackTrace()[0].getClassName(), EnumSet.class.getName());
        assertEquals(e.getStackTrace()[0].getMethodName(), "readObjectNoData");
    }

    /** Tests deserialization of JumboEnumSet withOUT the superclass in the byte stream. */
    @Test
    public void testDeserializeJumboEnumSet() throws Exception {
        Class<?> c = Class.forName("java.util.JumboEnumSet");
        byte[] bytes = SerialObjectBuilder.newBuilder("java.util.JumboEnumSet")
                .suid(ObjectStreamClass.lookup(c).getSerialVersionUID())
                .build();
        InvalidObjectException e = expectThrows(IOE, () -> deserialize(bytes));
        assertEquals(e.getStackTrace()[0].getClassName(), EnumSet.class.getName());
        assertEquals(e.getStackTrace()[0].getMethodName(), "readObjectNoData");
    }

    /** Tests deserialization of RegularEnumSet with the superclass in the byte stream. */
    @Test
    public void testDeserializeRegularEnumSet2() throws Exception {
        Class<?> resc = Class.forName("java.util.RegularEnumSet");
        byte[] bytes = SerialObjectBuilder.newBuilder("java.util.RegularEnumSet")
                .suid(ObjectStreamClass.lookup(resc).getSerialVersionUID())
                .superClass(SerialObjectBuilder.newBuilder("java.util.EnumSet")
                        .suid(ObjectStreamClass.lookup(EnumSet.class).getSerialVersionUID()))
                .build();
        InvalidObjectException e = expectThrows(IOE, () -> deserialize(bytes));
        assertEquals(e.getStackTrace()[0].getClassName(), EnumSet.class.getName());
        assertEquals(e.getStackTrace()[0].getMethodName(), "readObject");
    }

    /** Tests deserialization of JumboEnumSet with the superclass in the byte stream. */
    @Test
    public void testDeserializeJumboEnumSet2() throws Exception {
        Class<?> resc = Class.forName("java.util.JumboEnumSet");
        byte[] bytes = SerialObjectBuilder.newBuilder("java.util.JumboEnumSet")
                .suid(ObjectStreamClass.lookup(resc).getSerialVersionUID())
                .superClass(SerialObjectBuilder.newBuilder("java.util.EnumSet")
                        .suid(ObjectStreamClass.lookup(EnumSet.class).getSerialVersionUID()))
                .build();
        InvalidObjectException e = expectThrows(IOE, () -> deserialize(bytes));
        assertEquals(e.getStackTrace()[0].getClassName(), EnumSet.class.getName());
        assertEquals(e.getStackTrace()[0].getMethodName(), "readObject");
    }

    @SuppressWarnings("unchecked")
    static <T> T deserialize(byte[] streamBytes)
        throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(streamBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (T) ois.readObject();
    }
}
