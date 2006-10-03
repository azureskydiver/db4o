namespace com.db4o.drs.inside.traversal
{
	public interface CollectionFlattener
	{
		bool CanHandle(com.db4o.reflect.ReflectClass claxx);

		bool CanHandle(object obj);

		bool CanHandle(System.Type c);

		com.db4o.foundation.Iterator4 IteratorFor(object collection);
	}
}
