/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Comparing
{
    class CompareExample
    {
        private const string FileName = "example.db";

        public static void Main(string[] args)
        {
            Configure();
            StoreRecords();
            CheckRecords();
        }
        // end Main

        public static void Configure()
        {
            Db4oFactory.Configure().ObjectClass(typeof(MyString)).Compare(new MyStringAttribute());
        }
        // end Configure

        public static void StoreRecords()
        {
            File.Delete(FileName);
            IObjectContainer container = Db4oFactory.OpenFile(FileName);
            try
            {
                Record record = new Record("Michael Schumacher, points: 100");
                container.Set(record);
                record = new Record("Rubens Barrichello, points: 98");
                container.Set(record);
                record = new Record("Kimi Raikonnen, points: 55");
                container.Set(record);
            }
            finally
            {
                container.Close();
            }
        }
        // end StoreRecords

        public static void CheckRecords()
        {
            IObjectContainer container = Db4oFactory.OpenFile(FileName);
            try
            {
                IQuery q = container.Query();
                q.Constrain(new Record("Rubens"));
                q.Descend("_record").Constraints().Contains();
                IObjectSet result = q.Execute();
                ListResult(result);
            }
            finally
            {
                container.Close();
            }
        }
        // end CheckRecords

        public static void ListResult(IObjectSet result)
        {
            System.Console.WriteLine(result.Size());
            while (result.HasNext())
            {
                System.Console.WriteLine(result.Next());
            }
        }
        // end ListResult

        private class MyStringAttribute : IObjectAttribute
        {
            public object Attribute(object original)
            {
                if (original is MyString)
                {
                    return ((MyString)original).ToString();
                }
                return original;
            }
        }
        // end MyStringAttribute
    }
}
