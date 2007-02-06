namespace com.db4o.@internal
{
	public class IDGenerator
	{
		private int id = 0;

		public virtual int Next()
		{
			id++;
			if (id > 0)
			{
				return id;
			}
			id = 1;
			return 1;
		}
	}
}
