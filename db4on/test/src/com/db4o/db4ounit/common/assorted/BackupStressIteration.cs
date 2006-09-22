namespace com.db4o.db4ounit.common.assorted
{
	public class BackupStressIteration
	{
		public int _count;

		public BackupStressIteration()
		{
		}

		public virtual void SetCount(int count)
		{
			_count = count;
		}

		public virtual int GetCount()
		{
			return _count;
		}
	}
}
