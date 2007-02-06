/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.@internal.handlers
{
	abstract public class IntegralTypeHandler : NetTypeHandler
	{
		public IntegralTypeHandler(com.db4o.@internal.ObjectContainerBase stream)
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
