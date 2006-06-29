namespace com.db4o.inside.convert
{
	public abstract class Conversion : j4o.lang.Runnable
	{
		protected com.db4o.YapFile _yapFile;

		public virtual void SetFile(com.db4o.YapFile yapFile)
		{
			_yapFile = yapFile;
		}

		public abstract void Run();
	}
}
