namespace Db4objects.Drs.Test
{
	public class R3 : Db4objects.Drs.Test.R2
	{
		internal Db4objects.Drs.Test.R0 circle3;

		internal Db4objects.Drs.Test.R4 r4;

		public virtual Db4objects.Drs.Test.R0 GetCircle3()
		{
			return circle3;
		}

		public virtual void SetCircle3(Db4objects.Drs.Test.R0 circle3)
		{
			this.circle3 = circle3;
		}

		public virtual Db4objects.Drs.Test.R4 GetR4()
		{
			return r4;
		}

		public virtual void SetR4(Db4objects.Drs.Test.R4 r4)
		{
			this.r4 = r4;
		}
	}
}
