namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class TimeStampIdGenerator
	{
		private long _next;

		public static long idToMilliseconds(long id)
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

		public virtual long generate()
		{
			long t = j4o.lang.JavaSystem.currentTimeMillis();
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

		public virtual long minimumNext()
		{
			return _next;
		}

		public virtual void setMinimumNext(long newMinimum)
		{
			_next = newMinimum;
		}
	}
}
