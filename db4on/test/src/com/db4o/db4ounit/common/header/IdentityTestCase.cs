namespace com.db4o.db4ounit.common.header
{
	public class IdentityTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
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
			((com.db4o.YapFile)Db()).GenerateNewIdentity();
			Reopen();
			Db4oUnit.ArrayAssert.AreNotEqual(oldSignature, Db().Identity().GetSignature());
		}
	}
}
