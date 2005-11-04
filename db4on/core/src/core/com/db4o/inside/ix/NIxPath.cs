namespace com.db4o.inside.ix
{
	public class NIxPath : com.db4o.Tree
	{
		internal com.db4o.inside.ix.NIxPathNode _head;

		internal bool _takePreceding;

		internal bool _takeSubsequent;

		public NIxPath()
		{
		}

		public NIxPath(com.db4o.inside.ix.NIxPathNode head, bool takePreceding, bool takeSubsequent
			)
		{
			_head = head;
			_takePreceding = takePreceding;
			_takeSubsequent = takeSubsequent;
		}

		public override int compare(com.db4o.Tree a_to)
		{
			return _head.compare(((com.db4o.inside.ix.NIxPath)a_to)._head);
		}
	}
}
