namespace com.db4o.db4ounit.common.acid
{
	public class CrashSimulatingTestCase : Db4oUnit.TestCase, Db4oUnit.Extensions.Fixtures.OptOutCS
	{
		public string _name;

		public com.db4o.db4ounit.common.acid.CrashSimulatingTestCase _next;

		private static readonly string PATH = System.IO.Path.Combine(System.IO.Path.GetTempPath
			(), "crashSimulate");

		private static readonly string FILE = System.IO.Path.Combine(PATH, "cs");

		internal const bool LOG = false;

		public CrashSimulatingTestCase()
		{
		}

		public CrashSimulatingTestCase(com.db4o.db4ounit.common.acid.CrashSimulatingTestCase
			 next_, string name)
		{
			_next = next_;
			_name = name;
		}

		public virtual void Test()
		{
			com.db4o.foundation.io.File4.Delete(FILE);
			System.IO.Directory.CreateDirectory(PATH);
			com.db4o.Db4o.Configure().BTreeNodeSize(4);
			CreateFile();
			com.db4o.db4ounit.common.acid.CrashSimulatingIoAdapter adapterFactory = new com.db4o.db4ounit.common.acid.CrashSimulatingIoAdapter
				(new com.db4o.io.RandomAccessFileAdapter());
			com.db4o.Db4o.Configure().Io(adapterFactory);
			com.db4o.ObjectContainer oc = com.db4o.Db4o.OpenFile(FILE);
			com.db4o.ObjectSet objectSet = oc.Get(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase
				(null, "three"));
			oc.Delete(objectSet.Next());
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "four"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "five"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "six"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "seven"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "eight"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "nine"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "10"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "11"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "12"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "13"));
			oc.Set(new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase(null, "14"));
			oc.Commit();
			com.db4o.query.Query q = oc.Query();
			q.Constrain(typeof(com.db4o.db4ounit.common.acid.CrashSimulatingTestCase));
			objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				com.db4o.db4ounit.common.acid.CrashSimulatingTestCase cst = (com.db4o.db4ounit.common.acid.CrashSimulatingTestCase
					)objectSet.Next();
				if (!(cst._name.Equals("10") || cst._name.Equals("13")))
				{
					oc.Delete(cst);
				}
			}
			oc.Commit();
			oc.Close();
			com.db4o.Db4o.Configure().Io(new com.db4o.io.RandomAccessFileAdapter());
			int count = adapterFactory.batch.WriteVersions(FILE);
			CheckFiles("R", adapterFactory.batch.NumSyncs());
			CheckFiles("W", count);
			j4o.lang.JavaSystem.Out.WriteLine("Total versions: " + count);
		}

		private void CheckFiles(string infix, int count)
		{
			for (int i = 1; i <= count; i++)
			{
				string fileName = FILE + infix + i;
				com.db4o.ObjectContainer oc = com.db4o.Db4o.OpenFile(fileName);
				if (!StateBeforeCommit(oc))
				{
					if (!StateAfterFirstCommit(oc))
					{
						Db4oUnit.Assert.IsTrue(StateAfterSecondCommit(oc));
					}
				}
				oc.Close();
			}
		}

		private bool StateBeforeCommit(com.db4o.ObjectContainer oc)
		{
			return Expect(oc, new string[] { "one", "two", "three" });
		}

		private bool StateAfterFirstCommit(com.db4o.ObjectContainer oc)
		{
			return Expect(oc, new string[] { "one", "two", "four", "five", "six", "seven", "eight"
				, "nine", "10", "11", "12", "13", "14" });
		}

		private bool StateAfterSecondCommit(com.db4o.ObjectContainer oc)
		{
			return Expect(oc, new string[] { "10", "13" });
		}

		private bool Expect(com.db4o.ObjectContainer oc, string[] names)
		{
			com.db4o.ObjectSet objectSet = oc.Query(typeof(com.db4o.db4ounit.common.acid.CrashSimulatingTestCase)
				);
			if (objectSet.Size() != names.Length)
			{
				return false;
			}
			while (objectSet.HasNext())
			{
				com.db4o.db4ounit.common.acid.CrashSimulatingTestCase cst = (com.db4o.db4ounit.common.acid.CrashSimulatingTestCase
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
				oc.Set(new com.db4o.db4ounit.common.assorted.SimplestPossibleItem("delme"));
			}
			com.db4o.db4ounit.common.acid.CrashSimulatingTestCase one = new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase
				(null, "one");
			com.db4o.db4ounit.common.acid.CrashSimulatingTestCase two = new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase
				(one, "two");
			com.db4o.db4ounit.common.acid.CrashSimulatingTestCase three = new com.db4o.db4ounit.common.acid.CrashSimulatingTestCase
				(one, "three");
			oc.Set(one);
			oc.Set(two);
			oc.Set(three);
			oc.Commit();
			com.db4o.ObjectSet objectSet = oc.Query(typeof(com.db4o.db4ounit.common.assorted.SimplestPossibleItem)
				);
			while (objectSet.HasNext())
			{
				oc.Delete(objectSet.Next());
			}
			oc.Close();
			com.db4o.foundation.io.File4.Copy(FILE, FILE + "0");
		}

		public override string ToString()
		{
			return _name + " -> " + _next;
		}
	}
}
