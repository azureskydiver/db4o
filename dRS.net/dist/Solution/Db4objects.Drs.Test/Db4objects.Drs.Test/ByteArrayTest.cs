namespace Db4objects.Drs.Test
{
	/// <summary>
	/// Design of this case is copied from
	/// com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.
	/// </summary>
	/// <remarks>
	/// Design of this case is copied from
	/// com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.
	/// </remarks>
	public class ByteArrayTest : Db4objects.Drs.Test.DrsTestCase
	{
		internal const int ARRAY_LENGTH = 5;

		internal static byte[] initial = CreateByteArray();

		internal static byte[] modInB = new byte[] { 2, 3, 5, 68, 69 };

		internal static byte[] modInA = new byte[] { 15, 36, 55, 8, 9, 28, 65 };

		public virtual void Test()
		{
			StoreInA();
			Replicate();
			ModifyInB();
			Replicate2();
			ModifyInA();
			Replicate3();
		}

		private void StoreInA()
		{
			Db4objects.Drs.Test.IIByteArrayHolder byteArrayHolder = new Db4objects.Drs.Test.ByteArrayHolder
				(CreateByteArray());
			A().Provider().StoreNew(byteArrayHolder);
			A().Provider().Commit();
			EnsureNames(A(), initial);
		}

		private void Replicate()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureNames(A(), initial);
			EnsureNames(B(), initial);
		}

		private void ModifyInB()
		{
			Db4objects.Drs.Test.IIByteArrayHolder c = GetTheObject(B());
			c.SetBytes(modInB);
			B().Provider().Update(c);
			B().Provider().Commit();
			EnsureNames(B(), modInB);
		}

		private void Replicate2()
		{
			ReplicateAll(B().Provider(), A().Provider());
			EnsureNames(A(), modInB);
			EnsureNames(B(), modInB);
		}

		private void ModifyInA()
		{
			Db4objects.Drs.Test.IIByteArrayHolder c = GetTheObject(A());
			c.SetBytes(modInA);
			A().Provider().Update(c);
			A().Provider().Commit();
			EnsureNames(A(), modInA);
		}

		private void Replicate3()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureNames(A(), modInA);
			EnsureNames(B(), modInA);
		}

		private void EnsureNames(Db4objects.Drs.Test.IDrsFixture fixture, byte[] bs)
		{
			EnsureOneInstance(fixture, typeof(Db4objects.Drs.Test.IIByteArrayHolder));
			Db4objects.Drs.Test.IIByteArrayHolder c = GetTheObject(fixture);
			Db4oUnit.ArrayAssert.AreEqual(c.GetBytes(), bs);
		}

		private Db4objects.Drs.Test.IIByteArrayHolder GetTheObject(Db4objects.Drs.Test.IDrsFixture
			 fixture)
		{
			return (Db4objects.Drs.Test.IIByteArrayHolder)GetOneInstance(fixture, typeof(Db4objects.Drs.Test.IIByteArrayHolder)
				);
		}

		internal static byte[] CreateByteArray()
		{
			byte[] bytes = new byte[ARRAY_LENGTH];
			for (byte i = 0; i < bytes.Length; ++i)
			{
				bytes[i] = i;
			}
			return bytes;
		}
	}
}
