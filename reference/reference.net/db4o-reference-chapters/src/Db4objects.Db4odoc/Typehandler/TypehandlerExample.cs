/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */
using System.Text;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Defragment;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Net;
using Db4objects.Db4o.Reflect.Generic;
using Db4objects.Db4o.Typehandlers;

namespace Db4objects.Db4odoc.Typehandler
{

    public class TypehandlerExample
    {

        private readonly static string Db4oFileName = "reference.db4o";
        private static IObjectContainer _container = null;


        public static void Main(string[] args)
        {
            TestReadWriteDelete();
            TestDefrag();
            TestCompare();
        }
        // end Main

        private class TypeHandlerPredicate : ITypeHandlerPredicate
        {
            public bool Match(IReflectClass classReflector, int version)
            {
                IReflector reflector = classReflector.Reflector();
                IReflectClass claxx = reflector.ForClass(typeof(StringBuilder));
                bool res = claxx.Equals(classReflector);
                return res;

            }
        }
        // end TypeHandlerPredicate

        private static IConfiguration Configure()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add a custom typehandler support

            configuration.RegisterTypeHandler(new TypeHandlerPredicate(),
                new StringBuilderHandler());
            return configuration;
        }
        // end Configure


        private static void TestReadWriteDelete()
        {
            StoreCar();
            // Does it still work after close? 
            RetrieveCar();
            // Does deletion work?
            DeleteCar();
            RetrieveCar();
        }
        // end TestReadWriteDelete

        private static void RetrieveCar()
        {
            IObjectContainer container = Database(Configure());
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.QueryByExample(new Car(null));
                    Car car = null;
                    if (result.HasNext())
                    {
                        car = (Car)result.Next();
                    }
                    System.Console.WriteLine("Retrieved: " + car);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }
        // end RetrieveCar

        private static void DeleteCar()
        {
            IObjectContainer container = Database(Configure());
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.QueryByExample(new Car(null));
                    Car car = null;
                    if (result.HasNext())
                    {
                        car = (Car)result.Next();
                    }
                    container.Delete(car);
                    System.Console.WriteLine("Deleted: " + car);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }
        // end DeleteCar

        private static void StoreCar()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database(Configure());
            if (container != null)
            {
                try
                {
                    Car car = new Car("BMW");
                    container.Store(car);
                    IObjectSet result = container.QueryByExample(new Car(null));
                    car = (Car)container.QueryByExample(new Car(null)).Next();
                    System.Console.WriteLine("Stored: " + car);

                }
                finally
                {
                    CloseDatabase();
                }
            }
        }
        // end StoreCar

        private static void TestCompare()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database(Configure());
            if (container != null)
            {
                try
                {
                    Car car = new Car("BMW");
                    container.Store(car);
                    car = new Car("Ferrari");
                    container.Store(car);
                    car = new Car("Mercedes");
                    container.Store(car);
                    IQuery query = container.Query();
                    query.Constrain(typeof(Car));
                    query.Descend("model").OrderAscending();
                    IObjectSet result = query.Execute();
                    ListResult(result);

                }
                finally
                {
                    CloseDatabase();
                }
            }
        }
        // end TestCompare

        private static void TestDefrag()
        {
            File.Delete(Db4oFileName + ".backup");
            StoreCar();
            Defragment.Defrag(Db4oFileName);
            RetrieveCar();
        }
        // end TestDefrag

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


        private static void ListResult(IObjectSet result)
        {
            System.Console.WriteLine(result.Size());
            while (result.HasNext())
            {
                System.Console.WriteLine(result.Next());
            }
        }
        // end ListResult

    }
}
