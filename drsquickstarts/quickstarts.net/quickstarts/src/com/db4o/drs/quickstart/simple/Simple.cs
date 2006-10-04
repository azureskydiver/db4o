namespace com.db4o.drs.quickstart.simple
{
	public class Simple
	{
		public static void Main(string[] args)
		{
			new com.db4o.drs.quickstart.simple.Simple().DoOneWayReplcation();
			new com.db4o.drs.quickstart.simple.Simple().DoBiDirectionalReplication();
			new com.db4o.drs.quickstart.simple.Simple().DoSelectiveReplication();
		}

		public virtual void DoSelectiveReplication()
		{
			ConfigureDb4oForReplication();
			com.db4o.ObjectContainer handheld = OpenDb("handheld.yap");
			StoreSomePilots(handheld);
			com.db4o.ObjectContainer desktop = OpenDb("desktop.yap");
			DisplayContents("Selective Replication", "Before", handheld, desktop);
			com.db4o.drs.ReplicationSession replication = com.db4o.drs.Replication.Begin(handheld
				, desktop);
			com.db4o.ObjectSet changed = replication.ProviderA().ObjectsChangedSinceLastReplication
				();
			while (changed.HasNext())
			{
				com.db4o.drs.quickstart.simple.Pilot p = (com.db4o.drs.quickstart.simple.Pilot)changed
					.Next();
				if (p._name.StartsWith("S"))
				{
					replication.Replicate(p);
				}
			}
			replication.Commit();
			DisplayContents("", "After", handheld, desktop);
			CloseDb(handheld);
			CloseDb(desktop);
		}

		private void DoBiDirectionalReplication()
		{
			ConfigureDb4oForReplication();
			com.db4o.ObjectContainer handheld = OpenDb("handheld.yap");
			StoreSomePilots(handheld);
			com.db4o.ObjectContainer desktop = OpenDb("desktop.yap");
			StoreSomeMorePilots(desktop);
			DisplayContents("Bi-Directional", "Before", handheld, desktop);
			com.db4o.drs.ReplicationSession replication = com.db4o.drs.Replication.Begin(handheld
				, desktop);
			com.db4o.ObjectSet changed = replication.ProviderA().ObjectsChangedSinceLastReplication
				();
			while (changed.HasNext())
			{
				replication.Replicate(changed.Next());
			}
			changed = replication.ProviderB().ObjectsChangedSinceLastReplication();
			while (changed.HasNext())
			{
				replication.Replicate(changed.Next());
			}
			replication.Commit();
			DisplayContents("", "After", handheld, desktop);
			CloseDb(handheld);
			CloseDb(desktop);
		}

		private void DisplayContents(string methodname, string pointintime, com.db4o.ObjectContainer
			 handheld, com.db4o.ObjectContainer desktop)
		{
			if (methodname != "")
			{
				System.Console.Out.WriteLine(methodname + " Replication");
				System.Console.Out.WriteLine();
			}
			System.Console.Out.WriteLine(pointintime + " Replication");
			System.Console.Out.WriteLine();
			DisplayContentsOf("Contents of Handheld", handheld);
			DisplayContentsOf("Contents of Desktop", desktop);
		}

		private void StoreSomeMorePilots(com.db4o.ObjectContainer db)
		{
			db.Set(new com.db4o.drs.quickstart.simple.Pilot("Peter van der Merwe", 37));
			db.Set(new com.db4o.drs.quickstart.simple.Pilot("Albert Kwan", 30));
		}

		private void DisplayContentsOf(string heading, com.db4o.ObjectContainer db)
		{
			System.Console.Out.WriteLine(heading);
			System.Console.Out.WriteLine();
			com.db4o.ObjectSet result = db.Get(new com.db4o.drs.quickstart.simple.Pilot());
			ListResult(result);
		}

		private void CloseDb(com.db4o.ObjectContainer db)
		{
			db.Close();
		}

		private com.db4o.ObjectContainer OpenDb(string dbname)
		{
			new j4o.io.File(dbname).Delete();
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(dbname);
			return db;
		}

		private void ConfigureDb4oForReplication()
		{
			com.db4o.Db4o.Configure().GenerateUUIDs(int.MaxValue);
			com.db4o.Db4o.Configure().GenerateVersionNumbers(int.MaxValue);
		}

		private void DoOneWayReplcation()
		{
			ConfigureDb4oForReplication();
			com.db4o.ObjectContainer handheld = OpenDb("handheld.yap");
			StoreSomePilots(handheld);
			com.db4o.ObjectContainer desktop = OpenDb("desktop.yap");
			DisplayContents("One-way Replication", "Before", handheld, desktop);
			com.db4o.drs.ReplicationSession replication = com.db4o.drs.Replication.Begin(handheld
				, desktop);
			com.db4o.ObjectSet changed = replication.ProviderA().ObjectsChangedSinceLastReplication
				();
			while (changed.HasNext())
			{
				replication.Replicate(changed.Next());
			}
			replication.Commit();
			DisplayContents("", "After", handheld, desktop);
			CloseDb(handheld);
			CloseDb(desktop);
		}

		private void StoreSomePilots(com.db4o.ObjectContainer db)
		{
			db.Set(new com.db4o.drs.quickstart.simple.Pilot("Scott Felton", 52));
			db.Set(new com.db4o.drs.quickstart.simple.Pilot("Frank Green", 45));
		}

		public virtual void ListResult(com.db4o.ObjectSet result)
		{
			while (result.HasNext())
			{
				System.Console.Out.WriteLine(result.Next());
			}
			System.Console.Out.WriteLine();
		}
	}
}
