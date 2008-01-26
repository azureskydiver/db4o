/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections.Generic;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.TA;


namespace Db4objects.Db4odoc.TPExample
{
    public class TPCollectionExample
    {

        private const string Db4oFileName = "reference.db4o";

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            TestCollectionPersistence();
        }
        // end Main

        private static IConfiguration ConfigureTP()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add TP support
            configuration.Add(new TransparentPersistenceSupport());
            return configuration;
        }
        // end ConfigureTP

        private static void StoreCollection()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database(ConfigureTP());
            if (container != null)
            {
                try
                {
                    Team team = new Team();
                    for (int i = 0; i < 10; i++)
                    {
                        team.AddPilot(new Pilot("Pilot #" + i));
                    }
                    container.Set(team);
                    container.Commit();
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine(ex.StackTrace);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end StoreCollection

        private static void TestCollectionPersistence()
        {
            StoreCollection();
            IObjectContainer container = Database(ConfigureTP());
            if (container != null)
            {
                try
                {
                    Team team = (Team)container.QueryByExample(new Team()).Next();
                    // this method will activate all the members in the collection
                    IList<Pilot> pilots = team.Pilots;
                    foreach (Pilot p in pilots)
                    {
                        p.Name = "Modified: " + p.Name;
                    }
                    team.AddPilot(new Pilot("New pilot"));
                    // explicitly commit to persist changes
                    container.Commit();
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine(ex.Message);
                }
                finally
                {
                    // If TP changes were not committed explicitly,
                    // they would be persisted with the #close call
                    CloseDatabase();
                }
            }
            // reopen the database and check the changes
            container = Database(ConfigureTP());
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.QueryByExample(new Team());
                    Team team = (Team)result[0];
                    team.ListAllPilots();
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine(ex.Message);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end TestCollectionPersistence

        
        private static IObjectContainer Database(IConfiguration configuration)
        {
            if (_container == null)
            {
                try
                {
                    _container = Db4oFactory.OpenFile(configuration, Db4oFileName);
                }
                catch (DatabaseFileLockedException ex)
                {
                    System.Console.WriteLine(ex.Message);
                }
            }
            return _container;
        }

        // end Database

        private static void CloseDatabase()
        {
            if (_container != null)
            {
                _container.Close();
                _container = null;
            }
        }

        // end CloseDatabase

    }
}
