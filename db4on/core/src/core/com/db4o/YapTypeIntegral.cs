/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;

namespace com.db4o {

	abstract internal class YapTypeIntegral :YapTypeAbstract
	{
        public YapTypeIntegral(com.db4o.YapStream stream) : base(stream) {
        }

        public override bool isEqual(Object compare, Object with){
            // sheesh, it would have been nice to call ==,
            // but it doesn't seem to work 
            return compare.Equals(with);
        }
	}

}
