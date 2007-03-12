namespace com.db4o.@internal.cs
{
	/// <summary>Defines a strategy on how to prefetch objects from the server.</summary>
	/// <remarks>Defines a strategy on how to prefetch objects from the server.</remarks>
	public interface PrefetchingStrategy
	{
		int PrefetchObjects(com.db4o.@internal.cs.ClientObjectContainer container, com.db4o.foundation.IntIterator4
			 ids, object[] prefetched, int prefetchCount);
	}
}
