Imports System.IO
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.ClientServer
    Public Class ClientServerExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared ReadOnly ServerPort As Integer = 56128

        Public Shared ReadOnly ServerUser As String = "user"

        Public Shared ReadOnly ServerPassword As String = "password"

        Public Shared Sub Main(ByVal args As String())
            File.Delete(YapFileName)
            AccessLocalServer()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                SetFirstCar(db)
                SetSecondCar(db)
            Finally
                db.Close()
            End Try
            ConfigureDb4o()
            Dim server As IObjectServer = Db4oFactory.OpenServer(YapFileName, 0)
            Try
                QueryLocalServer(server)
                DemonstrateLocalReadCommitted(server)
                DemonstrateLocalRollback(server)
            Finally
                server.Close()
            End Try
            AccessRemoteServer()
            server = Db4oFactory.OpenServer(YapFileName, ServerPort)
            server.GrantAccess(ServerUser, ServerPassword)
            Try
                QueryRemoteServer(ServerPort, ServerUser, ServerPassword)
                DemonstrateRemoteReadCommitted(ServerPort, ServerUser, ServerPassword)
                DemonstrateRemoteRollback(ServerPort, ServerUser, ServerPassword)
            Finally
                server.Close()
            End Try
        End Sub
        ' end Main

        Public Shared Sub SetFirstCar(ByVal db As IObjectContainer)
            Dim pilot As Pilot = New Pilot("Rubens Barrichello", 99)
            Dim car As Car = New Car("BMW")
            car.Pilot = pilot
            db.[Set](car)
        End Sub
        ' end SetFirstCar

        Public Shared Sub SetSecondCar(ByVal db As IObjectContainer)
            Dim pilot As Pilot = New Pilot("Michael Schumacher", 100)
            Dim car As Car = New Car("Ferrari")
            car.Pilot = pilot
            db.[Set](car)
        End Sub
        ' end SetSecondCar

        Public Shared Sub AccessLocalServer()
            Dim server As IObjectServer = Db4oFactory.OpenServer(YapFileName, 0)
            Try
                Dim client As IObjectContainer = server.OpenClient()
                ' Do something with this client, or open more clients
                client.Close()
            Finally
                server.Close()
            End Try
        End Sub
        ' end AccessLocalServer

        Public Shared Sub QueryLocalServer(ByVal server As IObjectServer)
            Dim client As IObjectContainer = server.OpenClient()
            ListResult(client.[Get](New Car(Nothing)))
            client.Close()
        End Sub
        ' end QueryLocalServer

        Public Shared Sub ConfigureDb4o()
            Db4oFactory.Configure().ObjectClass(GetType(Car)).UpdateDepth(3)
        End Sub
        ' end ConfigureDb4o

        Public Shared Sub DemonstrateLocalReadCommitted(ByVal server As IObjectServer)
            Dim client1 As IObjectContainer = server.OpenClient()
            Dim client2 As IObjectContainer = server.OpenClient()
            Dim pilot As Pilot = New Pilot("David Coulthard", 98)
            Dim result As IObjectSet = client1.[Get](New Car("BMW"))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Pilot = pilot
            client1.[Set](car)
            ListResult(client1.[Get](New Car(Nothing)))
            ListResult(client2.[Get](New Car(Nothing)))
            client1.Commit()
            ListResult(client1.[Get](GetType(Car)))
            ListRefreshedResult(client2, client2.[Get](GetType(Car)), 2)
            client1.Close()
            client2.Close()
        End Sub
        ' end DemonstrateLocalReadCommitted

        Public Shared Sub DemonstrateLocalRollback(ByVal server As IObjectServer)
            Dim client1 As IObjectContainer = server.OpenClient()
            Dim client2 As IObjectContainer = server.OpenClient()
            Dim result As IObjectSet = client1.[Get](New Car("BMW"))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Pilot = New Pilot("Someone else", 0)
            client1.[Set](car)
            ListResult(client1.[Get](New Car(Nothing)))
            ListResult(client2.[Get](New Car(Nothing)))
            client1.Rollback()
            client1.Ext().Refresh(car, 2)
            ListResult(client1.[Get](New Car(Nothing)))
            ListResult(client2.[Get](New Car(Nothing)))
            client1.Close()
            client2.Close()
        End Sub
        ' end DemonstrateLocalRollback

        Public Shared Sub AccessRemoteServer()
            Dim server As IObjectServer = Db4oFactory.OpenServer(YapFileName, ServerPort)
            server.GrantAccess(ServerUser, ServerPassword)
            Try
                Dim client As IObjectContainer = Db4oFactory.OpenClient("localhost", ServerPort, ServerUser, ServerPassword)
                ' Do something with this client, or open more clients
                client.Close()
            Finally
                server.Close()
            End Try
        End Sub
        ' end AccessRemoteServer

        Public Shared Sub QueryRemoteServer(ByVal port As Integer, ByVal user As String, ByVal password As String)
            Dim client As IObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            ListResult(client.[Get](New Car(Nothing)))
            client.Close()
        End Sub
        ' end QueryRemoteServer

        Public Shared Sub DemonstrateRemoteReadCommitted(ByVal port As Integer, ByVal user As String, ByVal password As String)
            Dim client1 As IObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim client2 As IObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim pilot As Pilot = New Pilot("Jenson Button", 97)
            Dim result As IObjectSet = client1.[Get](New Car(Nothing))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Pilot = pilot
            client1.[Set](car)
            ListResult(client1.[Get](New Car(Nothing)))
            ListResult(client2.[Get](New Car(Nothing)))
            client1.Commit()
            ListResult(client1.[Get](New Car(Nothing)))
            ListResult(client2.[Get](New Car(Nothing)))
            client1.Close()
            client2.Close()
        End Sub
        ' end DemonstrateRemoteReadCommitted

        Public Shared Sub DemonstrateRemoteRollback(ByVal port As Integer, ByVal user As String, ByVal password As String)
            Dim client1 As IObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim client2 As IObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim result As IObjectSet = client1.[Get](New Car(Nothing))
            Dim car As Car = DirectCast(result.[Next](), Car)
            car.Pilot = New Pilot("Someone else", 0)
            client1.[Set](car)
            ListResult(client1.[Get](New Car(Nothing)))
            ListResult(client2.[Get](New Car(Nothing)))
            client1.Rollback()
            client1.Ext().Refresh(car, 2)
            ListResult(client1.[Get](New Car(Nothing)))
            ListResult(client2.[Get](New Car(Nothing)))
            client1.Close()
            client2.Close()
        End Sub
        ' end DemonstrateRemoteRollback

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult

        Public Shared Sub ListRefreshedResult(ByVal container As IObjectContainer, ByVal items As IObjectSet, ByVal depth As Integer)
            Console.WriteLine(items.Count)
            For Each item As Object In items
                container.Ext().Refresh(item, depth)
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListRefreshedResult

    End Class
End Namespace
