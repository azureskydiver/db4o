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

		public StaticField(string name, object value)
		{
			this.name = name;
			this.value = value;
		}
	}
}
