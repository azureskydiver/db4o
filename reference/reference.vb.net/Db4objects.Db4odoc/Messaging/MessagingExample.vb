' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Messaging


Namespace Db4objects.Db4odoc.Messaging

    Public Class MessagingExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub ConfigureServer()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ClientServer.SetMessageRecipient(New SimpleMessageRecipient())
            Dim objectServer As IObjectServer = Db4oFactory.OpenServer(configuration, Db4oFileName, 0)
            Try
                ' Here is what we would do on the client to send the message
                Dim clientConfiguration As IConfiguration = Db4oFactory.NewConfiguration()
                Dim sender As IMessageSender = clientConfiguration.ClientServer().GetMessageSender()
                Dim clientObjectContainer As IObjectContainer = objectServer.OpenClient(clientConfiguration)
                sender.Send(New MyClientServerMessage("Hello from client."))
                clientObjectContainer.Close()
            Finally
                objectServer.Close()
            End Try
        End Sub
        ' end ConfigureServer
    End Class

    Public Class SimpleMessageRecipient
        Implements IMessageRecipient
        Public Sub ProcessMessage(ByVal context As IMessageContext, ByVal message As Object) Implements IMessageRecipient.ProcessMessage
            ' message objects will arrive in this code block
            System.Console.WriteLine(message)
        End Sub
    End Class

End Namespace
