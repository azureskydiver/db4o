using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4oDoc.Code.Configuration.ObjectConfig
{
    public class ObjectConfigurationExamples
    {
        private const String DatabaseFile = "database.db4o";

        private static void SetMinimalActivationDepth()
        {
            // #example: Set minimum activation depth
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.ObjectClass(typeof (Person)).MinimumActivationDepth(2);
            // #end example

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);
            container.Close();
        }
        private static void CallConstructor()
        {
            // #example: Call constructor
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.ObjectClass(typeof(Person)).CallConstructor(true);
            // #end example

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);
            container.Close();
        }

    }

    public class Person
    {
    }
}