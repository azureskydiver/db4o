namespace com.db4o.foundation
{
	/// <summary>Iterator primitives (concat, map, reduce, filter, etc...).</summary>
	/// <remarks>Iterator primitives (concat, map, reduce, filter, etc...).</remarks>
	/// <exclude></exclude>
	public class Iterators
	{
		public static readonly System.Collections.IEnumerator EMPTY_ITERATOR = new com.db4o.foundation.Iterator4Impl
			(null);

		private sealed class _AnonymousInnerClass15 : System.Collections.IEnumerable
		{
			public _AnonymousInnerClass15()
			{
			}

			public System.Collections.IEnumerator GetEnumerator()
			{
				return com.db4o.foundation.Iterators.EMPTY_ITERATOR;
			}
		}

		public static readonly System.Collections.IEnumerable EMPTY_ITERABLE = new _AnonymousInnerClass15
			();

		internal static readonly object NO_ELEMENT = new object();

		public static System.Collections.IEnumerator Concat(System.Collections.IEnumerator
			 iterators)
		{
			return new com.db4o.foundation.CompositeIterator4(iterators);
		}

		public static System.Collections.IEnumerator Map(System.Collections.IEnumerator iterator
			, com.db4o.foundation.Function4 function)
		{
			return new com.db4o.foundation.FunctionApplicationIterator(iterator, function);
		}

		public static System.Collections.IEnumerator Map(object[] array, com.db4o.foundation.Function4
			 function)
		{
			return Map(new com.db4o.foundation.ArrayIterator4(array), function);
		}

		public static System.Collections.IEnumerator Filter(object[] array, com.db4o.foundation.Predicate4
			 predicate)
		{
			return Filter(new com.db4o.foundation.ArrayIterator4(array), predicate);
		}

		public static System.Collections.IEnumerator Filter(System.Collections.IEnumerator
			 iterator, com.db4o.foundation.Predicate4 predicate)
		{
			return new com.db4o.foundation.FilteredIterator(iterator, predicate);
		}

		public static System.Collections.IEnumerator Iterate(object[] array)
		{
			return new com.db4o.foundation.ArrayIterator4(array);
		}

		public static int Size(System.Collections.IEnumerable iterable)
		{
			return Size(iterable.GetEnumerator());
		}

		private static int Size(System.Collections.IEnumerator iterator)
		{
			int count = 0;
			while (iterator.MoveNext())
			{
				++count;
			}
			return count;
		}

		public static string ToString(System.Collections.IEnumerator i)
		{
			if (!i.MoveNext())
			{
				return "[]";
			}
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			sb.Append("[");
			sb.Append(i.Current);
			while (i.MoveNext())
			{
				sb.Append(", ");
				sb.Append(i.Current);
			}
			sb.Append("]");
			return sb.ToString();
		}
	}
}
