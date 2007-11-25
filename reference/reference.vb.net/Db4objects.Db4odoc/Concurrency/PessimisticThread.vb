' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.Concurrency

    Public Class PessimisticThread
        Private _server As IObjectServer
        Private _container As IObjectContainer
        Private _id As String

        Public Sub New(ByVal id As String, ByVal server As IObjectServer)
            _id = id
            Me._server = server
            _container = _server.OpenClient
        End Sub
        ' end New

        Private ReadOnly Property Name() As String
            Get
                Return _id
            End Get
        End Property
        ' end Name

        Public Sub Run()
            Try
                Dim result As IObjectSet = _container.Get(GetType(Pilot))
                While result.HasNext
                    Dim pilot As Pilot = CType(result.Next, Pilot)
                    ' with pessimistic approach the object is locked as soon 
                    ' as we get it 
                    If Not _container.Ext.SetSemaphore("LOCK_" + _container.Ext.GetID(pilot).ToString(), 0) Then
                        Console.WriteLine("Error. The object is locked")
                    End If
                    Console.WriteLine(Name + "Updating pilot: " + pilot.ToString())
                    pilot.AddPoints(1)
                    _container.Set(pilot)
                    ' The changes should be committed to be 
                    ' visible to the other clients
                    _container.Commit()
                    _container.Ext.ReleaseSemaphore("LOCK_" + _container.Ext.GetID(pilot).ToString())
                    Console.WriteLine(Name + "Updated pilot: " + pilot.ToString())
                    Console.WriteLine()
                End While
            Finally
                _container.Close()
            End Try
        End Sub
        ' end Run

    End Class
End Namespace