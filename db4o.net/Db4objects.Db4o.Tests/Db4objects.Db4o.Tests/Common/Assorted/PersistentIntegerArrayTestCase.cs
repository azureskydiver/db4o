using Db4oUnit;
using Db4oUnit.Extensions;
using Db4oUnit.Extensions.Fixtures;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Tests.Common.Assorted;

namespace Db4objects.Db4o.Tests.Common.Assorted
{
	/// <exclude></exclude>
	public class PersistentIntegerArrayTestCase : AbstractDb4oTestCase, IOptOutCS
	{
		public static void Main(string[] arguments)
		{
			new PersistentIntegerArrayTestCase().RunSolo();
		}

		public virtual void Test()
		{
			int[] original = new int[] { 10, 99, 77 };
			PersistentIntegerArray arr = new PersistentIntegerArray(original);
			arr.Write(SystemTrans());
			int id = arr.GetID();
			Reopen();
			arr = new PersistentIntegerArray(id);
			arr.Read(SystemTrans());
			int[] copy = arr.Array();
			ArrayAssert.AreEqual(original, copy);
		}
	}
}
