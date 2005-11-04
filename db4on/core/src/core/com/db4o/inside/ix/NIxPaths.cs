namespace com.db4o.inside.ix
{
	public class NIxPaths
	{
		internal com.db4o.Tree _paths;

		internal virtual void add(com.db4o.inside.ix.NIxPath path)
		{
			_paths = com.db4o.Tree.add(_paths, path);
		}
	}
}
