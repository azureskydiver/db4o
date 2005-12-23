/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang.reflect {

    public class JavaArray {

        public static int getLength(object array) {
            return ((Array)array).GetLength(0);
        }

        public static Object get(object array, int index) {
            return ((Array)array).GetValue(index);
        }

        public static void set(Object array, int index, Object value) {
            ((Array)array).SetValue(value, index);
        }

        public static Object newInstance(Class elementType, int length) {
            return Array.CreateInstance(elementType.getNetType(), length);
        }

        public static Object newInstance(Class elementType, int[] dimensions) {
            return Array.CreateInstance(elementType.getNetType(), dimensions);
        }
    }

}
