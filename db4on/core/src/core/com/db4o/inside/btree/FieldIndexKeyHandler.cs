namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public class FieldIndexKeyHandler : com.db4o.inside.ix.Indexable4
	{
		private readonly com.db4o.inside.ix.Indexable4 _valueHandler;

		private readonly com.db4o.YInt _parentIdHandler;

		public FieldIndexKeyHandler(com.db4o.YapStream stream, com.db4o.inside.ix.Indexable4
			 delegate_)
		{
			_parentIdHandler = new com.db4o.YInt(stream);
			_valueHandler = delegate_;
		}

		public virtual object ComparableObject(com.db4o.Transaction trans, object indexEntry
			)
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual int LinkLength()
		{
			return _valueHandler.LinkLength() + com.db4o.YapConst.INT_LENGTH;
		}

		public virtual object ReadIndexEntry(com.db4o.YapReader a_reader)
		{
			int parentID = ((int)_parentIdHandler.ReadIndexEntry(a_reader));
			object objPart = _valueHandler.ReadIndexEntry(a_reader);
			if (parentID < 0)
			{
				objPart = null;
				parentID = -parentID;
			}
			return new com.db4o.inside.btree.FieldIndexKey(parentID, objPart);
		}

		public virtual void WriteIndexEntry(com.db4o.YapReader writer, object obj)
		{
			com.db4o.inside.btree.FieldIndexKey composite = (com.db4o.inside.btree.FieldIndexKey
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

		public virtual com.db4o.YapComparable PrepareComparison(object obj)
		{
			com.db4o.inside.btree.FieldIndexKey composite = (com.db4o.inside.btree.FieldIndexKey
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
			com.db4o.inside.btree.FieldIndexKey composite = (com.db4o.inside.btree.FieldIndexKey
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
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual bool IsGreater(object obj)
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual bool IsSmaller(object obj)
		{
			throw new com.db4o.foundation.NotImplementedException();
		}

		public virtual object Current()
		{
			return new com.db4o.inside.btree.FieldIndexKey(_parentIdHandler.CurrentInt(), _valueHandler
				.Current());
		}
	}
}
