namespace com.db4o.inside.btree
{
	/// <exclude></exclude>
	public sealed class SearchTarget
	{
		public static readonly com.db4o.inside.btree.SearchTarget LOWEST = new com.db4o.inside.btree.SearchTarget
			("Lowest");

		public static readonly com.db4o.inside.btree.SearchTarget ANY = new com.db4o.inside.btree.SearchTarget
			("Any");

		public static readonly com.db4o.inside.btree.SearchTarget HIGHEST = new com.db4o.inside.btree.SearchTarget
			("Highest");

		private readonly string _target;

		public SearchTarget(string target)
		{
			_target = target;
		}

		public override string ToString()
		{
			return _target;
		}
	}
}
