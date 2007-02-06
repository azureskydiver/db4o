namespace com.db4o.@internal
{
	internal class ShutDownRunnable : com.db4o.foundation.Collection4, j4o.lang.Runnable
	{
		public volatile bool dontRemove = false;

		public virtual void Run()
		{
			dontRemove = true;
			com.db4o.foundation.Collection4 copy = new com.db4o.foundation.Collection4(this);
			System.Collections.IEnumerator i = copy.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.@internal.ObjectContainerBase)i.Current).FailedToShutDown();
			}
		}
	}
}
