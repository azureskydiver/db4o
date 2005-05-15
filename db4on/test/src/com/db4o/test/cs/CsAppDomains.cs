/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using System.IO;
using com.db4o.query;

namespace com.db4o.test.cs
{
	public class ADTask : MarshalByRefObject
	{
		private String _name;
		
		public ADTask(string name)
		{
			_name = name;
		}
		
		public string Name
		{
			get
			{
				return _name;
			}
		}
	}

    class TaskDatabase : MarshalByRefDatabase
    {
        public string[] QueryTaskNames()
        {
            ArrayList names = new ArrayList();
            ObjectSet os = InternalQueryTasks();
            while (os.hasNext())
            {
                names.Add(((ADTask)os.next()).Name);
            }
            return (string[])names.ToArray(typeof(string));
        }

        public ADTask[] QueryTasks()
        {
            ArrayList tasks = new ArrayList();
            ObjectSet os = InternalQueryTasks();
            while (os.hasNext())
            {
                tasks.Add(os.next());
            }
            return (ADTask[])tasks.ToArray(typeof(ADTask));
        }

        private ObjectSet InternalQueryTasks()
        {
            Query query = _container.query();
            query.constrain(typeof(ADTask));
            query.descend("_name").orderAscending();
            return query.execute();
        }
    }
	
	/// <summary>
	/// Tests the interaction of db4o with multiple AppDomains
	/// </summary>
	public class CsAppDomains
	{
		// keep task objects alive to check for any identity problems
		ArrayList _tasks = new ArrayList();

		public void store()
		{
			ADTask task = null;
			Test.store(task = new ADTask("task 1"));
			_tasks.Add(task);

			Test.store(task = new ADTask("task 2"));
			_tasks.Add(task);
		}
		
		public void testRemoteDomain()
		{
			Test.closeAll();

			AppDomain domain = AppDomain.CreateDomain("db4o-remote-domain");
			try
			{
				using (TaskDatabase db = (TaskDatabase)domain.CreateInstanceAndUnwrap(typeof(TaskDatabase).Assembly.GetName().ToString(), typeof(TaskDatabase).FullName))
				{
					db.Open(currentFileName(), Test.isClientServer());
				
					string[] taskNames = db.QueryTaskNames();
					Test.ensureEquals(2, taskNames.Length);
					Test.ensureEquals("task 1", taskNames[0]);
					Test.ensureEquals("task 2", taskNames[1]);

					ADTask[] tasks = db.QueryTasks();
					Test.ensureEquals(2, tasks.Length);
					Test.ensureEquals("task 1", tasks[0].Name);
					Test.ensureEquals("task 2", tasks[1].Name);
				}
			}
			finally
			{
				AppDomain.Unload(domain);
				Test.reOpenAll(); // leave the Test object as we found it
			}
		}

		public string currentFileName()
		{
			return Path.GetFullPath(Test.isClientServer() ? Test.FILE_SERVER : Test.FILE_SOLO);
		}
	}
}
