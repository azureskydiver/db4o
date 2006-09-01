namespace com.db4o
{
	internal class ShutDownRunnable : com.db4o.foundation.Collection4, j4o.lang.Runnable
	{
		public volatile bool dontRemove = false;

		public virtual void Run()
		{
			dontRemove = true;
			com.db4o.foundation.Iterator4 i = Iterator();
			while (i.MoveNext())
			{
				((com.db4o.YapStream)i.Current()).FailedToShutDown();
			}
		}
	}
}
