
namespace com.db4o
{
	/// <exclude></exclude>
	public class TreeStringObject : com.db4o.TreeString
	{
		public readonly object i_object;

		public TreeStringObject(string a_key, object a_object) : base(a_key)
		{
			this.i_object = a_object;
		}
	}
}
