Imports System
Imports com.db4o
Imports com.db4o.messaging
Namespace com.db4odoc.f1.clientserver
    ''' <summary>
    ''' stops the db4o Server started with StartServer.
    ''' This is done by opening a client connection
    ''' to the server and by sending a StopServer object as
    ''' a message. StartServer will react in it's
    ''' processMessage method.
    ''' </summary>
    Public Class StopServer
        Inherits ServerConfiguration
        ''' <summary>
        ''' stops a db4o Server started with StartServer.
        ''' </summary>
        ''' <exception cref="Exception" />
        Public Shared Sub Main(ByVal args As String())
            Dim objectContainer As ObjectContainer = Nothing
            Try
                ' connect to the server
                objectContainer = Db4oFactory.OpenClient(HOST, PORT, USER, PASS)
            Catch e As Exception
                Console.WriteLine(e.ToString())
            End Try
            If Not objectContainer Is Nothing Then
                ' get the messageSender for the ObjectContainer 
                Dim messageSender As MessageSender = objectContainer.Ext().Configure().GetMessageSender()
                ' send an instance of a StopServer object
                messageSender.Send(New StopServer())
                ' close the ObjectContainer 
                objectContainer.Close()
            End If
        End Sub
        'end Main
    End Class
End Namespace
