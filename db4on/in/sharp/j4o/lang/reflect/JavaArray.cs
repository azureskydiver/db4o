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

        internal static void Set(object onArray, int index, object element)
        {
            ((Array)onArray).SetValue(element, index);
        }

        public static void Set(object array, int index, object obj) {
            ((Array)array).SetValue(obj,index);
        }
    }
}
