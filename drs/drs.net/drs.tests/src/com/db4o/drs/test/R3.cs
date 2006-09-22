namespace com.db4o.drs.test
{
	public class R3 : com.db4o.drs.test.R2
	{
		internal com.db4o.drs.test.R0 circle3;

		internal com.db4o.drs.test.R4 r4;

		public virtual com.db4o.drs.test.R0 GetCircle3()
		{
			return circle3;
		}

		public virtual void SetCircle3(com.db4o.drs.test.R0 circle3)
		{
			this.circle3 = circle3;
		}

		public virtual com.db4o.drs.test.R4 GetR4()
		{
			return r4;
		}

		public virtual void SetR4(com.db4o.drs.test.R4 r4)
		{
			this.r4 = r4;
		}
	}
}
