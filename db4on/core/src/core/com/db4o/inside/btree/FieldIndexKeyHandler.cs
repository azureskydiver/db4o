namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class FieldIndexKeyHandler : com.db4o.inside.ix.Indexable4
	{
		private readonly com.db4o.YInt _integerHandler;

		private readonly com.db4o.inside.ix.Indexable4 _delegate;

		public FieldIndexKeyHandler(com.db4o.YapStream stream, com.db4o.inside.ix.Indexable4
			 delegate_)
		{
			_integerHandler = new com.db4o.YInt(stream);
			_delegate = delegate_;
		}

		public virtual object ComparableObject(com.db4o.Transaction trans, object indexEntry
			)
		{
			return indexEntry;
		}

		public virtual int LinkLength()
		{
			return _delegate.LinkLength() + com.db4o.YapConst.INT_LENGTH;
		}

		public virtual object ReadIndexEntry(com.db4o.YapReader a_reader)
		{
			int intPart = (int)_integerHandler.ReadIndexEntry(a_reader);
			object objPart = _delegate.ReadIndexEntry(a_reader);
			return new com.db4o.inside.btree.FieldIndexKey(intPart, objPart);
		}

		public virtual void WriteIndexEntry(com.db4o.YapReader writer, object obj)
		{
			com.db4o.inside.btree.FieldIndexKey composite = Cast(obj);
			_integerHandler.Write(composite.ParentID(), writer);
			_delegate.WriteIndexEntry(writer, composite.Value());
		}

		private com.db4o.inside.btree.FieldIndexKey Cast(object obj)
		{
			return (com.db4o.inside.btree.FieldIndexKey)obj;
		}

		public virtual com.db4o.YapComparable PrepareComparison(object obj)
		{
			com.db4o.inside.btree.FieldIndexKey composite = Cast(obj);
			_delegate.PrepareComparison(composite.Value());
			_integerHandler.PrepareComparison(composite.ParentID());
			return this;
		}

		public virtual int CompareTo(object obj)
		{
			if (null == obj)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.inside.btree.FieldIndexKey composite = Cast(obj);
			int delegateResult = _delegate.CompareTo(composite.Value());
			if (delegateResult != 0)
			{
				return delegateResult;
			}
			return _integerHandler.CompareTo(composite.ParentID());
		}

		public virtual bool IsEqual(object obj)
		{
			return CompareTo(obj) == 0;
		}

		public virtual bool IsGreater(object obj)
		{
			return CompareTo(obj) > 0;
		}

		public virtual bool IsSmaller(object obj)
		{
			return CompareTo(obj) < 0;
		}

		public virtual object Current()
		{
			return new com.db4o.inside.btree.FieldIndexKey(_integerHandler.CurrentInt(), _delegate
				.Current());
		}
	}
}
