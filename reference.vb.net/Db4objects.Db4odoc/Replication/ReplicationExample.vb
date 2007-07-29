Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query
Imports Db4objects.Drs


Namespace Db4objects.Db4odoc.Replicating
    Public Class ReplicationExample
        Public Shared ReadOnly DtFileName As String = "reference.db4o"
        Public Shared ReadOnly HhFileName As String = "handheld.db4o"

        Public Shared Sub ConfigureReplication()
            Db4oFactory.Configure().GenerateUUIDs(ConfigScope.GLOBALLY)
            Db4oFactory.Configure().GenerateVersionNumbers(ConfigScope.GLOBALLY)
        End Sub
        ' end configureReplication

        Public Shared Sub ConfigureReplicationPilot()
            Db4oFactory.Configure().ObjectClass(GetType(Pilot)).GenerateUUIDs(True)
            Db4oFactory.Configure().ObjectClass(GetType(Pilot)).GenerateVersionNumbers(True)
        End Sub
        ' end configureReplicationPilot

        Public Shared Sub ConfigureForExisting()
            Db4oFactory.Configure().ObjectClass(GetType(Pilot)).EnableReplication(True)
            Defragment.Defragment.Defrag(DtFileName)
        End Sub
        ' end configureForExisting

        Public Shared Sub Replicate()
            Dim desktop As IObjectContainer = Db4oFactory.OpenFile(DtFileName)
            Dim handheld As IObjectContainer = Db4oFactory.OpenFile(HhFileName)
            Dim replic As IReplicationSession = Replication.Begin(handheld, desktop) '
            ' There is no need to replicate all the objects each time. 
            ' ObjectsChangedSinceLastReplication methods gives us 
            ' a list of modified objects
            Dim changed As IObjectSet = replic.ProviderA().ObjectsChangedSinceLastReplication()
            'Iterate through the changed objects, replicate them
            While changed.HasNext()
                replic.Replicate(changed.Next())
            End While
            replic.Commit()
        End Sub
        ' end replicate	

        Public Shared Sub ReplicatePilots()
            Dim desktop As IObjectContainer = Db4oFactory.OpenFile(DtFileName)
            Dim handheld As IObjectContainer = Db4oFactory.OpenFile(HhFileName)
            Dim replic As IReplicationSession = Replication.Begin(handheld, desktop) '
            ' There is no need to replicate all the objects each time. 
            ' ObjectsChangedSinceLastReplication methods gives us 
            ' a list of modified objects
            Dim changed As IObjectSet = replic.ProviderB().ObjectsChangedSinceLastReplication()
            ' Iterate through the changed objects,
            ' check if the name starts with "S" and replicate only those items
            While changed.HasNext()
                Dim p As Object = changed.Next
                If (p Is GetType(Pilot)) Then
                    If (CType(p, Pilot)).Name.StartsWith("S") Then
                        replic.Replicate(p)
                    End If
                End If
            End While
            replic.Commit()
        End Sub
        ' end ReplicatePilots

        Public Shared Sub ReplicateBiDirectional()
            Dim desktop As IObjectContainer = Db4oFactory.OpenFile(DtFileName)
            Dim handheld As IObjectContainer = Db4oFactory.OpenFile(HhFileName)
            Dim replic As IReplicationSession = Replication.Begin(handheld, desktop) '
            Dim changed As IObjectSet = replic.ProviderA().ObjectsChangedSinceLastReplication()
            'Iterate changed objects, replicate them
            While changed.HasNext()
                replic.Replicate(changed.Next())
            End While

            changed = replic.ProviderB().ObjectsChangedSinceLastReplication()
            ' Add one more loop for bi-directional replication
            While changed.HasNext()
                replic.Replicate(changed.Next())
            End While
            replic.Commit()
        End Sub
        ' end ReplicateBiDirectional
    End Class
End Namespace
