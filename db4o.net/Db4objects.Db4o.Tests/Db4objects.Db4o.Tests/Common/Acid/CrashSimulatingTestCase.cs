using System.IO;
using Db4oUnit;
using Db4oUnit.Extensions.Fixtures;
using Db4objects.Db4o;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Foundation.IO;
using Db4objects.Db4o.IO;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Tests.Common.Acid;
using Db4objects.Db4o.Tests.Common.Assorted;

namespace Db4objects.Db4o.Tests.Common.Acid
{
	public class CrashSimulatingTestCase : ITestCase, IOptOutCS
	{
		public string _name;

		public Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase _next;

		internal const bool LOG = false;

		public CrashSimulatingTestCase()
		{
		}

		public CrashSimulatingTestCase(Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase
			 next_, string name)
		{
			_next = next_;
			_name = name;
		}

		private bool HasLockFileThread()
		{
			if (!Platform4.HasLockFileThread())
			{
				return false;
			}
			return !Platform4.HasNio();
		}

		public virtual void Test()
		{
			if (HasLockFileThread())
			{
				Sharpen.Runtime.Out.WriteLine("CrashSimulatingTestCase is ignored on platforms with lock file thread."
					);
				return;
			}
			string path = Path.Combine(Path.GetTempPath(), "crashSimulate");
			string fileName = Path.Combine(path, "cs");
			File4.Delete(fileName);
			System.IO.Directory.CreateDirectory(path);
			Db4oFactory.Configure().BTreeNodeSize(4);
			CreateFile(fileName);
			CrashSimulatingIoAdapter adapterFactory = new CrashSimulatingIoAdapter(new RandomAccessFileAdapter
				());
			Db4oFactory.Configure().Io(adapterFactory);
			IObjectContainer oc = Db4oFactory.OpenFile(fileName);
			IObjectSet objectSet = oc.Get(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase
				(null, "three"));
			oc.Delete(objectSet.Next());
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "four"
				));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "five"
				));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "six")
				);
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "seven"
				));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "eight"
				));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "nine"
				));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "10"));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "11"));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "12"));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "13"));
			oc.Set(new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase(null, "14"));
			oc.Commit();
			IQuery q = oc.Query();
			q.Constrain(typeof(Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase));
			objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase cst = (Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase
					)objectSet.Next();
				if (!(cst._name.Equals("10") || cst._name.Equals("13")))
				{
					oc.Delete(cst);
				}
			}
			oc.Commit();
			oc.Close();
			Db4oFactory.Configure().Io(new RandomAccessFileAdapter());
			int count = adapterFactory.batch.WriteVersions(fileName);
			CheckFiles(fileName, "R", adapterFactory.batch.NumSyncs());
			CheckFiles(fileName, "W", count);
		}

		private void CheckFiles(string fileName, string infix, int count)
		{
			for (int i = 1; i <= count; i++)
			{
				string versionedFileName = fileName + infix + i;
				IObjectContainer oc = Db4oFactory.OpenFile(versionedFileName);
				try
				{
					if (!StateBeforeCommit(oc))
					{
						if (!StateAfterFirstCommit(oc))
						{
							Assert.IsTrue(StateAfterSecondCommit(oc));
						}
					}
				}
				finally
				{
					oc.Close();
				}
			}
		}

		private bool StateBeforeCommit(IObjectContainer oc)
		{
			return Expect(oc, new string[] { "one", "two", "three" });
		}

		private bool StateAfterFirstCommit(IObjectContainer oc)
		{
			return Expect(oc, new string[] { "one", "two", "four", "five", "six", "seven", "eight"
				, "nine", "10", "11", "12", "13", "14" });
		}

		private bool StateAfterSecondCommit(IObjectContainer oc)
		{
			return Expect(oc, new string[] { "10", "13" });
		}

		private bool Expect(IObjectContainer container, string[] names)
		{
			Collection4 expected = new Collection4(names);
			IObjectSet actual = container.Query(typeof(Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase)
				);
			while (actual.HasNext())
			{
				Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase current = (Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase
					)actual.Next();
				if (null == expected.Remove(current._name))
				{
					return false;
				}
			}
			return expected.IsEmpty();
		}

		private void CreateFile(string fileName)
		{
			IObjectContainer oc = Db4oFactory.OpenFile(fileName);
			try
			{
				Populate(oc);
			}
			finally
			{
				oc.Close();
			}
			File4.Copy(fileName, fileName + "0");
		}

		private void Populate(IObjectContainer container)
		{
			for (int i = 0; i < 10; i++)
			{
				container.Set(new SimplestPossibleItem("delme"));
			}
			Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase one = new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase
				(null, "one");
			Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase two = new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase
				(one, "two");
			Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase three = new Db4objects.Db4o.Tests.Common.Acid.CrashSimulatingTestCase
				(one, "three");
			container.Set(one);
			container.Set(two);
			container.Set(three);
			container.Commit();
			IObjectSet objectSet = container.Query(typeof(SimplestPossibleItem));
			while (objectSet.HasNext())
			{
				container.Delete(objectSet.Next());
			}
		}

		public override string ToString()
		{
			return _name + " -> " + _next;
		}
	}
}
