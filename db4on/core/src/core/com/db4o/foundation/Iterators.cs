namespace com.db4o.foundation
{
	/// <summary>Iterator primitives (cat, map, reduce, filter, etc...).</summary>
	/// <remarks>Iterator primitives (cat, map, reduce, filter, etc...).</remarks>
	/// <exclude></exclude>
	public class Iterators
	{
		internal static readonly object NO_ELEMENT = new object();

		public static com.db4o.foundation.Iterator4 Concat(com.db4o.foundation.Iterator4 
			iterators)
		{
			return new com.db4o.foundation.CompositeIterator4(iterators);
		}

		public static com.db4o.foundation.Iterator4 Map(com.db4o.foundation.Iterator4 iterator
			, com.db4o.foundation.Function4 function)
		{
			return new com.db4o.foundation.FunctionApplicationIterator(iterator, function);
		}

		public static com.db4o.foundation.Iterator4 Map(object[] array, com.db4o.foundation.Function4
			 function)
		{
			return Map(new com.db4o.foundation.ArrayIterator4(array), function);
		}
	}
}
