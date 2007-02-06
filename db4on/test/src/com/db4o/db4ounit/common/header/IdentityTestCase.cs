namespace com.db4o.db4ounit.common.header
{
	public class IdentityTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.header.IdentityTestCase().RunSolo();
		}

		public virtual void TestIdentityPreserved()
		{
			com.db4o.ext.Db4oDatabase ident = Db().Identity();
			Reopen();
			com.db4o.ext.Db4oDatabase ident2 = Db().Identity();
			Db4oUnit.Assert.IsNotNull(ident);
			Db4oUnit.Assert.AreEqual(ident, ident2);
		}

		public virtual void TestGenerateIdentity()
		{
			byte[] oldSignature = Db().Identity().GetSignature();
			GenerateNewIdentity();
			Reopen();
			Db4oUnit.ArrayAssert.AreNotEqual(oldSignature, Db().Identity().GetSignature());
		}

		private void GenerateNewIdentity()
		{
			((com.db4o.@internal.LocalObjectContainer)Db()).GenerateNewIdentity();
		}
	}
}
