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
			_index = new com.db4o.inside.ix.Index4(file.getSystemTransaction(), new com.db4o.YInt
				(file), metaIndex, false);
			_indexTrans = _index.globalIndexTransaction();
		}

		internal abstract void add(int address, int length);

		internal abstract int address();

		public virtual void debug()
		{
		}

		internal virtual void find(int val)
		{
			_traverser = new com.db4o.inside.ix.IxTraverser();
			_traverser.findBoundsExactMatch(val, (com.db4o.inside.ix.IxTree)_indexTrans.getRoot
				());
		}

		internal abstract int length();

		internal virtual bool match()
		{
			_visitor = new com.db4o.inside.freespace.FreespaceVisitor();
			_traverser.visitMatch(_visitor);
			return _visitor.visited();
		}

		internal virtual bool preceding()
		{
			_visitor = new com.db4o.inside.freespace.FreespaceVisitor();
			_traverser.visitPreceding(_visitor);
			return _visitor.visited();
		}

		internal abstract void remove(int address, int length);

		internal virtual bool subsequent()
		{
			_visitor = new com.db4o.inside.freespace.FreespaceVisitor();
			_traverser.visitSubsequent(_visitor);
			return _visitor.visited();
		}
	}
}
