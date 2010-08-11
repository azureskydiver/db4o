using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using Db4objects.Db4o;

namespace Db4oDoc.Code.Query.NativeQueries
{
    public class NativeQueriesSorting
    {
        private const string DatabaseFile = "database.db4o";

        public static void Main(string[] args)
        {
            CleanUp();
            using (IObjectContainer container = Db4oEmbedded.OpenFile(DatabaseFile))
            {
                StoreData(container);

                NativeQuerySorting(container);
            }
        }


        private static void NativeQuerySorting(IObjectContainer container)
        {
            // #example: Native query with sorting
            IList<Pilot> pilots = container.Query(
                delegate(Pilot p) { return p.Age > 18; },
                delegate(Pilot p1, Pilot p2) { return p1.Name.CompareTo(p2.Name); });
            // #end example

            ListResult(pilots);
        }

        private static void CleanUp()
        {
            File.Delete(DatabaseFile);
        }


        private static void ListResult(IEnumerable result)
        {
            foreach (object obj in result)
            {
                Console.WriteLine(obj);
            }
        }

        private static void StoreData(IObjectContainer container)
        {
            Pilot john = new Pilot("John", 42);
            Pilot joanna = new Pilot("Joanna", 45);
            Pilot jenny = new Pilot("Jenny", 21);
            Pilot rick = new Pilot("Rick", 33);

            container.Store(new Car(john, "Ferrari"));
            container.Store(new Car(joanna, "Mercedes"));
            container.Store(new Car(jenny, "Volvo"));
            container.Store(new Car(rick, "Fiat"));
        }
    }

    internal class Pilot
    {
        private string name;
        private int age;

        public Pilot(string name, int age)
        {
            this.name = name;
            this.age = age;
        }

        public string Name
        {
            get { return name; }
            set { name = value; }
        }

        public int Age
        {
            get { return age; }
            set { age = value; }
        }

        public override string ToString()
        {
            return string.Format("Name: {0}, Age: {1}", name, age);
        }
    }

    internal class Car
    {
        private Pilot pilot;
        private string name;


        public Car(Pilot pilot, string name)
        {
            this.pilot = pilot;
            this.name = name;
        }

        public Pilot Pilot
        {
            get { return pilot; }
            set { pilot = value; }
        }

        public string Name
        {
            get { return name; }
            set { name = value; }
        }

        public override string ToString()
        {
            return string.Format("Pilot: {0}, Name: {1}", pilot, name);
        }
    }
}