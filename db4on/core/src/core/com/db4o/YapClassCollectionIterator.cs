namespace com.db4o
{
	/// <exclude></exclude>
	public class YapClassCollectionIterator : com.db4o.foundation.MappingIterator
	{
		private readonly com.db4o.YapClassCollection i_collection;

		internal YapClassCollectionIterator(com.db4o.YapClassCollection a_collection, System.Collections.IEnumerator
			 iterator) : base(iterator)
		{
			i_collection = a_collection;
		}

		public virtual com.db4o.YapClass CurrentClass()
		{
			return (com.db4o.YapClass)Current;
		}

		protected override object Map(object current)
		{
			return i_collection.ReadYapClass((com.db4o.YapClass)current, null);
		}
	}
}
