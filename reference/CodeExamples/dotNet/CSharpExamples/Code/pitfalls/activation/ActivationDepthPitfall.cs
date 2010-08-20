using System;
using System.IO;
using System.Linq;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Linq;

namespace Db4oDoc.Code.Pitfalls.Activation
{
    public class ActivationDepthPitfall
    {
        public const string DatabaseFile = "database.db4o";

        public static void Main(string[] args)
        {
            CleanUp();
            PrepareDeepObjGraph();

            try
            {
                RunIntoActivationIssue();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }

            FixItWithHigherActivationDepth();
        }

        private static void FixItWithHigherActivationDepth()
        {
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.ActivationDepth = 16;
            using (IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o"))
            {
                Person jodie = QueryForJodie(container);

                Person julia = jodie.Mother.Mother.Mother.Mother.Mother;

                Console.WriteLine(julia.Name);
                String joannaName = julia.Mother.Name;
                Console.WriteLine(joannaName);
            }
        }

        private static void RunIntoActivationIssue()
        {
            using (IObjectContainer container = Db4oEmbedded.OpenFile(DatabaseFile))
            {
                // #example: run into not activated objects
                Person jodie = QueryForJodie(container);

                Person julia = jodie.Mother.Mother.Mother.Mother.Mother;

                // This will print null
                // Because julia is not activated
                // and therefore all fields are not set
                Console.WriteLine(julia.Name);
                // This will throw a NullPointerException.
                // Because julia is not activated
                // and therefore all fields are not set
                String joannaName = julia.Mother.Name;
                // #end example
            }
        }

        private static void CleanUp()
        {
            File.Delete(DatabaseFile);
        }

        private static Person QueryForJodie(IObjectContainer container)
        {
            return (from Person p in container
                    where p.Name == "Jodie"
                    select p).First();
        }

        private static void PrepareDeepObjGraph()
        {
            using (IObjectContainer container = Db4oEmbedded.OpenFile(DatabaseFile))
            {
                Person joanna = new Person("Joanna");
                Person jenny = new Person(joanna, "Jenny");
                Person julia = new Person(jenny, "Julia");
                Person jill = new Person(julia, "Jill");
                Person joel = new Person(jill, "Joel");
                Person jamie = new Person(joel, "Jamie");
                Person jodie = new Person(jamie, "Jodie");
                container.Store(jodie);
            }
        }
    }


    internal class Person
    {
        private Person mother;
        private string name;

        public Person(string name)
        {
            mother = mother;
            this.name = name;
        }

        public Person(Person mother, string name)
        {
            this.mother = mother;
            this.name = name;
        }

        public Person Mother
        {
            get { return mother; }
        }

        public string Name
        {
            get { return name; }
        }
    }
}