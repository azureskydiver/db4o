using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Replication
{
    public class ReplicationExample
    {
        readonly public static String DtFileName = "formula1.yap";
        readonly public static String HhFileName = "handheld.yap";

        public static void ConfigureReplication()
        {
            Db4oFactory.Configure().GenerateUUIDs(Int32.MaxValue);
            Db4oFactory.Configure().GenerateVersionNumbers(Int32.MaxValue);
        }
        // end configureReplication

        public static void ConfigureReplicationPilot()
        {
            Db4oFactory.Configure().ObjectClass(typeof(Pilot)).GenerateUUIDs(true);
            Db4oFactory.Configure().ObjectClass(typeof(Pilot)).GenerateVersionNumbers(true);
        }
        // end configureReplicationPilot

        public static void ConfigureForExisting()
        {
            Db4oFactory.Configure().ObjectClass(typeof(Pilot)).EnableReplication(true);
            Db4objects.Db4o.Defragment.Defragment.Defrag(DtFileName);
        }
        // end configureForExisting

        public static void Replicate()
        {
            IObjectContainer desktop = Db4oFactory.OpenFile(DtFileName);
            IObjectContainer handheld = Db4oFactory.OpenFile(HhFileName);
            Db4objects.Drs.IReplicationSession replication = Db4objects.Drs.Replication.Begin(handheld, desktop);
            /*
             * There is no need to replicate all the objects each time. 
             * ObjectsChangedSinceLastReplication methods gives us 
             * a list of modified objects
             */
            IObjectSet changed = replication.ProviderA().ObjectsChangedSinceLastReplication();
            //Iterate changed objects, replicate them
            while (changed.HasNext())
            {
                Pilot p = (Pilot)changed
                    .Next();
                if (p.Name.StartsWith("S"))
                {
                    replication.Replicate(p);
                }
            }
            replication.Commit();
        }
        // end replicate	
    }
}
