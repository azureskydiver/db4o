namespace com.db4o.test.net2
{
#if NET_2_0
	using System;
	using System.Collections.Generic;
	using System.Text;
	using com.db4o;
	using com.db4o.test;
	using com.db4o.query;

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

			Tester.store(p1);
			Tester.reOpen();
			Tester.store(p2);
		}

		public void testProjects()
		{
			Query query = Tester.query();
			query.constrain(typeof(Project));
			query.descend("name").orderAscending();

			ObjectSet os = query.execute();
			Tester.ensureEquals(2, os.size());

			Project p = (Project)os.next();
			Tester.ensureEquals("db4o", p.name);
			Tester.ensure(p.tasks != null);
			Tester.ensureEquals(2, p.tasks.Count);
			Tester.ensureEquals("dotnet 2 generics", p.tasks[0].name);
			Tester.ensureEquals("clean mono build", p.tasks[1].name);

			p = (Project)os.next();
			Tester.ensureEquals("enlightenment", p.name);
			Tester.ensure(p.tasks != null);
			Tester.ensureEquals(1, p.tasks.Count);
			Tester.ensureEquals("meditate", p.tasks[0].name);
		}

		public void testTasks()
		{
			Query query = Tester.query();
			query.constrain(typeof(Task));
			query.descend("name").orderAscending();

			ObjectSet os = query.execute();
			Tester.ensureEquals(3, os.size());
			foreach (string expected in new string[] { "clean mono build", "dotnet 2 generics", "meditate" })
			{
				Tester.ensureEquals(expected, ((Task)os.next()).name);
			}
		}

		public void testTaskLists()
		{
			Query query = Tester.query();
			query.constrain(typeof(List<Task>));

			Tester.ensureEquals(2, query.execute().size());

			query = Tester.query();
			query.constrain(typeof(List<Project>));

			Tester.ensureEquals(0, query.execute().size());
		}
	}
#endif
}
