' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports com.db4o
Imports com.db4o.messaging


Namespace com.db4odoc.f1.messaging

    Public Class MessagingExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub ConfigureServer()
            Dim objectServer As ObjectServer = Db4o.OpenServer(YapFileName, 0)
            objectServer.Ext().Configure().SetMessageRecipient(New SimpleMessageRecipient())
            Try
                Dim clientObjectContainer As ObjectContainer = objectServer.OpenClient()
                ' Here is what we would do on the client to send the message
                Dim sender As MessageSender = clientObjectContainer.Ext().Configure()
                sender.Send(New MyClientServerMessage("Hello from client."))
                clientObjectContainer.Close()
            Finally
                objectServer.Close()
            End Try
        End Sub
        ' end ConfigureServer
    End Class

    Public Class SimpleMessageRecipient
        Implements MessageRecipient
        Public Sub ProcessMessage(ByVal objectContainer As ObjectContainer, ByVal message As Object) Implements MessageRecipient.ProcessMessage
            ' message objects will arrive in this code block
            System.Console.WriteLine(message)
        End Sub
    End Class

End Namespace
