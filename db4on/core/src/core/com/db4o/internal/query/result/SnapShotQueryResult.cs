namespace com.db4o.@internal.query.result
{
	/// <exclude></exclude>
	public class SnapShotQueryResult : com.db4o.@internal.query.result.AbstractLateQueryResult
	{
		public SnapShotQueryResult(com.db4o.@internal.Transaction transaction) : base(transaction
			)
		{
		}

		public override void LoadFromClassIndex(com.db4o.@internal.ClassMetadata clazz)
		{
			CreateSnapshot(ClassIndexIterable(clazz));
		}

		public override void LoadFromClassIndexes(com.db4o.@internal.ClassMetadataIterator
			 classCollectionIterator)
		{
			CreateSnapshot(ClassIndexesIterable(classCollectionIterator));
		}

		public override void LoadFromQuery(com.db4o.@internal.query.processor.QQuery query
			)
		{
			System.Collections.IEnumerator _iterator = query.ExecuteSnapshot();
			_iterable = new _AnonymousInnerClass29(this, _iterator);
		}

		private sealed class _AnonymousInnerClass29 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass29(SnapShotQueryResult _enclosing, System.Collections.IEnumerator
				 _iterator)
			{
				this._enclosing = _enclosing;
				this._iterator = _iterator;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				_iterator.Reset();
				return _iterator;
			}

			private readonly SnapShotQueryResult _enclosing;

			private readonly System.Collections.IEnumerator _iterator;
		}

		private void CreateSnapshot(System.Collections.IEnumerable iterable)
		{
			com.db4o.foundation.Tree ids = com.db4o.@internal.TreeInt.AddAll(null, new com.db4o.foundation.IntIterator4Adaptor
				(iterable));
			_iterable = new _AnonymousInnerClass39(this, ids);
		}

		private sealed class _AnonymousInnerClass39 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass39(SnapShotQueryResult _enclosing, com.db4o.foundation.Tree
				 ids)
			{
				this._enclosing = _enclosing;
				this.ids = ids;
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return new com.db4o.foundation.TreeKeyIterator(ids);
			}

			private readonly SnapShotQueryResult _enclosing;

			private readonly com.db4o.foundation.Tree ids;
		}
	}
}
