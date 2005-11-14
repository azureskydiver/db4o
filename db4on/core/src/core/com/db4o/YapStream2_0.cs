/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

namespace com.db4o
{
#if NET_2_0

    using com.db4o.inside.query;
    using com.db4o.query;
	using System;
	using System.Collections.Generic;
	using System.Text;

    public partial class YapStream
    {
        public IList<Extent> query<Extent>(Predicate<Extent> match)
        {
            return getNativeQueryHandler().execute(match);
        }

        public IList<Extent> query<Extent>(System.Type extent)
        {
            QQuery q = (QQuery)query();
            q.constrain(extent);
            QueryResult qres = q.getQueryResult();
            return new com.db4o.inside.query.GenericObjectSetFacade<Extent>(qres);
        }
    }
#endif
}


