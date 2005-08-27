
namespace com.db4o
{
	/// <exclude></exclude>
	public class IntArrayList
	{
		internal const int INC = 20;

		protected int[] i_content;

		private int i_current;

		private int i_count;

		public IntArrayList() : this(INC)
		{
		}

		public IntArrayList(int initialSize)
		{
			i_content = new int[initialSize];
		}

		public virtual void add(int a_value)
		{
			if (i_count >= i_content.Length)
			{
				int[] temp = new int[i_content.Length + INC];
				j4o.lang.JavaSystem.arraycopy(i_content, 0, temp, 0, i_content.Length);
				i_content = temp;
			}
			i_content[i_count++] = a_value;
		}

		public virtual int indexOf(int a_value)
		{
			for (int i = 0; i < i_count; i++)
			{
				if (i_content[i] == a_value)
				{
					return i;
				}
			}
			return -1;
		}

		public virtual int size()
		{
			return i_count;
		}

		public virtual void reset()
		{
			i_current = i_count - 1;
		}

		public virtual bool hasNext()
		{
			return i_current >= 0;
		}

		public virtual int nextInt()
		{
			return i_content[i_current--];
		}

		public virtual long[] asLong()
		{
			long[] longs = new long[i_count];
			for (int i = 0; i < i_count; i++)
			{
				longs[i] = i_content[i];
			}
			return longs;
		}
	}
}
