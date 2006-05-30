/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace j4o.lang.reflect {

    public class JavaArray {

        public static int GetLength(object array) {
            return ((Array)array).GetLength(0);
        }

        public static Object Get(object array, int index) {
            return ((Array)array).GetValue(index);
        }

        public static void Set(Object array, int index, Object value) {
            ((Array)array).SetValue(value, index);
        }

        public static Object NewInstance(Class elementType, int length) {
            return Array.CreateInstance(elementType.GetNetType(), length);
        }

        public static Object NewInstance(Class elementType, int[] dimensions) {
            return Array.CreateInstance(elementType.GetNetType(), dimensions);
        }
    }
}
