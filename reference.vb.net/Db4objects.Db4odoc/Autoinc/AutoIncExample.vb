' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

' This example shows how to implement object callbacks to assign 
' autoincremented ID to a special type of objects

Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Events
Namespace Db4objects.Db4odoc.Autoinc

    Class AutoIncExample
        Private Const Db4oFileName As String = "reference.db4o"
        Private Shared _container As IObjectContainer

        Public Shared Sub Main(ByVal args As String())
            Dim db As IObjectContainer = Nothing
            File.Delete(Db4oFileName)
            Try
                db = OpenContainer()
                RegisterCallback()
                StoreObjects()
                RetrieveObjects()
            Finally
                CloseContainer()
            End Try
        End Sub
        ' end Main

        Private Shared Function OpenContainer() As IObjectContainer
            If _container Is Nothing Then
                _container = Db4oFactory.OpenFile(Db4oFileName)
            End If
            Return _container
        End Function
        ' end OpenContainer

        Private Shared Sub CloseContainer()
            If Not (_container Is Nothing) Then
                _container.Close()
                _container = Nothing
            End If
        End Sub
        ' end CloseContainer

        Private Shared Sub RetrieveObjects()
            Dim db As IObjectContainer = OpenContainer()
            Dim result As IObjectSet = db.Get(New TestObject(Nothing))
            ListResult(result)
        End Sub
        ' end RetrieveObjects

        Private Shared Sub StoreObjects()
            Dim db As IObjectContainer = OpenContainer()
            Dim test As TestObject
            test = New TestObject("FirstObject")
            db.Set(test)
            test = New TestObject("SecondObject")
            db.Set(test)
            test = New TestObject("ThirdObject")
            db.Set(test)
        End Sub
        ' end StoreObjects

        Private Shared Sub RegisterCallback()
            Dim db As IObjectContainer = OpenContainer()
            ' register an event handler, which will assign autoincremented IDs to any
            ' object extending CountedObject, when the object is created
            Dim registry As IEventRegistry = EventRegistryFactory.ForObjectContainer(db)
            AddHandler registry.Creating, AddressOf OnCreating
        End Sub
        ' end RegisterCallback

        Private Shared Sub OnCreating(ByVal sender As Object, ByVal args As CancellableObjectEventArgs)
            Dim db As IObjectContainer = OpenContainer()
            Dim obj As Object = args.Object
            ' only for the objects extending the CountedObject
            If TypeOf obj Is CountedObject Then
                CType(obj, CountedObject).Id = GetNextId(db)
            End If
        End Sub
        ' end OnCreating

        Private Shared Function GetNextId(ByVal db As IObjectContainer) As Integer
            ' this function retrieves the next available ID from 
            ' the IncrementedId object
            Dim r As IncrementedId = IncrementedId.GetIdObject(db)
            Dim nRoll As Integer
            nRoll = r.GetNextID(db)
            Return nRoll
        End Function
        ' end GetNextId

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

    End Class
End Namespace