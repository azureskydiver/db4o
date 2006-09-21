namespace com.db4o.drs.test
{
	public class ListHolder
	{
		private string name;

		private System.Collections.IList list;

		public ListHolder()
		{
		}

		public ListHolder(string name)
		{
			this.name = name;
		}

		public virtual void Add(com.db4o.drs.test.ListContent obj)
		{
			list.Add(obj);
		}

		public virtual string GetName()
		{
			return name;
		}

		public virtual void SetName(string name)
		{
			this.name = name;
		}

		public virtual System.Collections.IList GetList()
		{
			return list;
		}

		public virtual void SetList(System.Collections.IList list)
		{
			this.list = list;
		}

		public override string ToString()
		{
			return "name = " + name + ", list = " + list;
		}
	}
}
