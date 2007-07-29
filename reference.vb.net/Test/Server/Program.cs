using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;

using Db4objects.Db4o;
using Db4objects.Db4o.Messaging;

namespace Server
{
    class Program
    {
        static void Main(string[] args)
        {
            DBManager dbm = new DBManager();
            dbm.StartServer();
        }

        internal class DBManager : IMessageRecipient
        {

            private string _filePath;

            private int _port;

            private string _host;

            private string _user;

            private string _password;
            bool stop = false;

            private IObjectServer _server;

            internal DBManager()
            {

                _filePath = "c:\\test.db";

                _host = "localhost";

                _port = 0xdb40;

                _user = "db4o";

                _password = "db4o";

            }

            internal void StartServer()
            {

                lock (this)
                {

                    //Defrag

                    //Db4objects.Db4o.Defragment.Defragment.Defrag(_filePath);

                    //Open Server

                    _server = Db4oFactory.OpenServer(_filePath, _port);

                    //Configure

                    _server.Ext().Configure().DetectSchemaChanges(true);

                    _server.GrantAccess(_user, _password);





                    // Using the messaging functionality to redirect all

                    // messages to this.processMessage

                    _server.Ext().Configure().ClientServer().SetMessageRecipient(this);

                    try
                    {
                        if (!stop)
                        {
                            // wait forever until Close will change stop variable
                            Monitor.Wait(this);
                        }
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.ToString());
                    }
                    _server.Close();

                }

            }

            internal void StopServer()
            {

                Thread closeClient;

                closeClient = new Thread(new ThreadStart(SendCloseServerMessage));

                closeClient.Start();

            }

            private void SendCloseServerMessage()
            {

                IObjectContainer client = null;

                try
                {

                    // connect to the server

                    client = Db4oFactory.OpenClient(_host, _port, _user, _password);

                }

                catch
                {

                    throw;

                }



                if (client != null)
                {

                    // get the messageSender for the IObjectContainer

                    IMessageSender messageSender = client.Ext().Configure().ClientServer().GetMessageSender();

                    // send an instance of a StopServer object

                    messageSender.Send(new StopServerMsg());



                    // close the IObjectContainer

                    client.Close();

                }

            }

            private class StopServerMsg

            { }

            public void ProcessMessage(IObjectContainer con, object message)
            {

                Thread.Sleep(1000);

                if (message is StopServerMsg)
                {

                    con.Close();

                    _server.Close();

                }

            }

        }

    }
}
