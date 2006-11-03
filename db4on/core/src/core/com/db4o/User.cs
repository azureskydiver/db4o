namespace com.db4o
{
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class User : com.db4o.Internal4
	{
		public string name;

		public string password;

		public User()
		{
		}

		public User(string name, string password)
		{
			this.name = name;
			this.password = password;
		}
	}
}
