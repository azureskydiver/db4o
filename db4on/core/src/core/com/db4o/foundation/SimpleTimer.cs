namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class SimpleTimer : j4o.lang.Runnable
	{
		private readonly j4o.lang.Runnable _runnable;

		private readonly int _interval;

		public volatile bool stopped = false;

		public SimpleTimer(j4o.lang.Runnable runnable, int interval, string name)
		{
			_runnable = runnable;
			_interval = interval;
			j4o.lang.Thread thread = new j4o.lang.Thread(this);
			thread.setDaemon(true);
			thread.setName(name);
			thread.start();
		}

		public virtual void stop()
		{
			stopped = true;
		}

		public virtual void run()
		{
			while (!stopped)
			{
				com.db4o.foundation.Cool.sleepIgnoringInterruption(_interval);
				if (!stopped)
				{
					_runnable.run();
				}
			}
		}
	}
}
