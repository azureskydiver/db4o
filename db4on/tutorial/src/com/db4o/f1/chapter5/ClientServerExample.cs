namespace com.db4o.f1.chapter5
{
    using System;
    using System.IO;
    using com.db4o;
    using com.db4o.f1;

    public class ClientServerExample : Util
    {
        public static void Main(string[] args)
        {
            File.Delete(Util.YapFileName);
            accessLocalServer();
            File.Delete(Util.YapFileName);
            ObjectContainer db = Db4o.openFile(Util.YapFileName);
            try
            {
                setFirstCar(db);
                setSecondCar(db);
            }
            finally
            {
                db.close();
            }
            
            configureDb4o();
            ObjectServer server = Db4o.openServer(Util.YapFileName, 0);
            try
            {
                queryLocalServer(server);
                demonstrateLocalReadCommitted(server);
                demonstrateLocalRollback(server);
            }
            finally
            {
                server.close();
            }
            
            accessRemoteServer();
            server = Db4o.openServer(Util.YapFileName, ServerPort);
            server.grantAccess(ServerUser, ServerPassword);
            try
            {
                queryRemoteServer(ServerPort, ServerUser, ServerPassword);
                demonstrateRemoteReadCommitted(ServerPort, ServerUser, ServerPassword);
                demonstrateRemoteRollback(ServerPort, ServerUser, ServerPassword);
            }
            finally
            {
                server.close();
            }
        }
            
        public static void setFirstCar(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.set(car);
        }
    
        public static void setSecondCar(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Michael Schumacher", 100);
            Car car = new Car("Ferrari");
            car.Pilot = pilot;
            db.set(car);
        }
    
        public static void accessLocalServer()
        {
            ObjectServer server = Db4o.openServer(Util.YapFileName, 0);
            try
            {
                ObjectContainer client = server.openClient();
                // Do something with this client, or open more clients
                client.close();
            }
            finally
            {
                server.close();
            }
        }
    
        public static void queryLocalServer(ObjectServer server)
        {
            ObjectContainer client = server.openClient();
            listResult(client.get(new Car(null)));
            client.close();
        }
        
        public static void configureDb4o()
        {
        	Db4o.configure().objectClass(typeof(Car)).updateDepth(3);
        }
    
        public static void demonstrateLocalReadCommitted(ObjectServer server)
        {
            ObjectContainer client1 =server.openClient();
            ObjectContainer client2 =server.openClient();
            Pilot pilot = new Pilot("David Coulthard", 98);
            ObjectSet result = client1.get(new Car("BMW"));
            Car car = (Car)result.next();
            car.Pilot = pilot;
            client1.set(car);
            listResult(client1.get(new Car(null)));
            listResult(client2.get(new Car(null)));
            client1.commit();
            listResult(client1.get(typeof(Car)));			
            listRefreshedResult(client2, client2.get(typeof(Car)), 2);
            client1.close();
            client2.close();
        }
    
        public static void demonstrateLocalRollback(ObjectServer server)
        {
            ObjectContainer client1 = server.openClient();
            ObjectContainer client2 = server.openClient();
            ObjectSet result = client1.get(new Car("BMW"));
            Car car = (Car)result.next();
            car.Pilot = new Pilot("Someone else", 0);
            client1.set(car);
            listResult(client1.get(new Car(null)));
            listResult(client2.get(new Car(null)));
            client1.rollback();
            client1.ext().refresh(car, 2);
            listResult(client1.get(new Car(null)));
            listResult(client2.get(new Car(null)));
            client1.close();
            client2.close();
        }
    
        public static void accessRemoteServer()
        {
            ObjectServer server = Db4o.openServer(Util.YapFileName, ServerPort);
            server.grantAccess(ServerUser, ServerPassword);
            try
            {
                ObjectContainer client = Db4o.openClient("localhost", ServerPort, ServerUser, ServerPassword);
                // Do something with this client, or open more clients
                client.close();
            }
            finally
            {
                server.close();
            }
        }
    
        public static void queryRemoteServer(int port, string user, string password)
        {
            ObjectContainer client = Db4o.openClient("localhost", port, user, password);
            listResult(client.get(new Car(null)));
            client.close();
        }
    
        public static void demonstrateRemoteReadCommitted(int port, string user, string password)
        {
            ObjectContainer client1 = Db4o.openClient("localhost", port, user, password);
            ObjectContainer client2 = Db4o.openClient("localhost", port, user, password);
            Pilot pilot = new Pilot("Jenson Button", 97);
            ObjectSet result = client1.get(new Car(null));
            Car car = (Car)result.next();
            car.Pilot = pilot;
            client1.set(car);
            listResult(client1.get(new Car(null)));
            listResult(client2.get(new Car(null)));
            client1.commit();
            listResult(client1.get(new Car(null)));
            listResult(client2.get(new Car(null)));
            client1.close();
            client2.close();
        }
    
        public static void demonstrateRemoteRollback(int port, string user, string password)
        {
            ObjectContainer client1 = Db4o.openClient("localhost", port, user, password);
            ObjectContainer client2 = Db4o.openClient("localhost", port, user, password);
            ObjectSet result = client1.get(new Car(null));
            Car car = (Car)result.next();
            car.Pilot = new Pilot("Someone else", 0);
            client1.set(car);
            listResult(client1.get(new Car(null)));
            listResult(client2.get(new Car(null)));
            client1.rollback();
            client1.ext().refresh(car,2);
            listResult(client1.get(new Car(null)));
            listResult(client2.get(new Car(null)));
            client1.close();
            client2.close();
        }
    }
}
