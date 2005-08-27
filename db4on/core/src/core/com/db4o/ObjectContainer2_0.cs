/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

namespace com.db4o
{
#if NET_2_0
    using System.Collections.Generic;

	/// <summary>
	/// A native query predicate.
	/// </summary>	
	/// <remarks>
	/// The type parameter specifies the extent for the query.<br />
	/// For all instances of the extent that are to be included into the
	/// resultset of the query, the method returns true. For all instances
	/// that are not to be included the method returns false. <br /><br />
	/// Here is an example of an anonymous method that follows these conventions:<br />
	/// <pre><code>
	/// delegate(Cat cat) {<br />
	///     return cat.name.equals("Frizz");<br />
	/// }<br />
	/// </code></pre><br /><br />
	/// </remarks>
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