namespace com.db4o
{
	/// <exclude></exclude>
	public class Null : com.db4o.inside.ix.Indexable4
	{
		public static readonly com.db4o.inside.ix.Indexable4 INSTANCE = new com.db4o.Null
			();

		public virtual object comparableObject(com.db4o.Transaction trans, object indexEntry
			)
		{
			return null;
		}

		public virtual int compareTo(object a_obj)
		{
			if (a_obj == null)
			{
				return 0;
			}
			return -1;
		}

		public virtual object current()
		{
			return null;
		}

		public override bool Equals(object obj)
		{
			return obj == null;
		}

		public virtual bool isEqual(object obj)
		{
			return obj == null;
		}

		public virtual bool isGreater(object obj)
		{
			return false;
		}

		public virtual bool isSmaller(object obj)
		{
			return false;
		}

		public virtual int linkLength()
		{
			return 0;
		}

		public virtual com.db4o.YapComparable prepareComparison(object obj)
		{
			return this;
		}

		public virtual object readIndexEntry(com.db4o.YapReader a_reader)
		{
			return null;
		}

		public virtual void writeIndexEntry(com.db4o.YapReader a_writer, object a_object)
		{
		}
	}
}
