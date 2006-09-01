namespace com.db4o.foundation
{
	public interface KeyValueIterator
	{
		bool MoveNext();

		object Key();

		object Value();
	}
}
