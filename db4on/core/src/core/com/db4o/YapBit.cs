namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class YapBit
	{
		private int i_value;

		public YapBit(int a_value)
		{
			i_value = a_value;
		}

		internal void set(bool a_bit)
		{
			i_value = i_value * 2;
			if (a_bit)
			{
				i_value++;
			}
		}

		public bool get()
		{
			double cmp = (double)i_value / 2;
			i_value = i_value / 2;
			return (cmp != i_value);
		}

		internal byte getByte()
		{
			return (byte)i_value;
		}
	}
}
