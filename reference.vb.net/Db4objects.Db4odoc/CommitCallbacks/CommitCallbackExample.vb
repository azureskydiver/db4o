' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Events
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.CommitCallbacks

    Class CommitCallbackExample
        Private Const FileName As String = "test.db"
        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            File.Delete(FileName)
            Try
                Configure()
                StoreFirstObject()
                StoreOtherObjects()
            Finally
                Container().Close()
            End Try
        End Sub
        ' end Main

        Private Shared Function Container() As IObjectContainer
            If _container Is Nothing Then
                _container = Db4oFactory.OpenFile(FileName)
            End If
            Return _container
        End Function
        ' end Container

        Private Shared Sub Configure()
            Dim registry As IEventRegistry = EventRegistryFactory.ForObjectContainer(Container)
            ' register an event handler, which will check object uniqueness on commit
            AddHandler registry.Committing, AddressOf OnCommitting
        End Sub
        ' end Configure

        Private Shared Sub OnCommitting(ByVal sender As Object, ByVal args As CommitEventArgs)
            ' uniqueness should be checked for both added and updated objects
            CheckUniqueness(args.Added)
            CheckUniqueness(args.Updated)
        End Sub
        ' end OnCommitting

        Private Shared Sub CheckUniqueness(ByVal collection As IObjectInfoCollection)
            For Each info As IObjectInfo In collection
                ' only check for Item objects
                If (TypeOf info.GetObject() Is Item) Then
                    Dim item As Item = CType(info.GetObject, Item)
                    If item Is Nothing Then
                        Continue For
                    End If
                    ' search for objects with the same fields in the database
                    Dim found As IObjectSet = Container.Get(New Item(item.Number, item.Word))
                    If found.Count > 1 Then
                        Throw New Db4oException("Object is not unique: " + item.ToString())
                    End If
                End If
            Next
        End Sub
        ' end CheckUniqueness

        Private Shared Sub StoreFirstObject()
            Dim container As IObjectContainer = CommitCallbackExample.Container()
            Try
                ' end creating and storing item1 to the database
                Dim item As Item = New Item(1, "one")
                container.Set(item)
                ' no problems here
                container.Commit()
            Catch ex As Db4oException
                System.Console.WriteLine(ex.Message)
                container.Rollback()
            End Try
        End Sub
        ' end StoreFirstObject

        Private Shared Sub StoreOtherObjects()
            Dim container As IObjectContainer = CommitCallbackExample.Container()
            Dim item As Item = New Item(2, "one")
            container.Set(item)
            item = New Item(1, "two")
            container.Set(item)
            Try
                container.Commit()
            Catch ex As Db4oException
                System.Console.WriteLine(ex.Message)
                container.Rollback()
            End Try
            System.Console.WriteLine("Commit successful")
            item = New Item(1, "one")
            container.Set(item)
            Try
                container.Commit()
            Catch ex As Db4oException
                System.Console.WriteLine(ex.Message)
                container.Rollback()
            End Try
        End Sub
        ' end StoreOtherObjects

    End Class
End Namespace