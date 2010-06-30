using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4oDoc.Code.Configuration.Common
{
    public class CommonConfigurationExamples
    {
        private const string DatabaseFile = "database.db4o";


        private static void ExampleForCommonConfig()
        {
            // #example: change activation depth
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.ActivationDepth = 2;
            // other configurations...
            configuration.IdSystem.UsePointerBasedSystem();

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);
            // #end example
            container.Close();
        }

        private static void InternStrings()
        {
            // #example: intern strings
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.InternStrings = true;
            // #end example

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);

            container.Close();
        }

        private static void NameProvider()
        {
            // #example: set a name-provider
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.NameProvider(new SimpleNameProvider("Database"));
            // #end example

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);

            container.Close();
        }


        private static void ChangeWeakReferenceCollectionIntervall()
        {
            // #example: change weak reference collection interval
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.WeakReferenceCollectionInterval = (10*1000);
            // #end example

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);

            container.Close();
        }

        private static void MarkTransient()
        {
            CleanUp();

            // #example: add an transient marker annotatin
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.MarkTransient(typeof (TransientMarkerAttribute).FullName);
            // #end example

            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);
            container.Store(new WithTransient());
            container.Close();

            ReadWithTransientMarker();

            CleanUp();
        }

        private static void CleanUp()
        {
            System.IO.File.Delete(DatabaseFile);
        }

        private static void ReadWithTransientMarker()
        {
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.MarkTransient(typeof (TransientMarkerAttribute).FullName);
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile);
            WithTransient instance = container.Query<WithTransient>()[0];

            AssertTransientNotStored(instance);

            container.Close();
        }

        private static void AssertTransientNotStored(WithTransient instance)
        {
            if (null != instance.TransientString)
            {
                throw new Exception("Transient was stored!");
            }
        }
    }

    internal class WithTransient
    {
        [TransientMarker] private string transientString = "New";

        public string TransientString
        {
            get { return transientString; }
            set { transientString = value; }
        }
    }

    [AttributeUsage(AttributeTargets.Field)]
    internal class TransientMarkerAttribute : Attribute
    {
    }
}