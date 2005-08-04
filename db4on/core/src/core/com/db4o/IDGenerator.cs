namespace com.db4o
{
	internal class IDGenerator
	{
		private int id = 0;

		internal virtual int next()
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
