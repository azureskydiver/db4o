' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Imports System.IO
Imports System.Collections
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Replication
Imports Db4objects.Db4o.Config
Imports Db4objects.Drs

Namespace Db4objects.Db4odoc.ReplicationEvents
    Class EventsExample
        Public Shared Sub Main(ByVal args As String())
            ConflictResolutionExample()
        End Sub
        ' end Main

        Public Class ConflictReplicationEventListener
            Implements Db4objects.Drs.IReplicationEventListener
            Public Overridable Sub OnReplicate(ByVal e As Db4objects.Drs.IReplicationEvent) Implements Db4objects.Drs.IReplicationEventListener.OnReplicate
                If e.IsConflict() Then
                    Dim chosenObjectState As IObjectState = e.StateInProviderB()
                    e.OverrideWith(chosenObjectState)
                End If
            End Sub
        End Class
        ' end ConflictReplicationEventListener


        Private Shared Sub ConflictResolutionExample()
            Db4oFactory.Configure().GenerateUUIDs(ConfigScope.GLOBALLY)
            Db4oFactory.Configure().GenerateVersionNumbers(ConfigScope.GLOBALLY)
            '	Open databases
            Dim desktop As IObjectContainer = Db4oFactory.OpenFile("desktop.db4o")
            Dim handheld As IObjectContainer = Db4oFactory.OpenFile("handheld.db4o")

            Dim pilot As New Pilot("Scott Felton", 200)
            handheld.[Set](pilot)
            handheld.Commit()
            ' Clean the reference cache to make sure that objects in memory
            ' won't interfere
            
            handheld.Ext().Refresh(GetType(Pilot), System.Int32.MaxValue)

            ' Replicate changes from handheld to desktop
            ' Note, that only objects replicated from one database to another will 
            ' be treated as the same. If you will create an object and save it to both
            ' databases, dRS will count them as 2 different objects with identical 
            ' fields.
            Dim replication As IReplicationSession = Db4objects.Drs.Replication.Begin(handheld, desktop)
            Dim changedObjects As IObjectSet = replication.ProviderA().ObjectsChangedSinceLastReplication()
            While changedObjects.HasNext()
                replication.Replicate(changedObjects.Next())
            End While
            replication.Commit()

            ' change object on the handheld
            pilot = DirectCast(handheld.Query(GetType(Pilot)).[Next](), Pilot)
            pilot.Name = "S.Felton"
            handheld.[Set](pilot)
            handheld.Commit()

            '	change object on the desktop
            pilot = DirectCast(desktop.Query(GetType(Pilot)).[Next](), Pilot)
            pilot.Name = "Scott"
            desktop.[Set](pilot)
            desktop.Commit()

            ' The replication will face a conflict: Pilot object was changed on the 
            ' handheld and on the desktop.
            ' To resolve this conflict we will add an event handler, which makes
            ' desktop changes dominating.
            Dim listener As IReplicationEventListener = New ConflictReplicationEventListener()
            replication = Db4objects.Drs.Replication.Begin(handheld, desktop, listener)

            'The state of the desktop after the replication should not change, as it dominates
            changedObjects = replication.ProviderA().ObjectsChangedSinceLastReplication()
            While changedObjects.HasNext()
                replication.Replicate(changedObjects.[Next]())
            End While

            'Commit
            replication.Commit()
            replication.Close()

            ' Check what we've got on the desktop
            Dim result As IObjectSet = desktop.Query(GetType(Pilot))
            System.Console.WriteLine(result.Size())
            While result.HasNext()
                System.Console.WriteLine(result.[Next]())
            End While
            handheld.Close()
            desktop.Close()

            File.Delete("handheld.db4o")
            File.Delete("desktop.db4o")

        End Sub
        ' end conflictResolutionExample

    End Class
End Namespace