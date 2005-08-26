namespace com.db4o
{
	internal abstract class Config4Abstract
	{
		internal int i_cascadeOnActivate = 1;

		internal int i_cascadeOnDelete = 0;

		internal int i_cascadeOnUpdate = 0;

		internal string i_name;

		public virtual void cascadeOnActivate(bool flag)
		{
			i_cascadeOnActivate = flag ? 1 : -1;
		}

		public virtual void cascadeOnDelete(bool flag)
		{
			i_cascadeOnDelete = flag ? 1 : -1;
		}

		public virtual void cascadeOnUpdate(bool flag)
		{
			i_cascadeOnUpdate = flag ? 1 : -1;
		}

		internal abstract string className();

		public override bool Equals(object obj)
		{
			return i_name.Equals(((com.db4o.Config4Abstract)obj).i_name);
		}

		public virtual string getName()
		{
			return i_name;
		}
	}
}
