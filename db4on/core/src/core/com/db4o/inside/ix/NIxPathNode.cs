namespace com.db4o.inside.ix
{
	public class NIxPathNode
	{
		internal com.db4o.inside.ix.IxTree _tree;

		internal int _comparisonResult;

		internal int[] _lowerAndUpperMatch;

		internal com.db4o.inside.ix.NIxPathNode _next;

		/// <summary>
		/// returns 0, if keys are equal
		/// returns negative if compared key (a_to) is smaller
		/// returns positive if compared key (a_to) is greater
		/// </summary>
		public virtual int compare(com.db4o.inside.ix.NIxPathNode other)
		{
			if (_next == null)
			{
				if (other._next != null)
				{
					return other.ascending() ? 1 : -1;
				}
				if (_lowerAndUpperMatch == null)
				{
					return 0;
				}
				if (_lowerAndUpperMatch[0] != other._lowerAndUpperMatch[0])
				{
					return other._lowerAndUpperMatch[0] - _lowerAndUpperMatch[0];
				}
				if (_lowerAndUpperMatch[1] != other._lowerAndUpperMatch[1])
				{
					return other._lowerAndUpperMatch[1] - _lowerAndUpperMatch[1];
				}
				return 0;
			}
			if (other._next == null)
			{
				return ascending() ? -1 : 1;
			}
			com.db4o.inside.ix.IxTree otherNext = other._next._tree;
			if (_tree.i_subsequent == otherNext)
			{
				return 1;
			}
			return -1;
		}

		internal virtual bool ascending()
		{
			return _tree.i_subsequent == _next._tree;
		}
	}
}
