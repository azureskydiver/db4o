/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

namespace com.db4o
{
#if NET_2_0
    using System.Collections.Generic;

	/// <summary>
	/// A native query predicate.
	/// </summary>
    public delegate bool Predicate<T>(T candidate);

    public partial interface ObjectContainer
    {
    	/// <summary>
    	/// Executes a native query against this container.
    	/// </summary>
        IList<Extent> query<Extent>(Predicate<Extent> match);
    }
#endif
}