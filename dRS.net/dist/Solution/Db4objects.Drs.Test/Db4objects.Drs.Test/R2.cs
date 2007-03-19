namespace Db4objects.Drs.Test
{
	public class R2 : Db4objects.Drs.Test.R1
	{
		internal Db4objects.Drs.Test.R0 circle2;

		internal Db4objects.Drs.Test.R3 r3;

		public virtual Db4objects.Drs.Test.R0 GetCircle2()
		{
			return circle2;
		}

		public virtual void SetCircle2(Db4objects.Drs.Test.R0 circle2)
		{
			this.circle2 = circle2;
		}

		public virtual Db4objects.Drs.Test.R3 GetR3()
		{
			return r3;
		}

		public virtual void SetR3(Db4objects.Drs.Test.R3 r3)
		{
			this.r3 = r3;
		}
	}
}
