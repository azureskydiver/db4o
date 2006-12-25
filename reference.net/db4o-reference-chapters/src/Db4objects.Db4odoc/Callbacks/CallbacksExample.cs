using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Events;

namespace Db4objects.Db4odoc.Callbacks
{
    class CallbacksExample
    {
        private readonly static string YapFileName = "formula1.yap";
        private static IObjectContainer _container;

        public static void Main(string[] args)
        {
            TestCreated();
            TestCascadedDelete();
            TestIntegrityCheck();
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

        private static void OnCreated(object sender, ObjectEventArgs args)
        {
            Object obj = args.Object;
            if (obj is Pilot)
            {
                Console.WriteLine(obj.ToString());
            }
        }
        // end OnCreated

        public static void TestCreated()
        {
            File.Delete(YapFileName);
            IObjectContainer container = OpenContainer();
            try
            {
                IEventRegistry registry = EventRegistryFactory.ForObjectContainer(container);
                // register an event handler, which will print all the car objects, that have been Created
                registry.Created += new ObjectEventHandler(OnCreated);

                Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
                container.Set(car);
            }
            finally
            {
                CloseContainer();
            }
        }
        // end TestCreated

        private static void FillContainer()
        {
            File.Delete(YapFileName);
            IObjectContainer container = OpenContainer();
            try
            {
                Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
                container.Set(car);
                car = new Car("Ferrari", new Pilot("Kimi Raikkonen"));
                container.Set(car);
            }
            finally
            {
                CloseContainer();
            }
        }
        // end FillContainer

        private static void OnDeleted(object sender, ObjectEventArgs args)
        {
            Object obj = args.Object;
            if (obj is Car)
            {
                OpenContainer().Delete(((Car)obj).Pilot);
            }
        }
        // end OnDeleted

        public static void TestCascadedDelete()
        {
		    FillContainer();
            IObjectContainer container = OpenContainer();
		    try {
			    // check the contents of the database
			    IObjectSet result = container.Get(null);
			    ListResult(result);
    			
			    IEventRegistry registry =  EventRegistryFactory.ForObjectContainer(container);
			    // register an event handler, which will delete the pilot when his car is Deleted 
                registry.Deleted += new ObjectEventHandler(OnDeleted);
			    // delete all the cars
			    result = container.Query(typeof(Car));
			    while(result.HasNext()) {
	                container.Delete(result.Next());
	            }
			    // check if the database is empty
			    result = container.Get(null);
			    ListResult(result);
		    } finally {
                CloseContainer();
		    }
	    }
        // end TestCascadedDelete

        private static void OnDeleting(object sender, CancellableObjectEventArgs args)
        {
            Object obj = args.Object;
            if (obj is Pilot)
            {
                IObjectContainer container = OpenContainer();
                // search for the cars referencing the pilot object
                IQuery q = container.Query();
                q.Constrain(typeof(Car));
                q.Descend("_pilot").Constrain(obj);
                IObjectSet result = q.Execute();
                if (result.Size() > 0)
                {
                    Console.WriteLine("Object " + (Pilot)obj + " can't be Deleted as object container has references to it");
                    args.Cancel();
                }
            }
        }
        // end OnDeleting

        public static void TestIntegrityCheck()
        {
		    FillContainer();
		    IObjectContainer container = Db4oFactory.OpenFile(YapFileName);
		    try {
                IEventRegistry registry = EventRegistryFactory.ForObjectContainer(container);
			    // register an event handler, which will stop Deleting a pilot when it is referenced from a car 
			    registry.Deleting += new CancellableObjectEventHandler(OnDeleting); 
    			
			    // check the contents of the database
			    IObjectSet result = container.Get(null);
			    ListResult(result);
    			
			    // try to delete all the pilots
			    result = container.Get(typeof(Pilot));
			    while(result.HasNext()) {
	                container.Delete(result.Next());
	            }
			    // check if any of the objects were Deleted
                result = container.Get(null);
			    ListResult(result);
		    } finally {
                CloseContainer();
		    }
	    }
        // end TestIntegrityCheck

        private static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Size());
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        // end ListResult
    }
}
