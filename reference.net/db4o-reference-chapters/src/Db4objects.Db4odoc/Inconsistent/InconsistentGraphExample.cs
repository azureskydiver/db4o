using System;
using System.IO;
using System.Threading;
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;


namespace Db4objects.Db4odoc.Inconsistent
{
    class InconsistentGraphExample
    {

        private const string Db4oFileName = "reference.db4o";

        private const int Port = 4440;

        private const string User = "db4o";

        private const string Password = "db4o";

        
        public static void Main(string[] args)
        {
            new InconsistentGraphExample().Run();
        }

        // end Main

        public void Run()
        {
            File.Delete(Db4oFileName);
            IObjectServer server = Db4oFactory.OpenServer(Db4oFileName, Port);
            try
            {
                server.GrantAccess(User, Password);

                IObjectContainer client1 = OpenClient();
                IObjectContainer client2 = OpenClient();

                if (client1 != null && client2 != null)
                {
                    try
                    {
                        // wait for the operations to finish
                        WaitForCompletion();

                        // save pilot with client1
                        Car client1Car = new Car("Ferrari", 2006, new Pilot("Schumacher"));
                        client1.Set(client1Car);
                        client1.Commit();
                        System.Console.WriteLine("Client1 version initially: " + client1Car);

                        WaitForCompletion();

                        // retrieve the same pilot with client2
                        Car client2Car = (Car)client2.Query(typeof(Car)).Next();
                        System.Console.WriteLine("Client2 version initially: " + client2Car);

                        // delete the pilot with client1
					    Pilot client1Pilot = (Pilot)client1.Query(typeof(Pilot)).Next();
					    client1.Delete(client1Pilot);
					    // modify the car, add and link a new pilot with client1
                        client1Car.Model = 2007;
                        client1Car.Pilot = new Pilot("Hakkinnen");
                        client1.Set(client1Car);
                        client1.Commit();

                        WaitForCompletion();

                        client1Car = (Car) client1.Query(typeof(Car)).Next();
					    System.Console.WriteLine("Client1 version after update: " + client1Car);


					    System.Console.WriteLine();
                        System.Console.WriteLine("client2Car still holds the old object graph in its reference cache"); 
					    client2Car = (Car) client2.Query(typeof(Car)).Next();
					    System.Console.WriteLine("Client2 version after update: " + client2Car);
					    IObjectSet result = client2.Query(typeof(Pilot));
					    System.Console.WriteLine("Though the new Pilot is retrieved by a new query: ");
					    ListResult(result);


                        WaitForCompletion();
                    }
                    catch (Exception ex)
                    {
                        System.Console.WriteLine(ex.ToString());
                    }
                    finally
                    {
                        CloseClient(client1);
                        CloseClient(client2);
                    }
                }
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.ToString());
            }
            finally
            {
                server.Close();
            }
        }

        // end Run

        private void CloseClient(IObjectContainer client)
        {
            client.Close();
        }

        // end CloseClient

        private IObjectContainer OpenClient()
        {
            try
            {
                IObjectContainer client = Db4oFactory.OpenClient("localhost", Port, User,
                        Password);
                return client;
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.ToString());
            }
            return null;
        }

        // end OpenClient

        
        private void WaitForCompletion()
        {
            try
            {
                Thread.Sleep(1000);
            }
            catch (ThreadInterruptedException e)
            {
                System.Console.WriteLine(e.Message);
            }
        }
        // end WaitForCompletion

        private static void ListResult(IObjectSet result)
		{
			Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				Console.WriteLine(item);
			}
		}
		// end ListResult

    }
}
