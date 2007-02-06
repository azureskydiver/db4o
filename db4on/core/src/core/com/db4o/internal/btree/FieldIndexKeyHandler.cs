namespace com.db4o.@internal.btree
{
	/// <exclude></exclude>
	public class FieldIndexKeyHandler : com.db4o.@internal.ix.Indexable4
	{
		private readonly com.db4o.@internal.ix.Indexable4 _valueHandler;

		private readonly com.db4o.@internal.handlers.IntHandler _parentIdHandler;

		public FieldIndexKeyHandler(com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.ix.Indexable4
			 delegate_)
		{
			_parentIdHandler = new com.db4o.@internal.IDHandler(stream);
			_valueHandler = delegate_;
		}

		public virtual object ComparableObject(com.db4o.@internal.Transaction trans, object
			 indexEntry)
		{
			throw new System.NotImplementedException();
		}

		public virtual int LinkLength()
		{
			return _valueHandler.LinkLength() + com.db4o.@internal.Const4.INT_LENGTH;
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.Buffer a_reader)
		{
			int parentID = ReadParentID(a_reader);
			object objPart = _valueHandler.ReadIndexEntry(a_reader);
			if (parentID < 0)
			{
				objPart = null;
				parentID = -parentID;
			}
			return new com.db4o.@internal.btree.FieldIndexKey(parentID, objPart);
		}

		private int ReadParentID(com.db4o.@internal.Buffer a_reader)
		{
			return ((int)_parentIdHandler.ReadIndexEntry(a_reader));
		}

		public virtual void WriteIndexEntry(com.db4o.@internal.Buffer writer, object obj)
		{
			com.db4o.@internal.btree.FieldIndexKey composite = (com.db4o.@internal.btree.FieldIndexKey
				)obj;
			int parentID = composite.ParentID();
			object value = composite.Value();
			if (value == null)
			{
				parentID = -parentID;
			}
			_parentIdHandler.Write(parentID, writer);
			_valueHandler.WriteIndexEntry(writer, composite.Value());
		}

		public virtual com.db4o.@internal.ix.Indexable4 ValueHandler()
		{
			return _valueHandler;
		}

		public virtual com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			com.db4o.@internal.btree.FieldIndexKey composite = (com.db4o.@internal.btree.FieldIndexKey
				)obj;
			_valueHandler.PrepareComparison(composite.Value());
			_parentIdHandler.PrepareComparison(composite.ParentID());
			return this;
		}

		public virtual int CompareTo(object obj)
		{
			if (null == obj)
			{
				throw new System.ArgumentNullException();
			}
			com.db4o.@internal.btree.FieldIndexKey composite = (com.db4o.@internal.btree.FieldIndexKey
				)obj;
			int delegateResult = _valueHandler.CompareTo(composite.Value());
			if (delegateResult != 0)
			{
				return delegateResult;
			}
			return _parentIdHandler.CompareTo(composite.ParentID());
		}

		public virtual bool IsEqual(object obj)
		{
			throw new System.NotImplementedException();
		}

		public virtual bool IsGreater(object obj)
		{
			throw new System.NotImplementedException();
		}

		public virtual bool IsSmaller(object obj)
		{
			throw new System.NotImplementedException();
		}

		public virtual object Current()
		{
			return new com.db4o.@internal.btree.FieldIndexKey(_parentIdHandler.CurrentInt(), 
				_valueHandler.Current());
		}

		public virtual void DefragIndexEntry(com.db4o.@internal.ReaderPair readers)
		{
			_parentIdHandler.DefragIndexEntry(readers);
			_valueHandler.DefragIndexEntry(readers);
		}
	}
}
