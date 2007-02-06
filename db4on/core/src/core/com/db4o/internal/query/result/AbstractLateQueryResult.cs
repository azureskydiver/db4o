namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public abstract class AbstractLateQueryResult : com.db4o.@internal.query.result.AbstractQueryResult
	{
		protected System.Collections.IEnumerable _iterable;

		public AbstractLateQueryResult(com.db4o.@internal.Transaction transaction) : base
			(transaction)
		{
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult SupportSize()
		{
			return ToIdTree();
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult SupportSort()
		{
			return ToIdList();
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult SupportElementAccess
			()
		{
			return ToIdList();
		}

		protected override int KnownSize()
		{
			return 0;
		}

		public override com.db4o.foundation.IntIterator4 IterateIDs()
		{
			if (_iterable == null)
			{
				throw new System.InvalidOperationException();
			}
			return new com.db4o.foundation.IntIterator4Adaptor(_iterable);
		}

		public override com.db4o.@internal.query.result.AbstractQueryResult ToIdList()
		{
			return ToIdTree().ToIdList();
		}

		public virtual bool SkipClass(com.db4o.@internal.ClassMetadata yapClass)
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

		protected virtual System.Collections.IEnumerable ClassIndexesIterable(com.db4o.@internal.ClassMetadataIterator
			 classCollectionIterator)
		{
			return new _AnonymousInnerClass61(this, classCollectionIterator);
		}

		private sealed class _AnonymousInnerClass61 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass61(AbstractLateQueryResult _enclosing, com.db4o.@internal.ClassMetadataIterator
				 classCollectionIterator)
			{
				this._enclosing = _enclosing;
				this.classCollectionIterator = classCollectionIterator;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return new com.db4o.foundation.CompositeIterator4(new _AnonymousInnerClass64(this
					, classCollectionIterator));
			}

			private sealed class _AnonymousInnerClass64 : com.db4o.foundation.MappingIterator
			{
				public _AnonymousInnerClass64(_AnonymousInnerClass61 _enclosing, com.db4o.@internal.ClassMetadataIterator
					 baseArg1) : base(baseArg1)
				{
					this._enclosing = _enclosing;
				}

				protected override object Map(object current)
				{
					com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)current;
					if (this._enclosing._enclosing.SkipClass(yapClass))
					{
						return com.db4o.foundation.MappingIterator.SKIP;
					}
					return this._enclosing._enclosing.ClassIndexIterator(yapClass);
				}

				private readonly _AnonymousInnerClass61 _enclosing;
			}

			private readonly AbstractLateQueryResult _enclosing;

			private readonly com.db4o.@internal.ClassMetadataIterator classCollectionIterator;
		}

		protected virtual System.Collections.IEnumerable ClassIndexIterable(com.db4o.@internal.ClassMetadata
			 clazz)
		{
			return new _AnonymousInnerClass79(this, clazz);
		}

		private sealed class _AnonymousInnerClass79 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass79(AbstractLateQueryResult _enclosing, com.db4o.@internal.ClassMetadata
				 clazz)
			{
				this._enclosing = _enclosing;
				this.clazz = clazz;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return this._enclosing.ClassIndexIterator(clazz);
			}

			private readonly AbstractLateQueryResult _enclosing;

			private readonly com.db4o.@internal.ClassMetadata clazz;
		}

		public virtual System.Collections.IEnumerator ClassIndexIterator(com.db4o.@internal.ClassMetadata
			 clazz)
		{
			return com.db4o.@internal.classindex.BTreeClassIndexStrategy.Iterate(clazz, Transaction
				());
		}
	}
}
