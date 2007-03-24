using System;
using System.IO;
using System.Collections;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.selectivepersistence
{
    class TransientClassExample
    {
        public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            SaveObjects();
            RetrieveObjects();
        }
        // end main

        public static void SaveObjects()
        {
            File.Delete(YapFileName);
            IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
            try
            {
                // Save Test1 object with a NotStorable class field
                Test1 test1 = new Test1("Test1", new NotStorable());
                oc.Set(test1);
                // Save Test2 object with a NotStorable class field
                Test2 test2 = new Test2("Test2", new NotStorable(), test1);
                oc.Set(test2);
            }
            finally
            {
                oc.Close();
            }
        }
        // end SaveObjects

        public static void RetrieveObjects()
        {
            IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
            try
            {
                // retrieve the results and check if the NotStorable instances were saved
                IList result = oc.Get(null);
                ListResult(result);
            }
            finally
            {
                oc.Close();
            }
        }
        // end RetrieveObjects


        public static void ListResult(IList result)
        {
            Console.WriteLine(result.Count);
            for (int x = 0; x < result.Count; x++)
                Console.WriteLine(result[x]);
        }
        // end ListResult
    }
}
