/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System.Collections;
using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4o.Foundation
{
	public class FilteredIterator : MappingIterator
	{
		private readonly IPredicate4 _filter;

		public FilteredIterator(IEnumerator iterator, IPredicate4 filter) : base(iterator
			)
		{
			_filter = filter;
		}

		protected override object Map(object current)
		{
			return _filter.Match(current) ? current : MappingIterator.SKIP;
		}
	}
}
