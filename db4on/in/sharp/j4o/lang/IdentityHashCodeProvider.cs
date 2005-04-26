/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace j4o.lang {

    public class IdentityHashCodeProvider {

        public static int identityHashCode(object obj) {
            if (obj == null) {
                return 0;
            }
			return com.db4o.Compat.identityHashCode(obj);
        }
    }
}

