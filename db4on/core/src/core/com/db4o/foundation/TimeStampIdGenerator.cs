namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class TimeStampIdGenerator
	{
		private long _next;

		public static long IdToMilliseconds(long id)
		{
			return id >> 15;
		}

		public TimeStampIdGenerator() : this(0)
		{
		}

		public TimeStampIdGenerator(long minimumNext)
		{
			_next = minimumNext;
		}

		public virtual long Generate()
		{
			long t = j4o.lang.JavaSystem.CurrentTimeMillis();
			t = t << 15;
			if (t <= _next)
			{
				_next++;
			}
			else
			{
				_next = t;
			}
			return _next;
		}

		public virtual long MinimumNext()
		{
			return _next;
		}

		public virtual void SetMinimumNext(long newMinimum)
		{
			_next = newMinimum;
		}
	}
}
