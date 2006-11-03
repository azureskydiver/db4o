namespace com.db4o.inside.fieldindex
{
	public class FieldIndexProcessorResult
	{
		public static readonly com.db4o.inside.fieldindex.FieldIndexProcessorResult NO_INDEX_FOUND
			 = new com.db4o.inside.fieldindex.FieldIndexProcessorResult(null);

		public static readonly com.db4o.inside.fieldindex.FieldIndexProcessorResult FOUND_INDEX_BUT_NO_MATCH
			 = new com.db4o.inside.fieldindex.FieldIndexProcessorResult(null);

		private readonly com.db4o.inside.fieldindex.IndexedNode _indexedNode;

		public FieldIndexProcessorResult(com.db4o.inside.fieldindex.IndexedNode indexedNode
			)
		{
			_indexedNode = indexedNode;
		}

		public virtual com.db4o.foundation.Tree ToQCandidate(com.db4o.QCandidates candidates
			)
		{
			return com.db4o.TreeInt.ToQCandidate(ToTreeInt(), candidates);
		}

		public virtual com.db4o.TreeInt ToTreeInt()
		{
			if (FoundMatch())
			{
				return _indexedNode.ToTreeInt();
			}
			return null;
		}

		public virtual bool FoundMatch()
		{
			return FoundIndex() && !NoMatch();
		}

		public virtual bool FoundIndex()
		{
			return this != NO_INDEX_FOUND;
		}

		public virtual bool NoMatch()
		{
			return this == FOUND_INDEX_BUT_NO_MATCH;
		}

		public virtual System.Collections.IEnumerator IterateIDs()
		{
			return new _AnonymousInnerClass46(this, _indexedNode.GetEnumerator());
		}

		private sealed class _AnonymousInnerClass46 : com.db4o.foundation.MappingIterator
		{
			public _AnonymousInnerClass46(FieldIndexProcessorResult _enclosing, System.Collections.IEnumerator
				 baseArg1) : base(baseArg1)
			{
				this._enclosing = _enclosing;
			}

			protected override object Map(object current)
			{
				com.db4o.inside.btree.FieldIndexKey composite = (com.db4o.inside.btree.FieldIndexKey
					)current;
				return composite.ParentID();
			}

			private readonly FieldIndexProcessorResult _enclosing;
		}
	}
}
