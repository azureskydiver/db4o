namespace com.db4o.db4ounit.common.assorted
{
	public class BackupStressItem
	{
		public string _name;

		public int _iteration;

		public BackupStressItem()
		{
		}

		public BackupStressItem(string name, int iteration)
		{
			_name = name;
			_iteration = iteration;
		}
	}
}
