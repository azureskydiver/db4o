namespace com.db4o.foundation
{
	public class StopWatch
	{
		private long _started;

		private long _elapsed;

		public StopWatch()
		{
		}

		public virtual void Start()
		{
			_started = j4o.lang.JavaSystem.CurrentTimeMillis();
		}

		public virtual void Stop()
		{
			_elapsed = j4o.lang.JavaSystem.CurrentTimeMillis() - _started;
		}

		public virtual long Elapsed()
		{
			return _elapsed;
		}
	}
}
