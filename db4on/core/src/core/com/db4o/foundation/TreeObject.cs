namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class TreeObject : com.db4o.foundation.Tree
	{
		private readonly object _object;

		private readonly com.db4o.foundation.Comparison4 _function;

		public TreeObject(object @object, com.db4o.foundation.Comparison4 function)
		{
			_object = @object;
			_function = function;
		}

		public override int Compare(com.db4o.foundation.Tree tree)
		{
			return _function.Compare(_object, tree.Key());
		}

		public override object Key()
		{
			return _object;
		}
	}
}
