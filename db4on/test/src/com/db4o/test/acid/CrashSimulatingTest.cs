using com.db4o.test;
using System.IO;

namespace com.db4o.test.acid
{
	public class CrashSimulatingTest
	{
		public string _name;

		public com.db4o.test.acid.CrashSimulatingTest _next;

		private static readonly string PATH = "TEMP/crashSimulate";

		private static readonly string FILE = PATH + "/cs";

		public CrashSimulatingTest()
		{
		}

		public CrashSimulatingTest(com.db4o.test.acid.CrashSimulatingTest next_, string name
			)
		{
			_next = next_;
			_name = name;
		}

		public virtual void test()
		{
			if (Tester.isClientServer())
			{
				return;
			}
			new j4o.io.File(FILE).delete();
			new j4o.io.File(PATH).mkdirs();
			createFile();
			com.db4o.test.acid.CrashSimulatingIoAdapter adapterFactory = new com.db4o.test.acid.CrashSimulatingIoAdapter
				(new com.db4o.io.RandomAccessFileAdapter());
			com.db4o.Db4o.configure().io(adapterFactory);
			com.db4o.ObjectContainer oc = com.db4o.Db4o.openFile(FILE);
			com.db4o.ObjectSet objectSet = oc.get(new com.db4o.test.acid.CrashSimulatingTest(
				null, "three"));
			oc.delete(objectSet.next());
			oc.set(new com.db4o.test.acid.CrashSimulatingTest(null, "four"));
			oc.set(new com.db4o.test.acid.CrashSimulatingTest(null, "five"));
			oc.set(new com.db4o.test.acid.CrashSimulatingTest(null, "six"));
			oc.set(new com.db4o.test.acid.CrashSimulatingTest(null, "seven"));
			oc.set(new com.db4o.test.acid.CrashSimulatingTest(null, "eight"));
			oc.set(new com.db4o.test.acid.CrashSimulatingTest(null, "nine"));
			oc.commit();
			oc.close();
			com.db4o.Db4o.configure().io(new com.db4o.io.RandomAccessFileAdapter());
			int count = adapterFactory.batch.writeVersions(FILE);
			checkFiles("R", adapterFactory.batch.numSyncs());
			checkFiles("W", count);
			j4o.lang.JavaSystem._out.println("Total versions: " + count);
		}

		private void checkFiles(string infix, int count)
		{
			for (int i = 1; i <= count; i++)
			{
				string fileName = FILE + infix + i;
				com.db4o.ObjectContainer oc = com.db4o.Db4o.openFile(fileName);
				if (!stateBeforeCommit(oc))
				{
					if (!stateAfterCommit(oc))
					{
						Tester.error();
					}
				}
				oc.close();
			}
		}

		private bool stateBeforeCommit(com.db4o.ObjectContainer oc)
		{
			return expect(oc, new string[] { "one", "two", "three" });
		}

		private bool stateAfterCommit(com.db4o.ObjectContainer oc)
		{
			return expect(oc, new string[] { "one", "two", "four", "five", "six", "seven", "eight"
				, "nine" });
		}

		private bool expect(com.db4o.ObjectContainer oc, string[] names)
		{
			com.db4o.ObjectSet objectSet = oc.query(j4o.lang.Class.getClassForType(typeof(com.db4o.test.acid.CrashSimulatingTest

				)));
			if (objectSet.size() != names.Length)
			{
				return false;
			}
			while (objectSet.hasNext())
			{
				com.db4o.test.acid.CrashSimulatingTest cst = (com.db4o.test.acid.CrashSimulatingTest
					)objectSet.next();
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

		private void createFile()
		{
			com.db4o.ObjectContainer oc = com.db4o.Db4o.openFile(FILE);
			for (int i = 0; i < 10; i++)
			{
				SimplestPossible sp=new SimplestPossible();
				sp.name="delme";
				oc.set(sp);
			}
			com.db4o.test.acid.CrashSimulatingTest one = new com.db4o.test.acid.CrashSimulatingTest
				(null, "one");
			com.db4o.test.acid.CrashSimulatingTest two = new com.db4o.test.acid.CrashSimulatingTest
				(one, "two");
			com.db4o.test.acid.CrashSimulatingTest three = new com.db4o.test.acid.CrashSimulatingTest
				(one, "three");
			oc.set(one);
			oc.set(two);
			oc.set(three);
			oc.commit();
			com.db4o.ObjectSet objectSet = oc.query(j4o.lang.Class.getClassForType(typeof(com.db4o.test.SimplestPossible
				)));
			while (objectSet.hasNext())
			{
				oc.delete(objectSet.next());
			}
			oc.close();
			File.Copy(FILE,FILE + "0");
		}

		public override string ToString()
		{
			return _name + " -> " + _next;
		}
	}
}
