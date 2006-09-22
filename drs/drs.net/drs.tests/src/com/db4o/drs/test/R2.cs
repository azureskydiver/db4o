namespace com.db4o.drs.test
{
	public class R2 : com.db4o.drs.test.R1
	{
		internal com.db4o.drs.test.R0 circle2;

		internal com.db4o.drs.test.R3 r3;

		public virtual com.db4o.drs.test.R0 GetCircle2()
		{
			return circle2;
		}

		public virtual void SetCircle2(com.db4o.drs.test.R0 circle2)
		{
			this.circle2 = circle2;
		}

		public virtual com.db4o.drs.test.R3 GetR3()
		{
			return r3;
		}

		public virtual void SetR3(com.db4o.drs.test.R3 r3)
		{
			this.r3 = r3;
		}
	}
}
