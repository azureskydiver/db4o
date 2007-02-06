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

		public User(string name_, string password_)
		{
			name = name_;
			password = password_;
		}
	}
}
