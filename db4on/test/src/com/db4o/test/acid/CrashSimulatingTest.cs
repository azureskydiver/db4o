using com.db4o.test;
using System.IO;

namespace com.db4o.test.acid
{
	public class CrashSimulatingTest
	{
		public string _name;

		public com.db4o.test.acid.CrashSimulatingTest _next;

		private static readonly string PATH = Path.Combine(Path.GetTempPath(), "crashSimulate");

		private static readonly string FILE = Path.Combine(PATH, "cs");

		public CrashSimulatingTest()
		{
		}

		public CrashSimulatingTest(com.db4o.test.acid.CrashSimulatingTest next_, string name
			)
		{
			_next = next_;
			_name = name;
		}

		public virtual void Test()
		{
			if (Tester.IsClientServer())
			{
				return;
			}
			new j4o.io.File(FILE).Delete();
			new j4o.io.File(PATH).Mkdirs();
			CreateFile();
			com.db4o.test.acid.CrashSimulatingIoAdapter adapterFactory = new com.db4o.test.acid.CrashSimulatingIoAdapter
				(new com.db4o.io.RandomAccessFileAdapter());
			com.db4o.Db4o.Configure().Io(adapterFactory);
			com.db4o.ObjectContainer oc = com.db4o.Db4o.OpenFile(FILE);
			com.db4o.ObjectSet objectSet = oc.Get(new com.db4o.test.acid.CrashSimulatingTest(
				null, "three"));
			oc.Delete(objectSet.Next());
			oc.Set(new com.db4o.test.acid.CrashSimulatingTest(null, "four"));
			oc.Set(new com.db4o.test.acid.CrashSimulatingTest(null, "five"));
			oc.Set(new com.db4o.test.acid.CrashSimulatingTest(null, "six"));
			oc.Set(new com.db4o.test.acid.CrashSimulatingTest(null, "seven"));
			oc.Set(new com.db4o.test.acid.CrashSimulatingTest(null, "eight"));
			oc.Set(new com.db4o.test.acid.CrashSimulatingTest(null, "nine"));
			oc.Commit();
			oc.Close();
			com.db4o.Db4o.Configure().Io(new com.db4o.io.RandomAccessFileAdapter());
			int count = adapterFactory.batch.WriteVersions(FILE);
			CheckFiles("R", adapterFactory.batch.NumSyncs());
			CheckFiles("W", count);
			System.Console.WriteLine("Total versions: " + count);
		}

		private void CheckFiles(string infix, int count)
		{
			for (int i = 1; i <= count; i++)
			{
				string fileName = FILE + infix + i;
				com.db4o.ObjectContainer oc = com.db4o.Db4o.OpenFile(fileName);
				if (!StateBeforeCommit(oc))
				{

					if (!StateAfterCommit(oc))
					{
						Tester.Error();
					}
				}
				oc.Close();
			}
		}

		private bool StateBeforeCommit(com.db4o.ObjectContainer oc)
		{
			return Expect(oc, new string[] { "one", "two", "three" });
		}

		private bool StateAfterCommit(com.db4o.ObjectContainer oc)
		{
			return Expect(oc, new string[] { "one", "two", "four", "five", "six", "seven", "eight"
				, "nine" });
		}

		private bool Expect(com.db4o.ObjectContainer oc, string[] names)
		{
			com.db4o.ObjectSet objectSet = oc.Query(typeof(com.db4o.test.acid.CrashSimulatingTest
				));
			if (objectSet.Size() != names.Length)
			{
				return false;
			}
			while (objectSet.HasNext())
			{
				com.db4o.test.acid.CrashSimulatingTest cst = (com.db4o.test.acid.CrashSimulatingTest
					)objectSet.Next();
				bool found = false;
				for (int i = 0; i < names.Length; i++)
				{
					if (cst._name.Equals(names[i]))
					{
						names[i] = null;
						found = true;
						break;
					}
				}
				if (!found)
				{
					return false;
				}
			}
			for (int i = 0; i < names.Length; i++)
			{
				if (names[i] != null)
				{
					return false;
				}
			}
			return true;
		}

		private void CreateFile()
		{
			com.db4o.ObjectContainer oc = com.db4o.Db4o.OpenFile(FILE);
			for (int i = 0; i < 10; i++)
			{
				SimplestPossible sp=new SimplestPossible();
				sp.name="delme";
				oc.Set(sp);
			}
			com.db4o.test.acid.CrashSimulatingTest one = new com.db4o.test.acid.CrashSimulatingTest
				(null, "one");
			com.db4o.test.acid.CrashSimulatingTest two = new com.db4o.test.acid.CrashSimulatingTest
				(one, "two");
			com.db4o.test.acid.CrashSimulatingTest three = new com.db4o.test.acid.CrashSimulatingTest
				(one, "three");
			oc.Set(one);
			oc.Set(two);
			oc.Set(three);
			oc.Commit();
			com.db4o.ObjectSet objectSet = oc.Query(typeof(com.db4o.test.SimplestPossible));
			while (objectSet.HasNext())
			{
				oc.Delete(objectSet.Next());
			}
			oc.Close();
			File.Delete(FILE + "0");
			File.Copy(FILE,FILE + "0");
		}

		public override string ToString()
		{
			return _name + " -> " + _next;
		}
	}
}
