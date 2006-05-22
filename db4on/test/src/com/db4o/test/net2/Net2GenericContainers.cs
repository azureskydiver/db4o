namespace com.db4o.test.net2
{
#if NET_2_0 || CF_2_0
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
		public void Store()
		{
			Project p1 = new Project("db4o");
			p1.tasks.Add(new Task("dotnet 2 generics"));
			p1.tasks.Add(new Task("clean mono build"));
			
			Project p2 = new Project("enlightenment");
			p2.tasks.Add(new Task("meditate"));

			Tester.Store(p1);
			Tester.Store(p2);

            Dictionary<string, List<Task>> dict = new Dictionary<string, List<Task>>();
            dict.Add(p1.name, p1.tasks);
            dict.Add(p2.name, p2.tasks);

            Tester.Store(dict);
			
			Tester.ReOpenAll();
		}
		
		public void TestDescendOnList()
		{
			EnsureProjectByTaskName("enlightenment", "meditate");
			EnsureProjectByTaskName("db4o", "clean mono build");
		}

		private static void EnsureProjectByTaskName(string expectedProjectName, string taskName)
		{
			Query q = Tester.Query();
			q.Constrain(typeof (Project));
			q.Descend("tasks").Descend("name").Constrain(taskName);
			ObjectSet result = q.Execute();
			Tester.EnsureEquals(1, result.Count, "testDescendOnList.Count");
			Tester.EnsureEquals(expectedProjectName, ((Project) result[0]).name, "testDescendOnList.name");
		}


		public void TestDict()
        {
            Query query = Tester.Query();
            query.Constrain(typeof(Dictionary<string, List<Task>>));

            ObjectSet os = query.Execute();
            Tester.EnsureEquals(1, os.Size());

            Dictionary<string, List<Task>> dict = (Dictionary<string, List<Task>>)os.Next();
            Tester.Ensure(dict != null);

            Tester.EnsureEquals(2, dict.Count);
            Tester.Ensure(dict.ContainsKey("enlightenment"));
            Tester.Ensure(dict.ContainsKey("db4o"));
        }

		public void TestProjects()
		{
			Query query = Tester.Query();
			query.Constrain(typeof(Project));
			query.Descend("name").OrderAscending();

			ObjectSet os = query.Execute();
			Tester.EnsureEquals(2, os.Size());

			Project p = (Project)os.Next();
			Tester.EnsureEquals("db4o", p.name);
			Tester.Ensure(p.tasks != null);
			Tester.EnsureEquals(2, p.tasks.Count);
			Tester.EnsureEquals("dotnet 2 generics", p.tasks[0].name);
			Tester.EnsureEquals("clean mono build", p.tasks[1].name);

			p = (Project)os.Next();
			Tester.EnsureEquals("enlightenment", p.name);
			Tester.Ensure(p.tasks != null);
			Tester.EnsureEquals(1, p.tasks.Count);
			Tester.EnsureEquals("meditate", p.tasks[0].name);
		}

		public void TestTasks()
		{
			Query query = Tester.Query();
			query.Constrain(typeof(Task));
			query.Descend("name").OrderAscending();

			ObjectSet os = query.Execute();
			Tester.EnsureEquals(3, os.Size());
			foreach (string expected in new string[] { "clean mono build", "dotnet 2 generics", "meditate" })
			{
				Tester.EnsureEquals(expected, ((Task)os.Next()).name);
			}
		}

		public void TestTaskLists()
		{
			Query query = Tester.Query();
			query.Constrain(typeof(List<Task>));

			Tester.EnsureEquals(2, query.Execute().Size());

			query = Tester.Query();
			query.Constrain(typeof(List<Project>));

			Tester.EnsureEquals(0, query.Execute().Size());
		}
	}
#endif
}
