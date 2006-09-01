namespace com.db4o
{
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class StaticField : com.db4o.Internal4
	{
		public string name;

		public object value;

		public StaticField()
		{
		}

		public StaticField(string name_, object value_)
		{
			name = name_;
			value = value_;
		}
	}
}
