namespace com.db4o.foundation
{
	public class FilteredIterator : com.db4o.foundation.MappingIterator
	{
		private readonly com.db4o.foundation.Predicate4 _filter;

		public FilteredIterator(System.Collections.IEnumerator iterator, com.db4o.foundation.Predicate4
			 filter) : base(iterator)
		{
			_filter = filter;
		}

		protected override object Map(object current)
		{
			return _filter.Match(current) ? current : com.db4o.foundation.MappingIterator.SKIP;
		}
	}
}
