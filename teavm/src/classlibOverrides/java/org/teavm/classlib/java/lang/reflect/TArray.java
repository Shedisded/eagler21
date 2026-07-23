/*
 *  Copyright 2013 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang.reflect;

import org.teavm.classlib.PlatformDetector;
import org.teavm.classlib.java.lang.TArrayIndexOutOfBoundsException;
import org.teavm.classlib.java.lang.TClass;
import org.teavm.classlib.java.lang.TIllegalArgumentException;
import org.teavm.classlib.java.lang.TNegativeArraySizeException;
import org.teavm.classlib.java.lang.TNullPointerException;
import org.teavm.classlib.java.lang.TObject;
import org.teavm.dependency.PluggableDependency;
import org.teavm.interop.Address;
import org.teavm.runtime.GC;
import org.teavm.runtime.reflect.ClassInfo;

public final class TArray extends TObject {
    @PluggableDependency(ArrayDependencyPlugin.class)
    public static int getLength(Object array) throws TIllegalArgumentException {
        var cls = ((TClass<?>) (Object) array.getClass()).getClassInfo();
        if (cls.itemType() == null) {
            throw new TIllegalArgumentException();
        }
        return cls.arrayLength(array);
    }

    @PluggableDependency(ArrayDependencyPlugin.class)
    public static Object newInstance(Class<?> componentType, int length) throws TNegativeArraySizeException {
        if (componentType == null) {
            throw new TNullPointerException();
        }
        if (componentType == void.class) {
            throw new TIllegalArgumentException();
        }
        if (length < 0) {
            throw new TNegativeArraySizeException();
        }

        var cls = ((TClass<?>) (Object) componentType).getClassInfo();
        return cls.newArrayInstance(length);
    }

    public static Object newInstance(Class<?> componentType, int[] dimensions) throws TNegativeArraySizeException {
        if (dimensions.length == 0) {
            throw new TIllegalArgumentException();
        }
        return newMultiArray(componentType, dimensions, 0);
    }

    // Recursive reflective multi-dim array creation (java.lang.reflect.Array.newInstance(cls, int[])).
    // Reuses the single-dim newInstance + set; the outer levels are arrays-of-arrays whose element class
    // is taken from a recursively-built sub-array. World gen (noise/DFU) hits this; was a Stage-D stub.
    private static Object newMultiArray(Class<?> componentType, int[] dimensions, int index)
            throws TNegativeArraySizeException {
        int length = dimensions[index];
        if (index == dimensions.length - 1) {
            return newInstance(componentType, length);
        }
        if (length < 0) {
            throw new TNegativeArraySizeException();
        }
        // Build one inner array to discover the element (array) class, then an outer array of it.
        Object first = newMultiArray(componentType, dimensions, index + 1);
        Object result = newInstance(first.getClass(), length);
        if (length > 0) {
            set(result, 0, first);
            for (int i = 1; i < length; i++) {
                set(result, i, newMultiArray(componentType, dimensions, index + 1));
            }
        }
        return result;
    }

    @PluggableDependency(ArrayDependencyPlugin.class)
    public static Object get(Object array, int index) throws TIllegalArgumentException,
            TArrayIndexOutOfBoundsException {
        if (index < 0 || index >= getLength(array)) {
            throw new TArrayIndexOutOfBoundsException();
        }
        var classInfo = ((TClass<?>) (Object) array.getClass()).getClassInfo();
        return classInfo.getItem(array, index);
    }

    @PluggableDependency(ArrayDependencyPlugin.class)
    public static void set(Object array, int index, Object value) throws TIllegalArgumentException,
            TArrayIndexOutOfBoundsException {
        if (index < 0 || index >= getLength(array)) {
            throw new TArrayIndexOutOfBoundsException();
        }
        var classInfo = ((TClass<?>) (Object) array.getClass()).getClassInfo();
        classInfo.putItem(array, index, value);
        if (PlatformDetector.requiresOwnGC() && classInfo.itemType().primitiveKind() == ClassInfo.PrimitiveKind.NOT) {
            GC.writeBarrier(Address.ofObject(array).toStructure());
        }
    }
}
