/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.IO;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Replication;
using Db4objects.Db4o.Config;
using Db4objects.Drs;

namespace Db4objects.Db4odoc.ReplicationEvents
{
    class EventsExample
    {
        //public static void Main(string[] args)
        //{
        //    ConflictResolutionExample();
        //}
        //// end Main

        //public class ConflictReplicationEventListener : Db4objects.Drs.IReplicationEventListener
        //{
        //    public virtual void OnReplicate(Db4objects.Drs.IReplicationEvent e)
        //    {
        //        if (e.IsConflict())
        //        {
        //            IObjectState chosenObjectState = e.StateInProviderB();
        //            e.OverrideWith(chosenObjectState);
        //        }
        //    }
        //}
        //// end ConflictReplicationEventListener


        //private static void ConflictResolutionExample()
        //{
        //    Db4oFactory.Configure().GenerateUUIDs(ConfigScope.GLOBALLY);
        //    Db4oFactory.Configure().GenerateVersionNumbers(ConfigScope.GLOBALLY);
        //    //	Open databases
        //    IObjectContainer desktop = Db4oFactory.OpenFile("desktop.db4o");
        //    IObjectContainer handheld = Db4oFactory.OpenFile("handheld.db4o");

        //    Pilot pilot = new Pilot("Scott Felton", 200);
        //    handheld.Set(pilot);
        //    handheld.Commit();
        //    /* Clean the reference cache to make sure that objects in memory
        //    * won't interfere
        //    */
        //    handheld.Ext().Refresh(typeof(Pilot), System.Int32.MaxValue);

        //    /* Replicate changes from handheld to desktop
        //     * Note, that only objects replicated from one database to another will 
        //     * be treated as the same. If you will create an object and save it to both
        //     * databases, dRS will count them as 2 different objects with identical 
        //     * fields.
        //     */
        //    IReplicationSession replication = Db4objects.Drs.Replication.Begin(handheld, desktop);
        //    IObjectSet changedObjects = replication.ProviderA().ObjectsChangedSinceLastReplication();
        //    while (changedObjects.HasNext())
        //        replication.Replicate(changedObjects.Next());
        //    replication.Commit();

        //    // change object on the handheld
        //    pilot = (Pilot)handheld.Query(typeof(Pilot)).Next();
        //    pilot.Name = "S.Felton";
        //    handheld.Set(pilot);
        //    handheld.Commit();

        //    //	change object on the desktop
        //    pilot = (Pilot)desktop.Query(typeof(Pilot)).Next();
        //    pilot.Name = "Scott";
        //    desktop.Set(pilot);
        //    desktop.Commit();

        //    /* The replication will face a conflict: Pilot object was changed on the 
        //    * handheld and on the desktop.
        //    * To resolve this conflict we will add an event handler, which makes
        //    * desktop changes dominating.
        //    */
        //    IReplicationEventListener listener = new ConflictReplicationEventListener();
        //    replication = Db4objects.Drs.Replication.Begin(handheld, desktop, listener);

        //    //The state of the desktop after the replication should not change, as it dominates
        //    changedObjects = replication.ProviderA().ObjectsChangedSinceLastReplication();
        //    while (changedObjects.HasNext())
        //        replication.Replicate(changedObjects.Next());

        //    //Commit
        //    replication.Commit();
        //    replication.Close();

        //    // Check what we've got on the desktop
        //    IObjectSet result = desktop.Query(typeof(Pilot));
        //    System.Console.WriteLine(result.Size());
        //    while (result.HasNext())
        //    {
        //        System.Console.WriteLine(result.Next());
        //    }
        //    handheld.Close();
        //    desktop.Close();

        //    File.Delete("handheld.db4o");
        //    File.Delete("desktop.db4o");

        //}
        // end conflictResolutionExample
    }
}


