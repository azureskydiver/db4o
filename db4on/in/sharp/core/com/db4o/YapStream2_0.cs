/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

namespace com.db4o
{
#if NET_2_0
	using System;
	using System.Collections.Generic;
	using System.Text;

    public partial class YapStream
    {
        public IList<Extent> query<Extent>(Predicate<Extent> match)
        {
            return getNativeQueryHandler().execute(match);
        }
    }
#endif
}


