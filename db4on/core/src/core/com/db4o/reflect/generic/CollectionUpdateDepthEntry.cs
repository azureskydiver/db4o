namespace com.db4o.reflect.generic
{
	internal class CollectionUpdateDepthEntry
	{
		internal readonly com.db4o.reflect.ReflectClassPredicate _predicate;

		internal readonly int _depth;

		internal CollectionUpdateDepthEntry(com.db4o.reflect.ReflectClassPredicate predicate
			, int depth)
		{
			_predicate = predicate;
			_depth = depth;
		}
	}
}
