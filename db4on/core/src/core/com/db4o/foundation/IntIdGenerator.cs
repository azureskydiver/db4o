namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class IntIdGenerator
	{
		private int _current = 1;

		public virtual int Next()
		{
			_current++;
			if (_current < 0)
			{
				_current = 1;
			}
			return _current;
		}
	}
}
