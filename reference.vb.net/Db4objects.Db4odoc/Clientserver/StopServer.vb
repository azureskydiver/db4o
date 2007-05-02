' Copyright (C) 2007 db4objects Inc. http://www.db4o.com
Imports System

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Messaging

Namespace Db4objects.Db4odoc.ClientServer
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
            ' get the messageSender for the IObjectContainer 
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            Dim messageSender As IMessageSender = configuration.ClientServer().GetMessageSender()
            Dim objectContainer As IObjectContainer = Nothing
            Try
                ' connect to the server
                objectContainer = Db4oFactory.OpenClient(configuration, Host, Port, User, Password)
            Catch e As Exception
                Console.WriteLine(e.ToString())
            End Try
            If Not objectContainer Is Nothing Then
                ' send an instance of a StopServer object
                messageSender.Send(New StopServer())
                ' close the IObjectContainer 
                objectContainer.Close()
            End If
        End Sub
        'end Main
    End Class
End Namespace
