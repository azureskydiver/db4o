/* Copyright (C) 2009 Versant Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4o.Internal.Handlers
{
    internal class PreparedComparisonFor<T> : IPreparedComparison where T : IComparable<T>
    {
        private readonly T _source;

        public PreparedComparisonFor(T source)
        {
            _source = source;
        }

        public int CompareTo(object obj)
        {
            T target = ComparableValueFor(obj);
            return _source.CompareTo(target);
        }

    	private static T ComparableValueFor(object obj)
    	{
    		return obj == null ? default(T) : ((T)obj);
    	}
    }
}
