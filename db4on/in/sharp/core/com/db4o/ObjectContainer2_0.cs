/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

namespace com.db4o
{
#if NET_2_0
    using System.Collections.Generic;

	/// <summary>
	/// Native Query delegate for .NET 2.0
	/// </summary>	
	/// <remarks>
    /// See the detailed Native Query documentation in the
    /// <see cref="com.db4o.query.Predicate">Predicate</see>
    /// class.
	/// </remarks>
    public delegate bool Predicate<T>(T candidate);

    public partial interface ObjectContainer
    {
    	/// <summary>
    	/// .NET 2.0 Native Query interface.
    	/// </summary>
        /// <remarks>
        /// See the detailed Native Query documentation in the
        /// <see cref="com.db4o.query.Predicate">Predicate</see>
        /// class.
        /// </remarks>
        IList <Extent> query <Extent>( Predicate<Extent> match );

        /// <summary>
        /// queries for all instances of a class.
        /// </summary>
        IList <Extent> query <Extent> (System.Type extent);

    }
#endif
}