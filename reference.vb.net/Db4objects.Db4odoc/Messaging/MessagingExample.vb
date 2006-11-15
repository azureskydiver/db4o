' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Messaging


Namespace Db4objects.Db4odoc.Messaging

    Public Class MessagingExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub ConfigureServer()
            Dim objectServer As IObjectServer = Db4oFactory.OpenServer(YapFileName, 0)
            objectServer.Ext().Configure().SetMessageRecipient(New SimpleMessageRecipient())
            Try
                Dim clientObjectContainer As IObjectContainer = objectServer.OpenClient()
                ' Here is what we would do on the client to send the message
                Dim sender As IMessageSender = clientObjectContainer.Ext().Configure()
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
        Public Sub ProcessMessage(ByVal objectContainer As IObjectContainer, ByVal message As Object) Implements IMessageRecipient.ProcessMessage
            ' message objects will arrive in this code block
            System.Console.WriteLine(message)
        End Sub
    End Class

End Namespace
