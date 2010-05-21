using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Internal;

namespace Db4oDoc.Code.DisconnectedObj.IdExamples
{
    public class AutoIncrementExample : IIdExample<int>
    {
        public static IIdExample<int> Create()
        {
            return new AutoIncrementExample();
        }

        public int IdForObject(object obj, IObjectContainer container)
        {
            // #example: get the id
            IDHolder idHolder = (IDHolder) obj;
            int id = idHolder.Id;
            // #end example
            return id;
        }

        public object ObjectForID(int idForObject, IObjectContainer container)
        {
            int id = idForObject;
            // #example: get an object by its id
            object instance = container.Query(delegate(IDHolder o) { return o.Id == id; })[0];
            // #end example
            return instance;
        }

        public void Configure(IEmbeddedConfiguration configuration)
        {
            // #example: index the id-field
            configuration.Common.ObjectClass(typeof (IDHolder)).ObjectField("id").Indexed(true);
            // #end example
        }

        public void RegisterEventOnContainer(IObjectContainer container)
        {
            // #example: use events to assign the ids
            AutoIncrement increment = new AutoIncrement(container);
            IEventRegistry eventRegistry = EventRegistryFactory.ForObjectContainer(container);
            
            eventRegistry.Creating+=
                delegate(object sender, CancellableObjectEventArgs args)
                {
                    if (args.Object is IDHolder)
                    {
                        IDHolder idHolder = (IDHolder)args.Object;
                        idHolder.Id = increment.GetNextID(idHolder.GetType());
                    }    
                };
            eventRegistry.Committing +=
                delegate(object sender, CommitEventArgs args)
                    {
                        increment.StoreState();    
                    };
            // #end example
        }
    }
}