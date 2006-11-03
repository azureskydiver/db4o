namespace com.db4o.db4ounit.common.assorted
{
	public class BackupStressTestCase : Db4oUnit.TestLifeCycle
	{
		private static bool verbose = false;

		private static bool runOnOldJDK = false;

		private static readonly string FILE = "backupstress.yap";

		private const int ITERATIONS = 5;

		private const int OBJECTS = 50;

		private const int COMMITS = 10;

		private com.db4o.ObjectContainer _objectContainer;

		private volatile bool _inBackup;

		private volatile bool _noMoreBackups;

		private int _backups;

		private int _commitCounter;

		public static void Main(string[] args)
		{
			verbose = true;
			runOnOldJDK = true;
			com.db4o.db4ounit.common.assorted.BackupStressTestCase stressTest = new com.db4o.db4ounit.common.assorted.BackupStressTestCase
				();
			stressTest.SetUp();
			stressTest.Test();
		}

		public virtual void SetUp()
		{
			com.db4o.Db4o.Configure().ObjectClass(typeof(com.db4o.db4ounit.common.assorted.BackupStressItem)
				).ObjectField("_iteration").Indexed(true);
		}

		public virtual void TearDown()
		{
		}

		public virtual void Test()
		{
			OpenDatabase();
			try
			{
				RunTestIterations();
			}
			finally
			{
				CloseDatabase();
			}
			CheckBackups();
		}

		private void RunTestIterations()
		{
			if (!runOnOldJDK && IsOldJDK())
			{
				j4o.lang.JavaSystem.Out.WriteLine("BackupStressTest is too slow for regression testing on Java JDKs < 1.4"
					);
				return;
			}
			com.db4o.db4ounit.common.assorted.BackupStressIteration iteration = new com.db4o.db4ounit.common.assorted.BackupStressIteration
				();
			_objectContainer.Set(iteration);
			_objectContainer.Commit();
			StartBackupThread();
			for (int i = 1; i <= ITERATIONS; i++)
			{
				for (int obj = 0; obj < OBJECTS; obj++)
				{
					_objectContainer.Set(new com.db4o.db4ounit.common.assorted.BackupStressItem("i" +
						 obj, i));
					_commitCounter++;
					if (_commitCounter >= COMMITS)
					{
						_objectContainer.Commit();
						_commitCounter = 0;
					}
				}
				iteration.SetCount(i);
				_objectContainer.Set(iteration);
				_objectContainer.Commit();
			}
		}

		private void StartBackupThread()
		{
			new j4o.lang.Thread(new _AnonymousInnerClass91(this)).Start();
		}

		private sealed class _AnonymousInnerClass91 : j4o.lang.Runnable
		{
			public _AnonymousInnerClass91(BackupStressTestCase _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Run()
			{
				while (!this._enclosing._noMoreBackups)
				{
					this._enclosing._backups++;
					string fileName = this._enclosing.BackupFile(this._enclosing._backups);
					this._enclosing.DeleteFile(fileName);
					try
					{
						this._enclosing._inBackup = true;
						this._enclosing._objectContainer.Ext().Backup(fileName);
						this._enclosing._inBackup = false;
					}
					catch (System.IO.IOException e)
					{
						j4o.lang.JavaSystem.PrintStackTrace(e);
					}
				}
			}

			private readonly BackupStressTestCase _enclosing;
		}

		private void OpenDatabase()
		{
			DeleteFile(FILE);
			_objectContainer = com.db4o.Db4o.OpenFile(FILE);
		}

		private void CloseDatabase()
		{
			_noMoreBackups = true;
			while (_inBackup)
			{
				j4o.lang.Thread.Sleep(1000);
			}
			_objectContainer.Close();
		}

		private void CheckBackups()
		{
			Stdout("BackupStressTest");
			Stdout("Backups created: " + _backups);
			for (int i = 1; i < _backups; i++)
			{
				Stdout("Backup " + i);
				com.db4o.ObjectContainer container = com.db4o.Db4o.OpenFile(BackupFile(i));
				try
				{
					Stdout("Open successful");
					com.db4o.query.Query q = container.Query();
					q.Constrain(typeof(com.db4o.db4ounit.common.assorted.BackupStressIteration));
					com.db4o.db4ounit.common.assorted.BackupStressIteration iteration = (com.db4o.db4ounit.common.assorted.BackupStressIteration
						)q.Execute().Next();
					int iterations = iteration.GetCount();
					Stdout("Iterations in backup: " + iterations);
					if (iterations > 0)
					{
						q = container.Query();
						q.Constrain(typeof(com.db4o.db4ounit.common.assorted.BackupStressItem));
						q.Descend("_iteration").Constrain(iteration.GetCount());
						com.db4o.ObjectSet items = q.Execute();
						Db4oUnit.Assert.AreEqual(OBJECTS, items.Size());
						while (items.HasNext())
						{
							com.db4o.db4ounit.common.assorted.BackupStressItem item = (com.db4o.db4ounit.common.assorted.BackupStressItem
								)items.Next();
							Db4oUnit.Assert.AreEqual(iterations, item._iteration);
						}
					}
				}
				finally
				{
					container.Close();
				}
				Stdout("Backup OK");
			}
			j4o.lang.JavaSystem.Out.WriteLine("BackupStressTest " + _backups + " files OK.");
			for (int i = 1; i <= _backups; i++)
			{
				DeleteFile(BackupFile(i));
			}
			DeleteFile(FILE);
		}

		private bool DeleteFile(string fname)
		{
			return new j4o.io.File(fname).Delete();
		}

		private bool IsOldJDK()
		{
			com.db4o.YapStream stream = (com.db4o.YapStream)_objectContainer;
			return stream.NeedsLockFileThread();
		}

		private string BackupFile(int count)
		{
			return string.Empty + count + FILE;
		}

		private void Stdout(string @string)
		{
			if (verbose)
			{
				j4o.lang.JavaSystem.Out.WriteLine(@string);
			}
		}
	}
}
