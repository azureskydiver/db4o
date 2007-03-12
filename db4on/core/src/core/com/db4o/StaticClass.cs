namespace com.db4o
{
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class StaticClass : com.db4o.Internal4
	{
		public string name;

		public com.db4o.StaticField[] fields;

		public StaticClass()
		{
		}

		public StaticClass(string name_, com.db4o.StaticField[] fields_)
		{
			name = name_;
			fields = fields_;
		}
	}
}
