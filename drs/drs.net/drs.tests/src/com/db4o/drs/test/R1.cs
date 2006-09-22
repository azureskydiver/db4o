namespace com.db4o.drs.test
{
	public class R1 : com.db4o.drs.test.R0
	{
		internal com.db4o.drs.test.R0 circle1;

		internal com.db4o.drs.test.R2 r2;

		public virtual com.db4o.drs.test.R0 GetCircle1()
		{
			return circle1;
		}

		public virtual void SetCircle1(com.db4o.drs.test.R0 circle1)
		{
			this.circle1 = circle1;
		}

		public virtual com.db4o.drs.test.R2 GetR2()
		{
			return r2;
		}

		public virtual void SetR2(com.db4o.drs.test.R2 r2)
		{
			this.r2 = r2;
		}
	}
}
