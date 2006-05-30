namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public class NIxPath : com.db4o.Tree
	{
		internal readonly com.db4o.inside.ix.NIxPathNode _head;

		internal readonly bool _takePreceding;

		internal readonly bool _takeMatches;

		internal readonly bool _takeSubsequent;

		internal int _type;

		public NIxPath(com.db4o.inside.ix.NIxPathNode head, bool takePreceding, bool takeMatches
			, bool takeSubsequent, int pathType)
		{
			_head = head;
			_takePreceding = takePreceding;
			_takeMatches = takeMatches;
			_takeSubsequent = takeSubsequent;
			_type = pathType;
		}

		public override int Compare(com.db4o.Tree a_to)
		{
			com.db4o.inside.ix.NIxPath other = (com.db4o.inside.ix.NIxPath)a_to;
			return _head.Compare(other._head, _type, other._type);
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "NIxPath +\n";
			string space = " ";
			com.db4o.inside.ix.NIxPathNode node = _head;
			while (node != null)
			{
				str += space;
				space += " ";
				str += node.ToString() + "\n";
				node = node._next;
			}
			return str;
		}

		public override object ShallowClone()
		{
			com.db4o.inside.ix.NIxPath path = new com.db4o.inside.ix.NIxPath(_head, _takePreceding
				, _takeMatches, _takeSubsequent, _type);
			base.ShallowCloneInternal(path);
			return path;
		}
	}
}
