using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Linq;

namespace Db4oDoc.Code.Pitfalls.UpdateDepth
{
    public class UpdateDepthPitfall
    {
        public const String DatabaseFile = "database.db4o";

        public static void Main(string[] args)
        {
            CleanUp();
            PrepareDeepObjGraph();


            ToLowUpdateDeph();
            UpdateDepth();
        }

        private static void ToLowUpdateDeph()
        {
            // #example: Update doesn't work
            using (IObjectContainer container = Db4oEmbedded.OpenFile(DatabaseFile))
            {
                Person jodie = QueryForJodie(container);
                jodie.Add(new Person("Jamie"));
                // Remember that a collection is also a regular object
                // so with the default-update depth of one, only the changes
                // on the person-object are stored, but not the changes on
                // the friend-list.
                container.Store(jodie);
            }
            using (IObjectContainer container = Db4oEmbedded.OpenFile(DatabaseFile))
            {
                Person jodie = QueryForJodie(container);
                foreach (Person person in jodie.Friends)
                {
                    // the added friend is gone, because the update-depth is to low
                    Console.WriteLine("Friend=" + person.Name);
                }
            }
            // #end example
        }

        private static void UpdateDepth()
        {
            // #example: A higher update depth fixes the issue
            IEmbeddedConfiguration config = Db4oEmbedded.NewConfiguration();
            config.Common.UpdateDepth = 2;
            using (IObjectContainer container = Db4oEmbedded.OpenFile(config, DatabaseFile))
            {
                Person jodie = QueryForJodie(container);
                jodie.Add(new Person("Jamie"));
                // Remember that a collection is also a regular object
                // so with the default-update depth of one, only the changes
                // on the person-object are stored, but not the changes on
                // the friend-list.
                container.Store(jodie);
            }
            config = Db4oEmbedded.NewConfiguration();
            config.Common.UpdateDepth = 2;
            using (IObjectContainer container = Db4oEmbedded.OpenFile(config, DatabaseFile))
            {
                Person jodie = QueryForJodie(container);
                foreach (Person person in jodie.Friends)
                {
                    // the added friend is gone, because the update-depth is to low
                    Console.WriteLine("Friend=" + person.Name);
                }
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
                Person jodie = new Person("Jodie");

                jodie.Add(new Person("Joanna"));
                jodie.Add(new Person("Julia"));
                container.Store(jodie);
            }
        }
    }

    internal class Person
    {
        private IList<Person> friends = new List<Person>();

        private string name;

        internal Person(string name)
        {
            this.name = name;
        }


        public IList<Person> Friends
        {
            get { return friends; }
        }

        public string Name
        {
            get { return name; }
        }

        public void Add(Person item)
        {
            friends.Add(item);
        }
    }
}