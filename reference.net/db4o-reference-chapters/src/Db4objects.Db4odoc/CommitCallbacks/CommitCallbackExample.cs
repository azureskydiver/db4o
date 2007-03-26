/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.CommitCallbacks
{
    class CommitCallbackExample
    {
        private const string FileName = "test.db";
        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            File.Delete(FileName);
            try
            {
                Configure();
                StoreFirstObject();
                StoreOtherObjects();
            }
            finally
            {
                Container().Close();
            }
        }
        // end Main

        private static IObjectContainer Container()
        {
            if (_container == null)
            {
                _container = Db4oFactory.OpenFile(FileName);
            }
            return _container;
        }
        // end Container

        private static void Configure()
        {
            IEventRegistry registry = EventRegistryFactory.ForObjectContainer(Container());
            // register an event handler, which will check object uniqueness on commit
            registry.Committing += new CommitEventHandler(delegate(object sender, CommitEventArgs args)
            {
                // uniqueness should be checked for both added and updated objects
                CheckUniqueness(args.Added);
                CheckUniqueness(args.Updated);
            });
        }
        // end Configure

        private static void CheckUniqueness(IObjectInfoCollection collection)
        {
            foreach (IObjectInfo info in collection)
            {
                // only check for Item objects
                Item item = info.GetObject() as Item;
                if (item == null) continue;

                // search for objects with the same fields in the database
                IObjectSet found = Container().Get(new Item(item.Number, item.Word));
                if (found.Count > 1)
                {
                    throw new Db4oException("Object is not unique: " + item);
                }
            }
        }
        // end CheckUniqueness

        private static void StoreFirstObject()
        {
            IObjectContainer container = Container();
            try
            {
                // creating and storing item1 to the database
                Item item = new Item(1, "one");
                container.Set(item);
                // no problems here
                container.Commit();
            }
            catch (Db4oException ex)
            {
                System.Console.WriteLine(ex.Message);
                container.Rollback();
            }
        }
        // end StoreFirstObject

        private static void StoreOtherObjects()
        {
            IObjectContainer container = Container();
            // creating and storing similar items to the database
            Item item = new Item(2, "one");
            container.Set(item);
            item = new Item(1, "two");
            container.Set(item);
            try
            {
                // commit should work as there were no duplicate objects
                container.Commit();
            }
            catch (Db4oException ex)
            {
                System.Console.WriteLine(ex.Message);
                container.Rollback();
            }
            System.Console.WriteLine("Commit successful");

            // trying to save a duplicate object to the database
            item = new Item(1, "one");
            container.Set(item);
            try
            {
                // Commit should fail as duplicates are not allowed
                container.Commit();
            }
            catch (Db4oException ex)
            {
                System.Console.WriteLine(ex.Message);
                container.Rollback();
            }
        }
        // end StoreOtherObjects
    }
}
