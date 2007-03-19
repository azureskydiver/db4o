namespace Db4objects.Drs.Inside.Traversal
{
	public interface ICollectionFlattener
	{
		bool CanHandle(Db4objects.Db4o.Reflect.IReflectClass claxx);

		bool CanHandle(object obj);

		bool CanHandle(System.Type c);

		System.Collections.IEnumerator IteratorFor(object collection);
	}
}
