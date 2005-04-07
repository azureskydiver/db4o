/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using System.Reflection;

namespace j4o.lang.reflect {

    public class Method {

        private MethodInfo methodInfo;

        internal Method(MethodInfo methodInfo) {
            this.methodInfo = methodInfo;
        }

        public Object invoke(Object obj, Object[] args) {
            return methodInfo.Invoke(obj, args);
        }

        public String getName() {
            return methodInfo.Name;
        }
    }
}
