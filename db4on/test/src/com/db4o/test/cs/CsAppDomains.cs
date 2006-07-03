/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using System.IO;
using com.db4o.query;

namespace com.db4o.test.cs
{
#if !CF_1_0 && !CF_2_0
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
            while (os.HasNext())
            {
                names.Add(((ADTask)os.Next()).Name);
            }
            return (string[])names.ToArray(typeof(string));
        }

        public ADTask[] QueryTasks()
        {
            ArrayList tasks = new ArrayList();
            ObjectSet os = InternalQueryTasks();
            while (os.HasNext())
            {
                tasks.Add(os.Next());
            }
            return (ADTask[])tasks.ToArray(typeof(ADTask));
        }

        private ObjectSet InternalQueryTasks()
        {
            Query query = _container.Query();
            query.Constrain(typeof(ADTask));
            query.Descend("_name").OrderAscending();
            return query.Execute();
        }
    }
	
	/// <summary>
	/// Tests the interaction of db4o with multiple AppDomains
	/// </summary>
	public class CsAppDomains
	{
		// keep task objects alive to check for any identity problems
		ArrayList _tasks = new ArrayList();

		public void Store()
		{
			ADTask task = null;
			Tester.Store(task = new ADTask("task 1"));
			_tasks.Add(task);

			Tester.Store(task = new ADTask("task 2"));
			_tasks.Add(task);
		}
		
		public void TestRemoteDomain()
		{
			Tester.CloseAll();

			AppDomain domain = AppDomain.CreateDomain("db4o-remote-domain");
			try
			{
				using (TaskDatabase db = (TaskDatabase)domain.CreateInstanceAndUnwrap(typeof(TaskDatabase).Assembly.GetName().ToString(), typeof(TaskDatabase).FullName))
				{
					db.Open(CurrentFileName(), Tester.IsClientServer());
				
					string[] taskNames = db.QueryTaskNames();
					Tester.EnsureEquals(2, taskNames.Length);
					Tester.EnsureEquals("task 1", taskNames[0]);
					Tester.EnsureEquals("task 2", taskNames[1]);

					ADTask[] tasks = db.QueryTasks();
					Tester.EnsureEquals(2, tasks.Length);
					Tester.EnsureEquals("task 1", tasks[0].Name);
					Tester.EnsureEquals("task 2", tasks[1].Name);
				}
			}
			finally
			{
				AppDomain.Unload(domain);
				Tester.ReOpenAll(); // leave the Tester object as we found it
			}
		}

		public string CurrentFileName()
		{
			return Path.GetFullPath(Tester.IsClientServer() ? Tester.FILE_SERVER : Tester.FILE_SOLO);
		}
	}
#endif
}
