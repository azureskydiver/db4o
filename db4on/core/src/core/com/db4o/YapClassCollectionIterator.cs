namespace com.db4o
{
	internal class YapClassCollectionIterator : com.db4o.foundation.Iterator4
	{
		private readonly com.db4o.YapClassCollection i_collection;

		internal YapClassCollectionIterator(com.db4o.YapClassCollection a_collection, com.db4o.foundation.List4
			 a_first) : base(a_first)
		{
			i_collection = a_collection;
		}

		internal virtual com.db4o.YapClass nextClass()
		{
			com.db4o.YapClass yc = (com.db4o.YapClass)next();
			i_collection.readYapClass(yc, null);
			return yc;
		}
	}
}
