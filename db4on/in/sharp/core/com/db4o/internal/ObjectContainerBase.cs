namespace com.db4o.@internal
{
	using System;
	using com.db4o.@internal.query;
	using com.db4o.ext;

	/// <summary>
	/// </summary>
	/// <exclude />
	public abstract class ObjectContainerBase : com.db4o.@internal.PartialObjectContainer, ObjectContainer, ExtObjectContainer
	{
		internal ObjectContainerBase(com.db4o.config.Configuration config, com.db4o.@internal.ObjectContainerBase a_parent)
			: base(config, a_parent)
		{
		}

		void System.IDisposable.Dispose()
		{
			Close();
		}

		public abstract com.db4o.ext.Db4oDatabase Identity();

		public abstract void Backup(string path);
		
		com.db4o.ObjectSet ObjectContainer.Query(System.Type type)
		{
			return Query(j4o.lang.Class.GetClassForType(type));
		}

		class ComparerAdaptor : com.db4o.query.QueryComparator
		{
			private System.Collections.IComparer _comparer;

			public ComparerAdaptor(System.Collections.IComparer comparer)
			{
				_comparer = comparer;
			}

			public int Compare(object first, object second)
			{
				return _comparer.Compare(first, second);
			}
		}

		public ObjectSet Query(com.db4o.query.Predicate match, System.Collections.IComparer comparer)
		{
			if (null == match) throw new ArgumentNullException("match");
			return Query(match, new ComparerAdaptor(comparer));
		}

#if NET_2_0 || CF_2_0
		class GenericComparerAdaptor<T> : com.db4o.query.QueryComparator
		{
			private System.Collections.Generic.IComparer<T> _comparer;

			public GenericComparerAdaptor(System.Collections.Generic.IComparer<T> comparer)
			{
				_comparer = comparer;
			}

			public int Compare(object first, object second)
			{
				return _comparer.Compare((T)first, (T)second);
			}
		}

		class GenericComparisonAdaptor<T> : DelegateEnvelope, com.db4o.query.QueryComparator
		{
			public GenericComparisonAdaptor(System.Comparison<T> comparer)
				: base(comparer)
			{
			}

			public int Compare(object first, object second)
			{
				System.Comparison<T> _comparer = (System.Comparison<T>)GetContent();
				return _comparer((T)first, (T)second);
			}
		}

		public System.Collections.Generic.IList<Extent> Query<Extent>(Predicate<Extent> match)
		{
			if (null == match) throw new ArgumentNullException("match");
			return GetNativeQueryHandler().Execute(match, null);
		}

		public System.Collections.Generic.IList<Extent> Query<Extent>(Predicate<Extent> match, System.Collections.Generic.IComparer<Extent> comparer)
		{
			if (null == match) throw new ArgumentNullException("match");
			com.db4o.query.QueryComparator comparator = null != comparer
															? new GenericComparerAdaptor<Extent>(comparer)
															: null;
			return GetNativeQueryHandler().Execute(match, comparator);
		}

		public System.Collections.Generic.IList<Extent> Query<Extent>(Predicate<Extent> match, System.Comparison<Extent> comparison)
		{
			if (null == match) throw new ArgumentNullException("match");
			com.db4o.query.QueryComparator comparator = null != comparison
															? new GenericComparisonAdaptor<Extent>(comparison)
															: null;
			return GetNativeQueryHandler().Execute(match, comparator);
		}

		public System.Collections.Generic.IList<ElementType> Query<ElementType>(System.Type extent)
		{
			return Query<ElementType>(extent, null);
		}

		public System.Collections.Generic.IList<ElementType> Query<ElementType>(System.Type extent, System.Collections.Generic.IComparer<ElementType> comparer)
		{
			QQuery q = (QQuery)Query();
			q.Constrain(extent);
			if (null != comparer) q.SortBy(new GenericComparerAdaptor<ElementType>(comparer));
			QueryResult qres = q.GetQueryResult();
			return new com.db4o.@internal.query.GenericObjectSetFacade<ElementType>(qres);
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
