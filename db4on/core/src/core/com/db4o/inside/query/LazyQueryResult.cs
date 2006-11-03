namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public class LazyQueryResult : com.db4o.inside.query.AbstractQueryResult
	{
		private System.Collections.IEnumerable _iterable;

		public LazyQueryResult(com.db4o.Transaction trans) : base(trans)
		{
		}

		public override object Get(int index)
		{
			throw new System.NotImplementedException();
		}

		public override int IndexOf(int id)
		{
			throw new System.NotImplementedException();
		}

		public override com.db4o.foundation.IntIterator4 IterateIDs()
		{
			if (_iterable == null)
			{
				throw new System.InvalidOperationException();
			}
			return new com.db4o.foundation.IntIterator4Adaptor(_iterable.GetEnumerator());
		}

		public override void LoadFromClassIndex(com.db4o.YapClass clazz)
		{
			_iterable = new _AnonymousInnerClass39(this, clazz);
		}

		private sealed class _AnonymousInnerClass39 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass39(LazyQueryResult _enclosing, com.db4o.YapClass clazz
				)
			{
				this._enclosing = _enclosing;
				this.clazz = clazz;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return this._enclosing.ClassIndexIterator(clazz);
			}

			private readonly LazyQueryResult _enclosing;

			private readonly com.db4o.YapClass clazz;
		}

		public virtual System.Collections.IEnumerator ClassIndexIterator(com.db4o.YapClass
			 clazz)
		{
			return com.db4o.inside.classindex.BTreeClassIndexStrategy.Iterate(clazz, Transaction
				());
		}

		public override void LoadFromClassIndexes(com.db4o.YapClassCollectionIterator classCollectionIterator
			)
		{
			_iterable = new _AnonymousInnerClass51(this, classCollectionIterator);
		}

		private sealed class _AnonymousInnerClass51 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass51(LazyQueryResult _enclosing, com.db4o.YapClassCollectionIterator
				 classCollectionIterator)
			{
				this._enclosing = _enclosing;
				this.classCollectionIterator = classCollectionIterator;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return new com.db4o.foundation.CompositeIterator4(new _AnonymousInnerClass54(this
					, classCollectionIterator));
			}

			private sealed class _AnonymousInnerClass54 : com.db4o.foundation.MappingIterator
			{
				public _AnonymousInnerClass54(_AnonymousInnerClass51 _enclosing, com.db4o.YapClassCollectionIterator
					 baseArg1) : base(baseArg1)
				{
					this._enclosing = _enclosing;
				}

				protected override object Map(object current)
				{
					com.db4o.YapClass yapClass = (com.db4o.YapClass)current;
					if (this._enclosing._enclosing.SkipClass(yapClass))
					{
						return com.db4o.foundation.MappingIterator.SKIP;
					}
					return this._enclosing._enclosing.ClassIndexIterator(yapClass);
				}

				private readonly _AnonymousInnerClass51 _enclosing;
			}

			private readonly LazyQueryResult _enclosing;

			private readonly com.db4o.YapClassCollectionIterator classCollectionIterator;
		}

		public virtual bool SkipClass(com.db4o.YapClass yapClass)
		{
			if (yapClass.GetName() == null)
			{
				return true;
			}
			com.db4o.reflect.ReflectClass claxx = yapClass.ClassReflector();
			if (Stream().i_handlers.ICLASS_INTERNAL.IsAssignableFrom(claxx))
			{
				return true;
			}
			return false;
		}

		public override void LoadFromIdReader(com.db4o.YapReader reader)
		{
			throw new System.NotImplementedException();
		}

		public override void LoadFromQuery(com.db4o.QQuery query)
		{
			_iterable = new _AnonymousInnerClass84(this, query);
		}

		private sealed class _AnonymousInnerClass84 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass84(LazyQueryResult _enclosing, com.db4o.QQuery query)
			{
				this._enclosing = _enclosing;
				this.query = query;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return query.ExecuteLazy();
			}

			private readonly LazyQueryResult _enclosing;

			private readonly com.db4o.QQuery query;
		}

		public override int Size()
		{
			throw new System.NotImplementedException();
		}

		public override void Sort(com.db4o.query.QueryComparator cmp)
		{
			throw new System.NotImplementedException();
		}

		public override com.db4o.inside.query.AbstractQueryResult SupportSize()
		{
			return ToIdTree();
		}

		public override com.db4o.inside.query.AbstractQueryResult SupportSort()
		{
			return ToIdList();
		}

		public override com.db4o.inside.query.AbstractQueryResult SupportElementAccess()
		{
			return ToIdList();
		}

		protected override int KnownSize()
		{
			return 0;
		}

		public override com.db4o.inside.query.AbstractQueryResult ToIdList()
		{
			return ToIdTree().ToIdList();
		}
	}
}
