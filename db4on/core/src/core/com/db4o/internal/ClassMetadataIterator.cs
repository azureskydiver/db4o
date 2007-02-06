namespace com.db4o.@internal
{
	/// <exclude>TODO: remove this class or make it private to ClassMetadataRepository</exclude>
	public class ClassMetadataIterator : com.db4o.foundation.MappingIterator
	{
		private readonly com.db4o.@internal.ClassMetadataRepository i_collection;

		internal ClassMetadataIterator(com.db4o.@internal.ClassMetadataRepository a_collection
			, System.Collections.IEnumerator iterator) : base(iterator)
		{
			i_collection = a_collection;
		}

		public virtual com.db4o.@internal.ClassMetadata CurrentClass()
		{
			return (com.db4o.@internal.ClassMetadata)Current;
		}

		protected override object Map(object current)
		{
			return i_collection.ReadYapClass((com.db4o.@internal.ClassMetadata)current, null);
		}
	}
}
