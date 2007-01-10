/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
/*
 * This example shows how to implement object callbacks to assign 
 * autoincremented ID to a special type of objects
 */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Events;

namespace Db4objects.Db4odoc.Callbacks
{
    class AutoIncExample
    {
        private readonly static string YapFileName = "formula1.yap";
        private static IObjectContainer _container;


        public static void Main(string[] args)
        {
            IObjectContainer db = null;
            File.Delete(YapFileName);
            try
            {
                db = OpenContainer();
                RegisterCallback();
                StoreObjects();
                RetrieveObjects();
            }
            finally
            {
                CloseContainer();
            }
        }
        // end Main

        private static IObjectContainer OpenContainer()
        {
            if (_container == null)
            {
                _container = Db4oFactory.OpenFile(YapFileName);
            }
            return _container;
        }
        // end OpenContainer

        private static void CloseContainer()
        {
            if (_container != null)
            {
                _container.Close();
                _container = null;
            }
        }
        // end CloseContainer

        public static void RetrieveObjects()
        {
            IObjectContainer db = OpenContainer();
            IObjectSet result = db.Get(new TestObject(null));
            ListResult(result);
        }
        // end RetrieveObjects

        public static void StoreObjects()
        {
            IObjectContainer db = OpenContainer();
            TestObject test;
            test = new TestObject("FirstObject");
            db.Set(test);
            test = new TestObject("SecondObject");
            db.Set(test);
            test = new TestObject("ThirdObject");
            db.Set(test);
        }
        // end StoreObjects

        public static void RegisterCallback()
        {
            IObjectContainer db = OpenContainer();
            IEventRegistry registry = EventRegistryFactory.ForObjectContainer(db);
            // register an event handler, which will assign autoincremented IDs to any
            // object extending CountedObject, when the object is created
            registry.Creating += new CancellableObjectEventHandler(OnCreating);
        }
        // end RegisterCallback

        private static void OnCreating(object sender, CancellableObjectEventArgs args)
        {
            IObjectContainer db = OpenContainer();
            object obj = args.Object;
            // only for the objects extending the CountedObject
            if (obj is CountedObject)
            {
                ((CountedObject)obj).Id = GetNextId(db);
            }
        }
        // end OnCreating


        private static int GetNextId(IObjectContainer db)
        {
            // this function retrieves the next available ID from 
            // the IncrementedId object
            IncrementedId r = IncrementedId.GetIdObject(db);
            int nRoll;
            nRoll = r.GetNextID(db);

            return nRoll;
        }
        // end GetNextId

        public static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Count);
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        // end LlistResult
    }

}
