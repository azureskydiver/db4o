using System.IO;
using com.db4o;
using com.db4odoc.f1;

namespace com.db4odoc.f1.clientserver
{
	public class ClientServerExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public readonly static int ServerPort = 0xdb40;
		
		public readonly static string ServerUser = "user";
		
		public readonly static string ServerPassword = "password";

		public static void Main(string[] args)
		{
			File.Delete(YapFileName);
			AccessLocalServer();
			File.Delete(YapFileName);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try
			{
				SetFirstCar(db);
				SetSecondCar(db);
			}
			finally
			{
				db.Close();
			}
            
			ConfigureDb4o();
			ObjectServer server = Db4o.OpenServer(YapFileName, 0);
			try
			{
				QueryLocalServer(server);
				DemonstrateLocalReadCommitted(server);
				DemonstrateLocalRollback(server);
			}
			finally
			{
				server.Close();
			}
            
			AccessRemoteServer();
			server = Db4o.OpenServer(YapFileName, ServerPort);
			server.GrantAccess(ServerUser, ServerPassword);
			try
			{
				QueryRemoteServer(ServerPort, ServerUser, ServerPassword);
				DemonstrateRemoteReadCommitted(ServerPort, ServerUser, ServerPassword);
				DemonstrateRemoteRollback(ServerPort, ServerUser, ServerPassword);
			}
			finally
			{
				server.Close();
			}
		}
		// end Main
            
		public static void SetFirstCar(ObjectContainer db)
		{
			Pilot pilot = new Pilot("Rubens Barrichello", 99);
			Car car = new Car("BMW");
			car.Pilot = pilot;
			db.Set(car);
		}
		// end SetFirstCar
    
		public static void SetSecondCar(ObjectContainer db)
		{
			Pilot pilot = new Pilot("Michael Schumacher", 100);
			Car car = new Car("Ferrari");
			car.Pilot = pilot;
			db.Set(car);
		}
		// end SetSecondCar
    
		public static void AccessLocalServer()
		{
			ObjectServer server = Db4o.OpenServer(YapFileName, 0);
			try
			{
				ObjectContainer client = server.OpenClient();
				// Do something with this client, or open more clients
				client.Close();
			}
			finally
			{
				server.Close();
			}
		}
		// end AccessLocalServer
    
		public static void QueryLocalServer(ObjectServer server)
		{
			ObjectContainer client = server.OpenClient();
			ListResult(client.Get(new Car(null)));
			client.Close();
		}
		// end QueryLocalServer
        
		public static void ConfigureDb4o()
		{
			Db4o.Configure().ObjectClass(typeof(Car)).UpdateDepth(3);
		}
		// end ConfigureDb4o
    
		public static void DemonstrateLocalReadCommitted(ObjectServer server)
		{
			ObjectContainer client1 =server.OpenClient();
			ObjectContainer client2 =server.OpenClient();
			Pilot pilot = new Pilot("David Coulthard", 98);
			ObjectSet result = client1.Get(new Car("BMW"));
			Car car = (Car)result.Next();
			car.Pilot = pilot;
			client1.Set(car);
			ListResult(client1.Get(new Car(null)));
			ListResult(client2.Get(new Car(null)));
			client1.Commit();
			ListResult(client1.Get(typeof(Car)));			
			ListRefreshedResult(client2, client2.Get(typeof(Car)), 2);
			client1.Close();
			client2.Close();
		}
		// end DemonstrateLocalReadCommitted
    
		public static void DemonstrateLocalRollback(ObjectServer server)
		{
			ObjectContainer client1 = server.OpenClient();
			ObjectContainer client2 = server.OpenClient();
			ObjectSet result = client1.Get(new Car("BMW"));
			Car car = (Car)result.Next();
			car.Pilot = new Pilot("Someone else", 0);
			client1.Set(car);
			ListResult(client1.Get(new Car(null)));
			ListResult(client2.Get(new Car(null)));
			client1.Rollback();
			client1.Ext().Refresh(car, 2);
			ListResult(client1.Get(new Car(null)));
			ListResult(client2.Get(new Car(null)));
			client1.Close();
			client2.Close();
		}
		// end DemonstrateLocalRollback
    
		public static void AccessRemoteServer()
		{
			ObjectServer server = Db4o.OpenServer(YapFileName, ServerPort);
			server.GrantAccess(ServerUser, ServerPassword);
			try
			{
				ObjectContainer client = Db4o.OpenClient("localhost", ServerPort, ServerUser, ServerPassword);
				// Do something with this client, or open more clients
				client.Close();
			}
			finally
			{
				server.Close();
			}
		}
		// end AccessRemoteServer
    
		public static void QueryRemoteServer(int port, string user, string password)
		{
			ObjectContainer client = Db4o.OpenClient("localhost", port, user, password);
			ListResult(client.Get(new Car(null)));
			client.Close();
		}
		// end QueryRemoteServer
    
		public static void DemonstrateRemoteReadCommitted(int port, string user, string password)
		{
			ObjectContainer client1 = Db4o.OpenClient("localhost", port, user, password);
			ObjectContainer client2 = Db4o.OpenClient("localhost", port, user, password);
			Pilot pilot = new Pilot("Jenson Button", 97);
			ObjectSet result = client1.Get(new Car(null));
			Car car = (Car)result.Next();
			car.Pilot = pilot;
			client1.Set(car);
			ListResult(client1.Get(new Car(null)));
			ListResult(client2.Get(new Car(null)));
			client1.Commit();
			ListResult(client1.Get(new Car(null)));
			ListResult(client2.Get(new Car(null)));
			client1.Close();
			client2.Close();
		}
		// end DemonstrateRemoteReadCommitted
    
		public static void DemonstrateRemoteRollback(int port, string user, string password)
		{
			ObjectContainer client1 = Db4o.OpenClient("localhost", port, user, password);
			ObjectContainer client2 = Db4o.OpenClient("localhost", port, user, password);
			ObjectSet result = client1.Get(new Car(null));
			Car car = (Car)result.Next();
			car.Pilot = new Pilot("Someone else", 0);
			client1.Set(car);
			ListResult(client1.Get(new Car(null)));
			ListResult(client2.Get(new Car(null)));
			client1.Rollback();
			client1.Ext().Refresh(car,2);
			ListResult(client1.Get(new Car(null)));
			ListResult(client2.Get(new Car(null)));
			client1.Close();
			client2.Close();
		}
		// end DemonstrateRemoteRollback

		public static void ListResult(ObjectSet result)
		{
			System.Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				System.Console.WriteLine(item);
			}
		}
		// end ListResult

		public static void ListRefreshedResult(ObjectContainer container, ObjectSet items, int depth)
		{
			System.Console.WriteLine(items.Count);
			foreach (object item in items)
			{	
				container.Ext().Refresh(item, depth);
				System.Console.WriteLine(item);
			}
		}
		// end ListRefreshedResult		
	}
}
