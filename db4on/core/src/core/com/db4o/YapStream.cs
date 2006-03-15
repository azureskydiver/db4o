namespace com.db4o
{
    using System;
    using com.db4o.inside.query;
    using com.db4o.ext;

	public abstract class YapStream : com.db4o.YapStreamBase, ObjectContainer, ExtObjectContainer
	{
		internal YapStream(com.db4o.YapStream a_parent) : base(a_parent)
		{
		}

        public abstract com.db4o.ext.Db4oDatabase identity();

        public abstract void backup(string path);

        class ComparerAdaptor : com.db4o.query.QueryComparator
        {
            private System.Collections.IComparer _comparer;

            public ComparerAdaptor(System.Collections.IComparer comparer)
            {
                _comparer = comparer;
            }

            public int compare(object first, object second)
            {
                return _comparer.Compare(first, second);
            }
        }

        public ObjectSet query(com.db4o.query.Predicate match, System.Collections.IComparer comparer)
        {
            if (null == match) throw new ArgumentNullException("match");
            return query(match, new ComparerAdaptor(comparer));
        }
	    
#if NET_2_0
	    class GenericComparerAdaptor<T> : com.db4o.query.QueryComparator
	    {
            private System.Collections.Generic.IComparer<T> _comparer;
	        
	        public GenericComparerAdaptor(System.Collections.Generic.IComparer<T> comparer)
	        {
                _comparer = comparer;
	        }

            public int compare(object first, object second)
            {
                return _comparer.Compare((T)first, (T)second);
            }
	    }

        public System.Collections.Generic.IList<Extent> query<Extent>(Predicate<Extent> match)
        {
            if (null == match) throw new ArgumentNullException("match");
            return getNativeQueryHandler().execute(match, null);
        }

        public System.Collections.Generic.IList<Extent> query<Extent>(Predicate<Extent> match, System.Collections.Generic.IComparer<Extent> comparer)
        {
            if (null == match) throw new ArgumentNullException("match");
            com.db4o.query.QueryComparator comparator = null != comparer
                                                            ? new GenericComparerAdaptor<Extent>(comparer)
                                                            : null;
            return getNativeQueryHandler().execute(match, comparator);
        }

        public System.Collections.Generic.IList<ElementType> query<ElementType>(System.Type extent)
        {
            return query<ElementType>(extent, null);
        }
	    
	    public System.Collections.Generic.IList<ElementType> query<ElementType>(System.Type extent, System.Collections.Generic.IComparer<ElementType> comparer)
	    {
            QQuery q = (QQuery)query();
            q.constrain(extent);
            if (null != comparer) q.sortBy(new GenericComparerAdaptor<ElementType>(comparer));
            QueryResult qres = q.getQueryResult();
            return new com.db4o.inside.query.GenericObjectSetFacade<ElementType>(qres);
        }

        public System.Collections.Generic.IList<Extent> query<Extent>()
		{ 
			return query<Extent>(typeof(Extent));
		}

        public System.Collections.Generic.IList<Extent> query<Extent>(System.Collections.Generic.IComparer<Extent> comparer)
        {
            return query<Extent>(typeof(Extent), comparer);
        }
#endif
        
	}
}
