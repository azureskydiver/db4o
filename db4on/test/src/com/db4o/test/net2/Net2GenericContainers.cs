#if NET_2_0
using System;
using System.Collections.Generic;
using System.Text;
using com.db4o;
using com.db4o.test;
using com.db4o.query;

namespace com.db4o.test.net2
{
	class Task
	{
		public string name;

		public Task(string name)
		{
			this.name = name;
		}
	}

	class Project
	{
		public string name;
		public List<Task> tasks;

		public Project(string name)
		{
			this.name = name;
			this.tasks = new List<Task>();
		}
	}

	class Net2GenericContainers
	{
		public void store()
		{
			Project p1 = new Project("db4o");
			p1.tasks.Add(new Task("dotnet 2 generics"));
			p1.tasks.Add(new Task("clean mono build"));
			
			Project p2 = new Project("enlightenment");
			p2.tasks.Add(new Task("meditate"));

			Test.store(p1);
			Test.reOpen();
			Test.store(p2);
		}

		public void testProjects()
		{
			Query query = Test.query();
			query.constrain(typeof(Project));
			query.descend("name").orderAscending();

			ObjectSet os = query.execute();
			Test.ensureEquals(2, os.size());

			Project p = (Project)os.next();
			Test.ensureEquals("db4o", p.name);
			Test.ensure(p.tasks != null);
			Test.ensureEquals(2, p.tasks.Count);
			Test.ensureEquals("dotnet 2 generics", p.tasks[0].name);
			Test.ensureEquals("clean mono build", p.tasks[1].name);

			p = (Project)os.next();
			Test.ensureEquals("enlightenment", p.name);
			Test.ensure(p.tasks != null);
			Test.ensureEquals(1, p.tasks.Count);
			Test.ensureEquals("meditate", p.tasks[0].name);
		}

		public void testTasks()
		{
			Query query = Test.query();
			query.constrain(typeof(Task));
			query.descend("name").orderAscending();

			ObjectSet os = query.execute();
			Test.ensureEquals(3, os.size());
			foreach (string expected in new string[] { "clean mono build", "dotnet 2 generics", "meditate" })
			{
				Test.ensureEquals(expected, ((Task)os.next()).name);
			}
		}

		public void testTaskLists()
		{
			Query query = Test.query();
			query.constrain(typeof(List<Task>));

			Test.ensureEquals(2, query.execute().size());

			query = Test.query();
			query.constrain(typeof(List<Project>));

			Test.ensureEquals(0, query.execute().size());
		}
	}
}
#endif
