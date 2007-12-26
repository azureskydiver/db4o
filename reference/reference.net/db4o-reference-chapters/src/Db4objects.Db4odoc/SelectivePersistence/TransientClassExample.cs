/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.SelectivePersistence
{
    class TransientClassExample
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            SaveObjects();
            RetrieveObjects();
        }
        // end main

        private static void SaveObjects()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                // Save Test1 object with a NotStorable class field
                Test1 test1 = new Test1("Test1", new NotStorable());
                container.Set(test1);
                // Save Test2 object with a NotStorable class field
                Test2 test2 = new Test2("Test2", new NotStorable(), test1);
                container.Set(test2);
            }
            finally
            {
                container.Close();
            }
        }
        // end SaveObjects

        private static void RetrieveObjects()
        {
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                // retrieve the results and check if the NotStorable instances were saved
                IList result = container.Get(null);
                ListResult(result);
            }
            finally
            {
                container.Close();
            }
        }
        // end RetrieveObjects


        private static void ListResult(IList result)
        {
            Console.WriteLine(result.Count);
            foreach (object obj in result)
                Console.WriteLine(obj);
        }
        // end ListResult
    }
}
