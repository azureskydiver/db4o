namespace com.db4o.drs.test
{
	public class SimpleArrayHolder
	{
		private string name;

		private com.db4o.drs.test.SimpleArrayContent[] arr;

		public SimpleArrayHolder()
		{
		}

		public SimpleArrayHolder(string name)
		{
			this.name = name;
		}

		public virtual com.db4o.drs.test.SimpleArrayContent[] GetArr()
		{
			return arr;
		}

		public virtual void SetArr(com.db4o.drs.test.SimpleArrayContent[] arr)
		{
			this.arr = arr;
		}

		public virtual string GetName()
		{
			return name;
		}

		public virtual void SetName(string name)
		{
			this.name = name;
		}

		public virtual void Add(com.db4o.drs.test.SimpleArrayContent sac)
		{
			if (arr == null)
			{
				arr = new com.db4o.drs.test.SimpleArrayContent[] { sac };
				return;
			}
			com.db4o.drs.test.SimpleArrayContent[] temp = arr;
			arr = new com.db4o.drs.test.SimpleArrayContent[temp.Length + 1];
			System.Array.Copy(temp, 0, arr, 0, temp.Length);
			arr[temp.Length] = sac;
		}
	}
}
