namespace com.db4o.db4ounit.common.assorted
{
	public class ChangeIdentity : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public virtual void Test()
		{
			byte[] oldSignature = Db().Identity().GetSignature();
			((com.db4o.@internal.LocalObjectContainer)Db()).GenerateNewIdentity();
			Reopen();
			Db4oUnit.ArrayAssert.AreNotEqual(oldSignature, Db().Identity().GetSignature());
		}
	}
}
