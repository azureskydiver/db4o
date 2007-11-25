using System;
using System.IO;
using System.Threading;
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4odoc.CommitCallbacks
{
    class PushedUpdatesExample
    {

        private const string Db4oFileName = "reference.db4o";

        private const int Port = 4440;

        private const string User = "db4o";

        private const string Password = "db4o";

        private static Hashtable clientListeners = new Hashtable();

        public static void Main(string[] args)
        {
            new PushedUpdatesExample().Run();
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

                        WaitForCompletion();

                        // retrieve the same pilot with client2
                        Car client2Car = (Car)client2.Query(typeof(Car)).Next();
                        System.Console.WriteLine(client2Car);

                        // modify the pilot with client1
                        client1Car.Model = 2007;
                        client1Car.Pilot = new Pilot("Hakkinnen");
                        client1.Set(client1Car);
                        client1.Commit();

                        WaitForCompletion();

                        // client2Car has been automatically updated in
                        // the committed event handler because of the
                        // modification and the commit by client1
                        System.Console.WriteLine(client2Car);

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
            // remove listeners before shutting down
            if (clientListeners[client] != null)
            {
                IEventRegistry eventRegistry = EventRegistryFactory.ForObjectContainer(client);
                eventRegistry.Committed -= (CommitEventHandler)clientListeners[client];
                clientListeners.Remove(client);
            }
            client.Close();
        }

        // end CloseClient

        private IObjectContainer OpenClient()
        {
            try
            {
                IObjectContainer client = Db4oFactory.OpenClient("localhost", Port, User,
                        Password);
                CommitEventHandler committedEventHandler = CreateCommittedEventHandler(client);
                IEventRegistry eventRegistry = EventRegistryFactory.ForObjectContainer(client);
                eventRegistry.Committed += committedEventHandler;
                // save the client-listener pair in a map, so that we can
                // remove the listener later
                clientListeners.Add(client, committedEventHandler);
                return client;
            }
            catch (Exception ex)
            {
                System.Console.WriteLine(ex.ToString());
            }
            return null;
        }

        // end OpenClient

        private CommitEventHandler CreateCommittedEventHandler(IObjectContainer objectContainer)
        {
            return new CommitEventHandler(delegate(object sender, CommitEventArgs args)
                {
                    // get all the updated objects
                    IObjectInfoCollection updated = args.Updated;

                    foreach (IObjectInfo info in updated)
                    {
                        Object obj = info.GetObject();
                        // refresh object on the client
                        objectContainer.Ext().Refresh(obj, 2);
                    }
                });
        }

        // end CreateCommittedEventHandler

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

    }
}
