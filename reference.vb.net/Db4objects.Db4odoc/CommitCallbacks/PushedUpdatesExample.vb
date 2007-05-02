' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports System.Threading
Imports System.Collections
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Events
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Foundation

Namespace Db4objects.Db4odoc.CommitCallbacks

    Class PushedUpdatesExample
        Private Const Db4oFileName As String = "reference.db4o"
        Private Const Port As Integer = 4440
        Private Const User As String = "db4o"
        Private Const Password As String = "db4o"
        Private Shared clientListeners As Hashtable = New Hashtable

        Public Shared Sub Main(ByVal args As String())
            Call (New PushedUpdatesExample).Run()
        End Sub
        ' end Main

        Public Sub Run()
            File.Delete(Db4oFileName)
            Dim server As IObjectServer = Db4oFactory.OpenServer(Db4oFileName, Port)
            Try
                server.GrantAccess(User, Password)
                Dim client1 As IObjectContainer = OpenClient
                Dim client2 As IObjectContainer = OpenClient
                If Not (client1 Is Nothing) AndAlso Not (client2 Is Nothing) Then
                    Try
                        ' wait for the operations to finish
                        WaitForCompletion()

                        'save pilot with client1
                        Dim client1Car As Car = New Car("Ferrari", 2006, New Pilot("Schumacher"))
                        client1.Set(client1Car)
                        client1.Commit()
                        WaitForCompletion()

                        ' retrieve the same pilot with client2
                        Dim client2Car As Car = CType(client2.Query(GetType(Car)).Next, Car)
                        System.Console.WriteLine(client2Car)

                        ' modify the pilot with pilot1
                        client1Car.Model = 2007
                        client1Car.Pilot = New Pilot("Hakkinnen")
                        client1.Set(client1Car)
                        client1.Commit()
                        WaitForCompletion()

                        ' client2Car has been automatically updated in
                        ' the committed event handler because of the
                        ' modification and the commit by client1
                        System.Console.WriteLine(client2Car)
                        WaitForCompletion()
                    Catch ex As Exception
                        System.Console.WriteLine(ex.ToString)
                    Finally
                        CloseClient(client1)
                        CloseClient(client2)
                    End Try
                End If
            Catch ex As Exception
                System.Console.WriteLine(ex.ToString)
            Finally
                server.Close()
            End Try
        End Sub
        ' end Run

        Private Sub CloseClient(ByVal client As IObjectContainer)
            ' remove listeners before shutting down
            If Not (clientListeners(client) Is Nothing) Then
                Dim eventRegistry As IEventRegistry = EventRegistryFactory.ForObjectContainer(client)
                RemoveHandler eventRegistry.Committed, AddressOf CType(clientListeners(client), CommittedEventHandler).OnCommitted
                clientListeners.Remove(client)
            End If
            client.Close()
        End Sub
        ' end CloseClient

        Private Function OpenClient() As IObjectContainer
            Try
                Dim client As IObjectContainer = Db4oFactory.OpenClient("localhost", Port, User, Password)
                Dim committedEventHandler As CommittedEventHandler = New CommittedEventHandler(client)
                Dim eventRegistry As IEventRegistry = EventRegistryFactory.ForObjectContainer(client)
                AddHandler eventRegistry.Committed, AddressOf committedEventHandler.OnCommitted
                ' save the client-listener pair in a map, so that we can
                ' remove the listener later
                clientListeners.Add(client, committedEventHandler)
                Return client
            Catch ex As Exception
                System.Console.WriteLine(ex.ToString)
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

    End Class
End Namespace