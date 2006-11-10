using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Clientserver
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
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
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
			IObjectServer server = Db4oFactory.OpenServer(YapFileName, 0);
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
			server = Db4oFactory.OpenServer(YapFileName, ServerPort);
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
            
		public static void SetFirstCar(IObjectContainer db)
		{
			Pilot pilot = new Pilot("Rubens Barrichello", 99);
			Car car = new Car("BMW");
			car.Pilot = pilot;
			db.Set(car);
		}
		// end SetFirstCar
    
		public static void SetSecondCar(IObjectContainer db)
		{
			Pilot pilot = new Pilot("Michael Schumacher", 100);
			Car car = new Car("Ferrari");
			car.Pilot = pilot;
			db.Set(car);
		}
		// end SetSecondCar
    
		public static void AccessLocalServer()
		{
			IObjectServer server = Db4oFactory.OpenServer(YapFileName, 0);
			try
			{
				IObjectContainer client = server.OpenClient();
				// Do something with this client, or open more clients
				client.Close();
			}
			finally
			{
				server.Close();
			}
		}
		// end AccessLocalServer
    
		public static void QueryLocalServer(IObjectServer server)
		{
			IObjectContainer client = server.OpenClient();
			ListResult(client.Get(new Car(null)));
			client.Close();
		}
		// end QueryLocalServer
        
		public static void ConfigureDb4o()
		{
			Db4oFactory.Configure().ObjectClass(typeof(Car)).UpdateDepth(3);
		}
		// end ConfigureDb4o
    
		public static void DemonstrateLocalReadCommitted(IObjectServer server)
		{
			IObjectContainer client1 =server.OpenClient();
			IObjectContainer client2 =server.OpenClient();
			Pilot pilot = new Pilot("David Coulthard", 98);
			IObjectSet result = client1.Get(new Car("BMW"));
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
    
		public static void DemonstrateLocalRollback(IObjectServer server)
		{
			IObjectContainer client1 = server.OpenClient();
			IObjectContainer client2 = server.OpenClient();
			IObjectSet result = client1.Get(new Car("BMW"));
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
			IObjectServer server = Db4oFactory.OpenServer(YapFileName, ServerPort);
			server.GrantAccess(ServerUser, ServerPassword);
			try
			{
				IObjectContainer client = Db4oFactory.OpenClient("localhost", ServerPort, ServerUser, ServerPassword);
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
			IObjectContainer client = Db4oFactory.OpenClient("localhost", port, user, password);
			ListResult(client.Get(new Car(null)));
			client.Close();
		}
		// end QueryRemoteServer
    
		public static void DemonstrateRemoteReadCommitted(int port, string user, string password)
		{
			IObjectContainer client1 = Db4oFactory.OpenClient("localhost", port, user, password);
			IObjectContainer client2 = Db4oFactory.OpenClient("localhost", port, user, password);
			Pilot pilot = new Pilot("Jenson Button", 97);
			IObjectSet result = client1.Get(new Car(null));
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
			IObjectContainer client1 = Db4oFactory.OpenClient("localhost", port, user, password);
			IObjectContainer client2 = Db4oFactory.OpenClient("localhost", port, user, password);
			IObjectSet result = client1.Get(new Car(null));
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

		public static void ListResult(IObjectSet result)
		{
			System.Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				System.Console.WriteLine(item);
			}
		}
		// end ListResult

		public static void ListRefreshedResult(IObjectContainer container, IObjectSet items, int depth)
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
