namespace com.db4o.drs.test
{
	public class R4 : com.db4o.drs.test.R3
	{
		internal com.db4o.drs.test.R0 circle4;

		public virtual com.db4o.drs.test.R0 GetCircle4()
		{
			return circle4;
		}

		public virtual void SetCircle4(com.db4o.drs.test.R0 circle4)
		{
			this.circle4 = circle4;
		}
	}
}
