Imports System
Imports System.IO
Imports System.Threading
Imports System.Collections

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext


Namespace Db4objects.Db4odoc.Inconsistent
    Class InconsistentGraphExample

        Private Const Db4oFileName As String = "reference.db4o"

        Private Const Port As Integer = 4440

        Private Const User As String = "db4o"

        Private Const Password As String = "db4o"


        Public Shared Sub Main(ByVal args As String())
            Dim example As InconsistentGraphExample = New InconsistentGraphExample()
            example.Run()
        End Sub

        ' end Main

        Public Sub Run()
            File.Delete(Db4oFileName)
            Dim server As IObjectServer = Db4oFactory.OpenServer(Db4oFileName, Port)
            Try
                server.GrantAccess(User, Password)

                Dim client1 As IObjectContainer = OpenClient()
                Dim client2 As IObjectContainer = OpenClient()

                If client1 IsNot Nothing AndAlso client2 IsNot Nothing Then
                    Try
                        ' wait for the operations to finish
                        WaitForCompletion()

                        ' save pilot with client1
                        Dim client1Car As New Car("Ferrari", 2006, New Pilot("Schumacher"))
                        client1.[Set](client1Car)
                        client1.Commit()
                        System.Console.WriteLine("Client1 version initially: " + client1Car.ToString())

                        WaitForCompletion()

                        ' retrieve the same pilot with client2
                        Dim client2Car As Car = DirectCast(client2.Query(GetType(Car)).[Next](), Car)
                        System.Console.WriteLine("Client2 version initially: " + client2Car.ToString())

                        ' delete the pilot with client1
                        Dim client1Pilot As Pilot = DirectCast(client1.Query(GetType(Pilot)).[Next](), Pilot)
                        client1.Delete(client1Pilot)
                        ' modify the car, add and link a new pilot with client1
                        client1Car.Model = 2007
                        client1Car.Pilot = New Pilot("Hakkinnen")
                        client1.[Set](client1Car)
                        client1.Commit()

                        WaitForCompletion()

                        client1Car = DirectCast(client1.Query(GetType(Car)).[Next](), Car)
                        System.Console.WriteLine("Client1 version after update: " + client1Car.ToString())


                        System.Console.WriteLine()
                        System.Console.WriteLine("client2Car still holds the old object graph in its reference cache")
                        client2Car = DirectCast(client2.Query(GetType(Car)).[Next](), Car)
                        System.Console.WriteLine("Client2 version after update: " + client2Car.ToString())
                        Dim result As IObjectSet = client2.Query(GetType(Pilot))
                        System.Console.WriteLine("Though the new Pilot is retrieved by a new query: ")
                        ListResult(result)


                        WaitForCompletion()
                    Catch ex As Exception
                        System.Console.WriteLine(ex.ToString())
                    Finally
                        CloseClient(client1)
                        CloseClient(client2)
                    End Try
                End If
            Catch ex As Exception
                System.Console.WriteLine(ex.ToString())
            Finally
                server.Close()
            End Try
        End Sub

        ' end Run

        Private Sub CloseClient(ByVal client As IObjectContainer)
            client.Close()
        End Sub

        ' end CloseClient

        Private Function OpenClient() As IObjectContainer
            Try
                Dim client As IObjectContainer = Db4oFactory.OpenClient("localhost", Port, User, Password)
                Return client
            Catch ex As Exception
                System.Console.WriteLine(ex.ToString())
            End Try
            Return Nothing
        End Function

        ' end OpenClient


        Private Sub WaitForCompletion()
            Try
                Thread.Sleep(1000)
            Catch e As ThreadInterruptedException
                System.Console.WriteLine(e.Message)
            End Try
        End Sub
        ' end WaitForCompletion

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult

    End Class
End Namespace
