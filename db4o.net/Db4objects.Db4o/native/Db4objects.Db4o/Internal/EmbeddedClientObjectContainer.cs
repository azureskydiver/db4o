/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.Internal.Query;
using Db4objects.Db4o.Internal.Query.Result;
using Db4objects.Db4o.Internal.Query.Processor;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4o.Internal
{
	/// <exclude></exclude>
	public class EmbeddedClientObjectContainer : PartialEmbeddedClientObjectContainer
		, IInternalObjectContainer
	{
		public EmbeddedClientObjectContainer(LocalObjectContainer server) : base(server)
		{
		}

		public EmbeddedClientObjectContainer(LocalObjectContainer server, Transaction trans
			) : base(server, trans)
		{
		}
        void System.IDisposable.Dispose()
        {
            Close();
        }


        public IObjectSet Query(Db4objects.Db4o.Query.Predicate match, System.Collections.IComparer comparer)
        {
            return _server.Query(_transaction, match, new ComparerAdaptor(comparer));
        }

#if NET_2_0 || CF_2_0


        public System.Collections.Generic.IList<Extent> Query<Extent>(Predicate<Extent> match)
        {
            return _server.Query(match);
        }

        public System.Collections.Generic.IList<Extent> Query<Extent>(Predicate<Extent> match, System.Collections.Generic.IComparer<Extent> comparer)
        {
            return _server.Query(match, comparer);
        }

        public System.Collections.Generic.IList<Extent> Query<Extent>(Predicate<Extent> match, System.Comparison<Extent> comparison)
        {
            return _server.Query(match, comparison);
        }

        public System.Collections.Generic.IList<ElementType> Query<ElementType>(System.Type extent)
        {
            return Query<ElementType>(extent, null);
        }

        public System.Collections.Generic.IList<ElementType> Query<ElementType>(System.Type extent, System.Collections.Generic.IComparer<ElementType> comparer)
        {
            return _server.Query(extent, comparer);
        }

        public System.Collections.Generic.IList<Extent> Query<Extent>()
        {
            return Query<Extent>(typeof(Extent));
        }

        public System.Collections.Generic.IList<Extent> Query<Extent>(System.Collections.Generic.IComparer<Extent> comparer)
        {
            return Query<Extent>(typeof(Extent), comparer);
        }
#endif

    }
}
