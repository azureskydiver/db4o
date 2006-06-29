namespace com.db4o.inside.ix
{
	/// <summary>Node for index tree, can be addition or removal node</summary>
	public abstract class IxPatch : com.db4o.inside.ix.IxTree
	{
		internal int _parentID;

		internal object _value;

		private com.db4o.foundation.Queue4 _queue;

		internal IxPatch(com.db4o.inside.ix.IndexTransaction a_ft, int a_parentID, object
			 a_value) : base(a_ft)
		{
			_parentID = a_parentID;
			_value = a_value;
		}

		public override com.db4o.Tree Add(com.db4o.Tree a_new)
		{
			int cmp = Compare(a_new);
			if (cmp == 0)
			{
				com.db4o.inside.ix.IxPatch patch = (com.db4o.inside.ix.IxPatch)a_new;
				cmp = _parentID - patch._parentID;
				if (cmp == 0)
				{
					com.db4o.foundation.Queue4 queue = _queue;
					if (queue == null)
					{
						queue = new com.db4o.foundation.Queue4();
						queue.Add(this);
					}
					queue.Add(patch);
					patch._queue = queue;
					patch._subsequent = _subsequent;
					patch._preceding = _preceding;
					patch.CalculateSize();
					return patch;
				}
			}
			return Add(a_new, cmp);
		}

		public override int Compare(com.db4o.Tree a_to)
		{
			com.db4o.inside.ix.Indexable4 handler = _fieldTransaction.i_index._handler;
			return handler.CompareTo(handler.ComparableObject(Trans(), _value));
		}

		public virtual bool HasQueue()
		{
			return _queue != null;
		}

		public virtual com.db4o.foundation.Queue4 DetachQueue()
		{
			com.db4o.foundation.Queue4 queue = _queue;
			this._queue = null;
			return queue;
		}

		protected override com.db4o.Tree ShallowCloneInternal(com.db4o.Tree tree)
		{
			com.db4o.inside.ix.IxPatch patch = (com.db4o.inside.ix.IxPatch)base.ShallowCloneInternal
				(tree);
			patch._parentID = _parentID;
			patch._value = _value;
			patch._queue = _queue;
			return patch;
		}
	}
}
