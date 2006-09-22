namespace com.db4o
{
	/// <exclude></exclude>
	public class YapClassCollectionIterator : com.db4o.foundation.Iterator4Impl
	{
		private readonly com.db4o.YapClassCollection i_collection;

		internal YapClassCollectionIterator(com.db4o.YapClassCollection a_collection, com.db4o.foundation.List4
			 a_first) : base(a_first)
		{
			i_collection = a_collection;
		}

		public override bool MoveNext()
		{
			if (base.MoveNext())
			{
				i_collection.ReadYapClass(CurrentClass(), null);
				return true;
			}
			return false;
		}

		public virtual com.db4o.YapClass CurrentClass()
		{
			return (com.db4o.YapClass)Current();
		}
	}
}
