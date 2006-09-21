namespace com.db4o.drs.inside
{
	public interface CollectionHandler : com.db4o.drs.inside.traversal.CollectionFlattener
	{
		object EmptyClone(object originalCollection, com.db4o.reflect.ReflectClass originalCollectionClass
			);

		void CopyState(object original, object dest, com.db4o.drs.inside.CounterpartFinder
			 finder);

		object CloneWithCounterparts(object original, com.db4o.reflect.ReflectClass claxx
			, com.db4o.drs.inside.CounterpartFinder elementCloner);
	}
}
