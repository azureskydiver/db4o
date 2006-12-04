Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports Db4oTools
Imports Db4objects.Drs


Namespace Db4objects.Db4odoc.Replicating
    Public Class ReplicationExample
        Public Shared ReadOnly DtFileName As String = "formula1.yap"
        Public Shared ReadOnly HhFileName As String = "handheld.yap"

        Public Shared Sub ConfigureReplication()
            Db4oFactory.Configure().GenerateUUIDs(Int32.MaxValue)
            Db4oFactory.Configure().GenerateVersionNumbers(Int32.MaxValue)
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
            ' 			 * There is no need to replicate all the objects each time. 
            ' 			 * ObjectsChangedSinceLastReplication methods gives us 
            ' 			 * a list of modified objects
            ' 			 */
            Dim provider As IReplicationProvider = replic.ProviderA()
            Dim changed As IObjectSet = provider.ObjectsChangedSinceLastReplication()
            'Iterate changed objects, replicate them
            While changed.HasNext()
                Dim p As Pilot = CType(changed.Next, Pilot)
                If p.Name.StartsWith("S") Then
                    replic.Replicate(p)
                End If
            End While
            replic.Commit()
        End Sub
        ' end replicate	
    End Class
End Namespace
