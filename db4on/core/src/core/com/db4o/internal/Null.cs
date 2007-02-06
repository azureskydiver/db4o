namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class Null : com.db4o.@internal.ix.Indexable4
	{
		public static readonly com.db4o.@internal.ix.Indexable4 INSTANCE = new com.db4o.@internal.Null
			();

		public virtual object ComparableObject(com.db4o.@internal.Transaction trans, object
			 indexEntry)
		{
			return null;
		}

		public virtual int CompareTo(object a_obj)
		{
			if (a_obj == null)
			{
				return 0;
			}
			return -1;
		}

		public virtual object Current()
		{
			return null;
		}

		public virtual bool IsEqual(object obj)
		{
			return obj == null;
		}

		public virtual bool IsGreater(object obj)
		{
			return false;
		}

		public virtual bool IsSmaller(object obj)
		{
			return false;
		}

		public virtual int LinkLength()
		{
			return 0;
		}

		public virtual com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			return this;
		}

		public virtual object ReadIndexEntry(com.db4o.@internal.Buffer a_reader)
		{
			return null;
		}

		public virtual void WriteIndexEntry(com.db4o.@internal.Buffer a_writer, object a_object
			)
		{
		}

		public virtual void DefragIndexEntry(com.db4o.@internal.ReaderPair readers)
		{
		}
	}
}
