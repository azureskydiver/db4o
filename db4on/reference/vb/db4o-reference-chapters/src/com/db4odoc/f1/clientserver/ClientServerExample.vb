Imports System.IO
Imports com.db4o


Namespace com.db4odoc.f1.clientserver
    Public Class ClientServerExample
        Inherits Util
        Public Shared Sub Main(ByVal args As String())
            File.Delete(Util.YapFileName)
            AccessLocalServer()
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                SetFirstCar(db)
                SetSecondCar(db)
            Finally
                db.Close()
            End Try
            ConfigureDb4o()
            Dim server As ObjectServer = Db4oFactory.OpenServer(Util.YapFileName, 0)
            Try
                QueryLocalServer(server)
                DemonstrateLocalReadCommitted(server)
                DemonstrateLocalRollback(server)
            Finally
                server.Close()
            End Try
            AccessRemoteServer()
            server = Db4oFactory.OpenServer(Util.YapFileName, ServerPort)
            server.GrantAccess(ServerUser, ServerPassword)
            Try
                QueryRemoteServer(ServerPort, ServerUser, ServerPassword)
                DemonstrateRemoteReadCommitted(ServerPort, ServerUser, ServerPassword)
                DemonstrateRemoteRollback(ServerPort, ServerUser, ServerPassword)
            Finally
                server.Close()
            End Try
        End Sub

        Public Shared Sub SetFirstCar(ByVal db As ObjectContainer)
            Dim pilot As Pilot = New Pilot("Rubens Barrichello", 99)
            Dim car As Car = New Car("BMW")
            car.Pilot = pilot
            db.[Set](car)
        End Sub

        Public Shared Sub SetSecondCar(ByVal db As ObjectContainer)
            Dim pilot As Pilot = New Pilot("Michael Schumacher", 100)
            Dim car As Car = New Car("Ferrari")
            car.Pilot = pilot
            db.[Set](car)
        End Sub

        Public Shared Sub AccessLocalServer()
            Dim server As ObjectServer = Db4oFactory.OpenServer(Util.YapFileName, 0)
            Try
                Dim client As ObjectContainer = server.OpenClient()
                ' Do something with this client, or open more clients
                client.Close()
            Finally
                server.Close()
            End Try
        End Sub

        Public Shared Sub QueryLocalServer(ByVal server As ObjectServer)
            Dim client As ObjectContainer = server.OpenClient()
            ListResult(client.[Get](New Car(Nothing)))
            client.Close()
        End Sub

        Public Shared Sub ConfigureDb4o()
            Db4oFactory.Configure().ObjectClass(GetType(Car)).UpdateDepth(3)
        End Sub

        Public Shared Sub DemonstrateLocalReadCommitted(ByVal server As ObjectServer)
            Dim client1 As ObjectContainer = server.OpenClient()
            Dim client2 As ObjectContainer = server.OpenClient()
            Dim pilot As Pilot = New Pilot("David Coulthard", 98)
            Dim result As ObjectSet = client1.[Get](New Car("BMW"))
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

        Public Shared Sub DemonstrateLocalRollback(ByVal server As ObjectServer)
            Dim client1 As ObjectContainer = server.OpenClient()
            Dim client2 As ObjectContainer = server.OpenClient()
            Dim result As ObjectSet = client1.[Get](New Car("BMW"))
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

        Public Shared Sub AccessRemoteServer()
            Dim server As ObjectServer = Db4oFactory.OpenServer(Util.YapFileName, ServerPort)
            server.GrantAccess(ServerUser, ServerPassword)
            Try
                Dim client As ObjectContainer = Db4oFactory.OpenClient("localhost", ServerPort, ServerUser, ServerPassword)
                ' Do something with this client, or open more clients
                client.Close()
            Finally
                server.Close()
            End Try
        End Sub

        Public Shared Sub QueryRemoteServer(ByVal port As Integer, ByVal user As String, ByVal password As String)
            Dim client As ObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            ListResult(client.[Get](New Car(Nothing)))
            client.Close()
        End Sub

        Public Shared Sub DemonstrateRemoteReadCommitted(ByVal port As Integer, ByVal user As String, ByVal password As String)
            Dim client1 As ObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim client2 As ObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim pilot As Pilot = New Pilot("Jenson Button", 97)
            Dim result As ObjectSet = client1.[Get](New Car(Nothing))
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

        Public Shared Sub DemonstrateRemoteRollback(ByVal port As Integer, ByVal user As String, ByVal password As String)
            Dim client1 As ObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim client2 As ObjectContainer = Db4oFactory.OpenClient("localhost", port, user, password)
            Dim result As ObjectSet = client1.[Get](New Car(Nothing))
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

    End Class
End Namespace
