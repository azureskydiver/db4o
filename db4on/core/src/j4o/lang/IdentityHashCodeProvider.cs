
/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace j4o.lang {

    public class IdentityHashCodeProvider {
		
		public delegate int HashCodeFunction(object o);
		
		static HashCodeFunction _hashCode = com.db4o.Compat.getIdentityHashCodeFunction();

        public static int identityHashCode(object obj) {
            if (obj == null) {
                return 0;
            }
			return _hashCode(obj);
        }
    }
}

