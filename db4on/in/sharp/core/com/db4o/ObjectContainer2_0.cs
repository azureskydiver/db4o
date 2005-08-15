/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

namespace com.db4o
{
#if NET_2_0
    using System.Collections.Generic;

    public delegate bool Predicate<T>(T candidate);

    public partial interface ObjectContainer
    {
        IList<Extent> query<Extent>(Predicate<Extent> match);
    }
#endif
}