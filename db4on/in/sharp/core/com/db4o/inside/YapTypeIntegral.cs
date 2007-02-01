/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.inside
{
	abstract internal class YapTypeIntegral : YapTypeAbstract
	{
		public YapTypeIntegral(com.db4o.inside.ObjectContainerBase stream)
			: base(stream)
		{
		}

		public override bool IsEqual(Object compare, Object with)
		{
			// sheesh, it would have been nice to call ==,
			// but it doesn't seem to work 
			return compare.Equals(with);
		}
	}
}
