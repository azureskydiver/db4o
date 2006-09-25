namespace com.db4o.inside.freespace
{
	internal abstract class FreespaceIx
	{
		internal com.db4o.inside.ix.Index4 _index;

		internal com.db4o.inside.ix.IndexTransaction _indexTrans;

		internal com.db4o.inside.ix.IxTraverser _traverser;

		internal com.db4o.inside.freespace.FreespaceVisitor _visitor;

		internal FreespaceIx(com.db4o.YapFile file, com.db4o.MetaIndex metaIndex)
		{
			_index = new com.db4o.inside.ix.Index4(file.GetSystemTransaction(), new com.db4o.YInt
				(file), metaIndex, false);
			_indexTrans = _index.GlobalIndexTransaction();
		}

		internal abstract void Add(int address, int length);

		internal abstract int Address();

		public virtual void Debug()
		{
		}

		public virtual int EntryCount()
		{
			return com.db4o.foundation.Tree.Size(_indexTrans.GetRoot());
		}

		internal virtual void Find(int val)
		{
			_traverser = new com.db4o.inside.ix.IxTraverser();
			_traverser.FindBoundsExactMatch(val, (com.db4o.inside.ix.IxTree)_indexTrans.GetRoot
				());
		}

		internal abstract int Length();

		internal virtual bool Match()
		{
			_visitor = new com.db4o.inside.freespace.FreespaceVisitor();
			_traverser.VisitMatch(_visitor);
			return _visitor.Visited();
		}

		internal virtual bool Preceding()
		{
			_visitor = new com.db4o.inside.freespace.FreespaceVisitor();
			_traverser.VisitPreceding(_visitor);
			return _visitor.Visited();
		}

		internal abstract void Remove(int address, int length);

		internal virtual bool Subsequent()
		{
			_visitor = new com.db4o.inside.freespace.FreespaceVisitor();
			_traverser.VisitSubsequent(_visitor);
			return _visitor.Visited();
		}

		public virtual void Traverse(com.db4o.foundation.Visitor4 visitor)
		{
			com.db4o.foundation.Tree.Traverse(_indexTrans.GetRoot(), visitor);
		}
	}
}
