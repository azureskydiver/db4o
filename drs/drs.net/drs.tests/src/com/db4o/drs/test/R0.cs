namespace com.db4o.drs.test
{
	public class R0
	{
		internal string name;

		internal com.db4o.drs.test.R0 r0;

		internal com.db4o.drs.test.R1 r1;

		public virtual string GetName()
		{
			return name;
		}

		public virtual void SetName(string name)
		{
			this.name = name;
		}

		public virtual com.db4o.drs.test.R0 GetR0()
		{
			return r0;
		}

		public virtual void SetR0(com.db4o.drs.test.R0 r0)
		{
			this.r0 = r0;
		}

		public virtual com.db4o.drs.test.R1 GetR1()
		{
			return r1;
		}

		public virtual void SetR1(com.db4o.drs.test.R1 r1)
		{
			this.r1 = r1;
		}
	}
}
