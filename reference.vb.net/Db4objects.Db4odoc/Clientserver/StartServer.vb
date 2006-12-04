Imports System
Imports System.Threading
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Messaging

Namespace Db4objects.Db4odoc.ClientServer
    ''' <summary>
    ''' starts a db4o server with the settings from ServerConfiguration. 
    ''' This is a typical setup for a long running server.
    ''' The Server may be stopped from a remote location by running
    ''' StopServer. The StartServer instance is used as a MessageRecipient 
    ''' and reacts to receiving an instance of a StopServer object.
    ''' Note that all user classes need to be present on the server side
    ''' and that all possible Db4oFactory.Configure() calls to alter the db4o
    ''' configuration need to be executed on the client and on the server.
    ''' </summary>
    Public Class StartServer
        Inherits ServerConfiguration
        Implements IMessageRecipient
        ''' <summary>
        ''' setting the value to true denotes that the server should be closed
        ''' </summary>
        Private [stop] As Boolean = False

        ''' <summary>
        ''' starts a db4o server using the configuration from
        ''' ServerConfiguration.
        ''' </summary>
        Public Shared Sub Main(ByVal arguments As String())
            Dim server As New StartServer
            server.RunServer()
        End Sub
        ' end Main

        ''' <summary>
        ''' opens the IObjectServer, and waits forever until Close() is called
        ''' or a StopServer message is being received.
        ''' </summary>
        Public Sub RunServer()
            SyncLock Me
                Dim db4oServer As IObjectServer = Db4oFactory.OpenServer(FILE, PORT)
                db4oServer.GrantAccess(User, PASS)
                ' Using the messaging functionality to redirect all
                ' messages to this.processMessage
                db4oServer.Ext().Configure().SetMessageRecipient(Me)
                Try
                    If Not [stop] Then
                        ' wait forever until Close will change stop variable
                        Monitor.Wait(Me)
                    End If
                Catch e As Exception
                    Console.WriteLine(e.ToString())
                End Try
                db4oServer.Close()
            End SyncLock
        End Sub
        ' end RunServer

        ''' <summary>
        ''' messaging callback
        ''' see com.db4o.messaging.MessageRecipient#ProcessMessage()
        ''' </summary>
        Public Sub ProcessMessage(ByVal con As IObjectContainer, ByVal message As Object) Implements IMessageRecipient.ProcessMessage
            If TypeOf message Is StopServer Then
                Close()
            End If
        End Sub
        ' end ProcessMessage

        ''' <summary>
        ''' closes this server.
        ''' </summary>
        Public Sub Close()
            SyncLock Me
                [stop] = True
                Monitor.PulseAll(Me)
            End SyncLock
        End Sub
        ' end Close
    End Class
End Namespace
