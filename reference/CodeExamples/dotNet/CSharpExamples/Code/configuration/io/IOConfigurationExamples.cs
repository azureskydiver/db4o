using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.IO;

namespace Db4oDoc.Code.Configuration.IO
{
    public class IOConfigurationExamples
    {
        public static void SpecifyGrowStrategyForMemoryStorage()
        {
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            // #example: Using memory-storage with constant grow strategy
            IGrowthStrategy growStrategy = new ConstantGrowthStrategy(100);
            MemoryStorage memory = new MemoryStorage(growStrategy);
            configuration.File.Storage = memory;
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
        }

        public static void UsingMemoryStorage()
        {
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            // #example: Using memory-storage
            MemoryStorage memory = new MemoryStorage();
            configuration.File.Storage = memory;
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
        }

        public static void StorageStack()
        {
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            // #example: You stack up different storage-decorator to add functionality
            // the basic file storage
            IStorage fileStorage = new FileStorage();
            // add your own decorator
            IStorage myStorageDecorator = new MyStorageDecorator(fileStorage);
            // add caching to the storage
            IStorage storageWithCaching = new CachingStorage(myStorageDecorator);
            // finally configure db4o with our storage-stack
            configuration.File.Storage = storageWithCaching;
            // #end example
            IObjectContainer container = Db4oEmbedded.OpenFile(configuration, "database.db4o");
        }
    }

    ///
    ///This decorator does nothing. It's just used as an example
    ///
    internal class MyStorageDecorator : StorageDecorator
    {
        public MyStorageDecorator(IStorage storage) : base(storage)
        {
        }
    }
}