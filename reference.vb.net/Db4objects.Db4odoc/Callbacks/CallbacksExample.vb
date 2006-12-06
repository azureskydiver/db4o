Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Events

Namespace Db4objects.Db4odoc.Callbacks

    Class CallbacksExample
        Private Shared ReadOnly YapFileName As String = "formula1.yap"
        Private Shared _container As IObjectContainer

        Public Shared Sub Main(ByVal args As String())
            TestCreated()
            TestCascadedDelete()
            TestIntegrityCheck()
        End Sub
        ' end Main

        Private Shared Function OpenContainer() As IObjectContainer
            If _container Is Nothing Then
                _container = Db4oFactory.OpenFile(YapFileName)
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

        Private Shared Sub OnCreated(ByVal sender As Object, ByVal args As ObjectEventArgs)
            Dim obj As Object = args.Object
            If TypeOf obj Is Pilot Then
                Console.WriteLine(obj.ToString)
            End If
        End Sub
        ' end OnCreated

        Public Shared Sub TestCreated()
            File.Delete(YapFileName)
            Dim container As IObjectContainer = OpenContainer
            Try
                Dim registry As IEventRegistry = EventRegistryFactory.ForObjectContainer(container)
                ' register an event handler, which will print all the car objects, that have been Created
                AddHandler registry.Created, AddressOf OnCreated
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                container.Set(car)
            Finally
                CloseContainer()
            End Try
        End Sub
        ' end TestCreated

        Private Shared Sub FillContainer()
            File.Delete(YapFileName)
            Dim container As IObjectContainer = OpenContainer()
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                container.Set(car)
                car = New Car("Ferrari", New Pilot("Finn Kimi Raikkonen"))
                container.Set(car)
            Finally
                CloseContainer()
            End Try
        End Sub
        ' end FillContainer

        Private Shared Sub OnDeleted(ByVal sender As Object, ByVal args As ObjectEventArgs)
            Dim obj As Object = args.Object
            If TypeOf obj Is Car Then
                OpenContainer.Delete(CType(obj, Car).Pilot)
            End If
        End Sub
        ' end OnDeleted

        Public Shared Sub TestCascadedDelete()
            FillContainer()
            Dim container As IObjectContainer = OpenContainer
            Try
                ' check the contents of the database
                Dim result As IObjectSet = container.Get(Nothing)
                ListResult(result)
                Dim registry As IEventRegistry = EventRegistryFactory.ForObjectContainer(container)
                ' register an event handler, which will delete the pilot when his car is Deleted 
                AddHandler registry.Deleted, AddressOf OnDeleted
                ' delete all the cars
                result = container.Query(GetType(Car))
                While result.HasNext
                    container.Delete(result.Next)
                End While
                ' check if the database is empty
                result = container.Get(Nothing)
                ListResult(result)
            Finally
                CloseContainer()
            End Try
        End Sub
        ' end TestCascadedDelete

        Private Shared Sub OnDeleting(ByVal sender As Object, ByVal args As CancellableObjectEventArgs)
            Dim obj As Object = args.Object
            If TypeOf obj Is Pilot Then
                ' search for the cars referencing the pilot object
                Dim container As IObjectContainer = OpenContainer()
                Dim q As IQuery = container.Query
                q.Constrain(GetType(Car))
                q.Descend("_pilot").Constrain(obj)
                Dim result As IObjectSet = q.Execute
                If result.Size > 0 Then
                    Console.WriteLine("Object " + CType(obj, Pilot).ToString() + " can't be Deleted as object container has references to it")
                    args.Cancel()
                End If
            End If
        End Sub
        ' end OnDeleting

        Public Shared Sub TestIntegrityCheck()
            FillContainer()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim registry As IEventRegistry = EventRegistryFactory.ForObjectContainer(container)
                ' register an event handler, which will stop Deleting a pilot when it is referenced from a car 
                AddHandler registry.Deleting, AddressOf OnDeleting
                ' check the contents of the database
                Dim result As IObjectSet = container.Get(Nothing)
                ListResult(result)
                ' try to delete all the pilots
                result = container.Get(GetType(Pilot))
                While result.HasNext
                    container.Delete(result.Next)
                End While
                ' check if any of the objects were Deleted
                result = container.Get(Nothing)
                ListResult(result)
            Finally
                CloseContainer()
            End Try
        End Sub
        ' end TestIntegrityCheck

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Size)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

    End Class
End Namespace