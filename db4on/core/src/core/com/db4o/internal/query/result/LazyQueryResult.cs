namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public class LazyQueryResult : com.db4o.@internal.query.result.AbstractLateQueryResult
	{
		public LazyQueryResult(com.db4o.@internal.Transaction trans) : base(trans)
		{
		}

		public override void LoadFromClassIndex(com.db4o.@internal.ClassMetadata clazz)
		{
			_iterable = ClassIndexIterable(clazz);
		}

		public override void LoadFromClassIndexes(com.db4o.@internal.ClassMetadataIterator
			 classCollectionIterator)
		{
			_iterable = ClassIndexesIterable(classCollectionIterator);
		}

		public override void LoadFromQuery(com.db4o.@internal.query.processor.QQuery query
			)
		{
			_iterable = new _AnonymousInnerClass28(this, query);
		}

		private sealed class _AnonymousInnerClass28 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass28(LazyQueryResult _enclosing, com.db4o.@internal.query.processor.QQuery
				 query)
			{
				this._enclosing = _enclosing;
				this.query = query;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return query.ExecuteLazy();
			}

			private readonly LazyQueryResult _enclosing;

			private readonly com.db4o.@internal.query.processor.QQuery query;
		}
	}
}
