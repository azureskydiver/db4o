namespace com.db4o
{
	/// <exclude></exclude>
	public class TreeStringObject : com.db4o.TreeString
	{
		public readonly object _object;

		public TreeStringObject(string a_key, object a_object) : base(a_key)
		{
			this._object = a_object;
		}

		public override object ShallowClone()
		{
			com.db4o.TreeStringObject tso = new com.db4o.TreeStringObject(_key, _object);
			return ShallowCloneInternal(tso);
		}
	}
}
