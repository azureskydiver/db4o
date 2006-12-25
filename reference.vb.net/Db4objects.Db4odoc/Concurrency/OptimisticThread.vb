' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.Threading
Imports System.Collections
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Events
Namespace Db4objects.Db4odoc.Concurrency

    Public Class OptimisticThread
        Private _server As IObjectServer
        Private _db As IObjectContainer
        Private _id As String
        Private _updateSuccess As Boolean = False
        Private _idVersions As Hashtable

        Public Sub New(ByVal id As String, ByVal server As IObjectServer)
            _id = id
            Me._server = server
            _db = _server.OpenClient
            RegisterCallbacks()
            _idVersions = New Hashtable
        End Sub
        ' end New

        Private ReadOnly Property Name() As String
            Get
                Return _id
            End Get
        End Property
        ' end Name

        Private Sub RandomWait()
            Try
                Dim r As Random = New Random
                Dim sleepTime As Integer = 5000 * r.Next(1)
                Thread.Sleep(sleepTime)
            Catch e As Exception
                Console.WriteLine("Interrupted!")
            End Try
        End Sub
        ' end RandomWait

        Private Sub OnUpdating(ByVal sender As Object, ByVal args As CancellableObjectEventArgs)
            Dim obj As Object = args.Object
            ' retrieve the object version from the database
            Dim currentVersion As Long = _db.Ext.GetObjectInfo(obj).GetVersion
            Dim id As Long = _db.Ext.GetID(obj)
            ' get the version saved at the object retrieval
            Dim i As IEnumerator = _idVersions.GetEnumerator
            Dim initialVersion As Long = CType(_idVersions(id), Long)
            If Not (initialVersion = currentVersion) Then
                Console.WriteLine(Name + "Collision: ")
                Console.WriteLine(Name + "Stored object: version: " + currentVersion.ToString())
                Console.WriteLine(Name + "New object: " + obj.ToString() + " version: " + initialVersion.ToString())
                args.Cancel()
            Else
                _updateSuccess = True
            End If
        End Sub
        ' end OnUpdating

        Public Sub RegisterCallbacks()
            Dim registry As IEventRegistry = EventRegistryFactory.ForObjectContainer(_db)
            ' register an event handler to check collisions on update
            AddHandler registry.Updating, AddressOf OnUpdating
        End Sub
        ' end RegisterCallbacks

        Public Sub Run()
            Try
                Dim result As IObjectSet = _db.Get(GetType(Pilot))
                While result.HasNext
                    Dim pilot As Pilot = CType(result.Next, Pilot)
                    ' We will need to set a lock to make sure that the 
                    ' object version corresponds to the object retrieved.
                    ' (Prevent other client committing changes
                    ' at the time between object retrieval and version
                    ' retrieval )
    			    If Not _db.Ext.SetSemaphore("LOCK_" + _db.Ext.GetID(pilot).ToString(), 3000) Then
                        Console.WriteLine("Error. The object is locked")
                        Continue While
                    End If
                    Dim objVersion As Long = _db.Ext.GetObjectInfo(pilot).GetVersion
                    _db.Ext.Refresh(pilot, Int32.MaxValue)
                    _db.Ext.ReleaseSemaphore("LOCK_" + _db.Ext.GetID(pilot).ToString())
                    ' save object version into _idVersions collection
                    ' This will be needed to make sure that the version
                    ' originally retrieved is the same in the database 
                    ' at the time of modification
                    Dim id As Long = _db.Ext.GetID(pilot)
                    _idVersions.Add(id, objVersion)
                    Console.WriteLine(Name + "Updating pilot: " + pilot.ToString() + " version: " + objVersion.ToString())
                    pilot.AddPoints(1)
                    _updateSuccess = False
                    RandomWait()
                    If Not _db.Ext.SetSemaphore("LOCK_" + _db.Ext.GetID(pilot).ToString(), 3000) Then
                        Console.WriteLine("Error. The object is locked")
                        Continue While
                    End If
                    _db.Set(pilot)
                    ' The changes should be committed to be 
                    ' visible to the other clients
                    _db.Commit()
                    _db.Ext.ReleaseSemaphore("LOCK_" + _db.Ext.GetID(pilot).ToString())
                    If _updateSuccess Then
                        Console.WriteLine(Name + "Updated pilot: " + pilot.ToString())
                    End If
                    Console.WriteLine()
                    ' The object version is not valid after commit
                    ' - should be removed
                    _idVersions.Remove(id)
                End While
            Finally
                _db.Close()
            End Try
        End Sub
        ' end Run

    End Class
End Namespace