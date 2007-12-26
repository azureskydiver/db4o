/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.ClassNameFormat
{
    class ClassNameExample2
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            CheckDatabase();
        }
        // end Main

        public static void CheckDatabase()
        {
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                // Read db4o contents from another application
                IObjectSet result = container.Get(typeof(Test));
                ListResult(result);
                // Check what classes are actualy stored in the database
                IStoredClass[] storedClasses = container.Ext().StoredClasses();
                foreach (IStoredClass storedClass in storedClasses)
                {
                    System.Console.WriteLine("Stored class: " + storedClass.GetName());
                }
            }
            finally
            {
                container.Commit();
            }
        }
        // end CheckDatabase

        public static void ListResult(IObjectSet result)
        {
            System.Console.WriteLine("Objects found: " + result.Size());
            while (result.HasNext())
            {
                System.Console.WriteLine(result.Next());
            }
        }
        // end ListResult
    }
}
