namespace com.db4o.defragment
{
	/// <summary>Base class for defragment ID mappings.</summary>
	/// <remarks>Base class for defragment ID mappings.</remarks>
	/// <seealso cref="com.db4o.defragment.Defragment">com.db4o.defragment.Defragment</seealso>
	public abstract class AbstractContextIDMapping : com.db4o.defragment.ContextIDMapping
	{
		private com.db4o.foundation.Hashtable4 _classIDs = new com.db4o.foundation.Hashtable4
			();

		public void MapIDs(int origID, int mappedID, bool isClassID)
		{
			if (isClassID)
			{
				MapClassIDs(origID, mappedID);
				return;
			}
			MapNonClassIDs(origID, mappedID);
		}

		protected virtual int MappedClassID(int origID)
		{
			object obj = _classIDs.Get(origID);
			if (obj == null)
			{
				return 0;
			}
			return ((int)obj);
		}

		private void MapClassIDs(int oldID, int newID)
		{
			_classIDs.Put(oldID, newID);
		}

		protected abstract void MapNonClassIDs(int origID, int mappedID);

		public abstract void Close();

		public abstract int MappedID(int arg1, bool arg2);

		public abstract void Open();
	}
}
