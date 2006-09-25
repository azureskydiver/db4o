namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class MutableInt
	{
		private int _value;

		public MutableInt()
		{
		}

		public MutableInt(int value)
		{
			_value = value;
		}

		public virtual void Add(int addVal)
		{
			_value += addVal;
		}

		public virtual void Substract(int substractVal)
		{
			_value -= substractVal;
		}

		public virtual int Value()
		{
			return _value;
		}
	}
}
