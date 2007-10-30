using System;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

using Db4objects.Drs;

namespace Db4objects.Db4odoc.Replication
{
    public class ReplicationExample
    {
        //private const string DtFileName = "reference.db4o";
        //private const string HhFileName = "handheld.db4o";

        //public static void ConfigureReplication()
        //{
        //    Db4oFactory.Configure().GenerateUUIDs(ConfigScope.GLOBALLY);
        //    Db4oFactory.Configure().GenerateVersionNumbers(ConfigScope.GLOBALLY);
        //}
        //// end configureReplication

        //public static void ConfigureReplicationPilot()
        //{
        //    Db4oFactory.Configure().ObjectClass(typeof(Pilot)).GenerateUUIDs(true);
        //    Db4oFactory.Configure().ObjectClass(typeof(Pilot)).GenerateVersionNumbers(true);
        //}
        //// end configureReplicationPilot

        //public static void ConfigureForExisting()
        //{
        //    Db4oFactory.Configure().ObjectClass(typeof(Pilot)).EnableReplication(true);
        //    Db4objects.Db4o.Defragment.Defragment.Defrag(DtFileName);
        //}
        //// end configureForExisting

        //public static void Replicate()
        //{
        //    IObjectContainer desktop = Db4oFactory.OpenFile(DtFileName);
        //    IObjectContainer handheld = Db4oFactory.OpenFile(HhFileName);
        //    IReplicationSession replication = Db4objects.Drs.Replication.Begin(handheld, desktop);
            
        //    /*
        //     * There is no need to replicate all the objects each time. 
        //     * ObjectsChangedSinceLastReplication methods gives us 
        //     * a list of modified objects
        //     */
        //    IObjectSet changed = replication.ProviderA().ObjectsChangedSinceLastReplication();
        //    //Iterate through changed objects, replicate them
        //    while (changed.HasNext())
        //    {
        //        replication.Replicate(changed .Next ());
        //    }
        //    replication.Commit();
        //}
        //// end replicate	

        //public static void ReplicatePilots()
        //{
        //    IObjectContainer desktop = Db4oFactory.OpenFile(DtFileName);
        //    IObjectContainer handheld = Db4oFactory.OpenFile(HhFileName);
        //    IReplicationSession replication = Db4objects.Drs.Replication.Begin(handheld, desktop);
        //    IObjectSet changed = replication.ProviderB().ObjectsChangedSinceLastReplication();
        //    //Iterate changed objects, replicate them
        //    while (changed.HasNext())
        //    {
        //        object p = changed .Next();
        //        if ( p is Pilot)
        //        {
        //            if (((Pilot)p).Name.StartsWith("S"))
        //            {
        //                replication.Replicate(p);
        //            }
        //        }
        //    }
        //    replication.Commit();
        //}
        //// end ReplicatePilots

        //public static void ReplicateBiDirectional()
        //{
        //    IObjectContainer desktop = Db4oFactory.OpenFile(DtFileName);
        //    IObjectContainer handheld = Db4oFactory.OpenFile(HhFileName);
        //    IReplicationSession replication = Db4objects.Drs.Replication.Begin(handheld, desktop);
        //    IObjectSet changed = replication.ProviderA().ObjectsChangedSinceLastReplication();
        //    while (changed.HasNext())
        //    {
        //        replication.Replicate(changed.Next());
        //    }

        //    // Add one more loop for bi-directional replication
        //    changed = replication.ProviderB().ObjectsChangedSinceLastReplication();
        //    while (changed.HasNext())
        //    {
        //        replication.Replicate(changed.Next());
        //    }
        //    replication.Commit();
        //}
        // end ReplicateBiDirectional
    }

}
