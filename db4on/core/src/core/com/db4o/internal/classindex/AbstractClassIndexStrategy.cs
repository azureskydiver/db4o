namespace com.db4o.@internal.classindex
{
	/// <exclude></exclude>
	public abstract class AbstractClassIndexStrategy : com.db4o.@internal.classindex.ClassIndexStrategy
	{
		protected readonly com.db4o.@internal.ClassMetadata _yapClass;

		public AbstractClassIndexStrategy(com.db4o.@internal.ClassMetadata yapClass)
		{
			_yapClass = yapClass;
		}

		protected virtual int YapClassID()
		{
			return _yapClass.GetID();
		}

		public virtual int OwnLength()
		{
			return com.db4o.@internal.Const4.ID_LENGTH;
		}

		protected abstract void InternalAdd(com.db4o.@internal.Transaction trans, int id);

		public void Add(com.db4o.@internal.Transaction trans, int id)
		{
			CheckId(id);
			InternalAdd(trans, id);
		}

		protected abstract void InternalRemove(com.db4o.@internal.Transaction ta, int id);

		public void Remove(com.db4o.@internal.Transaction ta, int id)
		{
			CheckId(id);
			InternalRemove(ta, id);
		}

		private void CheckId(int id)
		{
		}

		public abstract System.Collections.IEnumerator AllSlotIDs(com.db4o.@internal.Transaction
			 arg1);

		public abstract void DefragIndex(com.db4o.@internal.ReaderPair arg1);

		public abstract void DefragReference(com.db4o.@internal.ClassMetadata arg1, com.db4o.@internal.ReaderPair
			 arg2, int arg3);

		public abstract void DontDelete(com.db4o.@internal.Transaction arg1, int arg2);

		public abstract int EntryCount(com.db4o.@internal.Transaction arg1);

		public abstract int Id();

		public abstract void Initialize(com.db4o.@internal.ObjectContainerBase arg1);

		public abstract void Purge();

		public abstract void Read(com.db4o.@internal.ObjectContainerBase arg1, int arg2);

		public abstract void TraverseAll(com.db4o.@internal.Transaction arg1, com.db4o.foundation.Visitor4
			 arg2);

		public abstract int Write(com.db4o.@internal.Transaction arg1);
	}
}
