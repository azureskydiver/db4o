' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Messaging

Namespace Db4objects.Db4odoc.Remote
    Public Class RemoteExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            SetObjects()
            UpdateCars()
            SetObjects()
            UpdateCarsWithMessage()
        End Sub
        ' end Main

        Private Shared Sub SetObjects()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim i As Integer
                For i = 0 To 5 - 1 Step i + 1
                    Dim car As Car = New Car("car" + i.ToString())
                    db.Set(car)
                Next
                db.Set(New RemoteExample())
            Finally
                db.Close()
            End Try
            CheckCars()
        End Sub
        ' end SetObjects


        Private Shared Sub UpdateCars()
            ' triggering mass updates with a singleton
            ' complete server-side execution
            Dim server As IObjectServer = Db4oFactory.OpenServer(Db4oFileName, 0)
            Try
                Dim client As IObjectContainer = server.OpenClient()
                Dim q As IQuery = client.Query()
                q.Constrain(GetType(RemoteExample))
                q.Constrain(New UpdateEvaluation())
                q.Execute()
                client.Close()
            Finally
                server.Close()
            End Try
            CheckCars()
        End Sub
        ' end UpdateCars

        Private Shared Sub CheckCars()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim q As IQuery = db.Query()
                q.Constrain(GetType(Car))
                Dim objectSet As IObjectSet = q.Execute()
                ListResult(objectSet)
            Finally
                db.Close()
            End Try
        End Sub
        ' end CheckCars

        Private Shared Sub UpdateCarsWithMessage()
            ' create message handler on the server
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ClientServer.SetMessageRecipient(New UpdateMessageRecipient())
            Dim server As IObjectServer = Db4oFactory.OpenServer(configuration, Db4oFileName, &HDB40)
            server.GrantAccess("user", "password")
            Try
                Dim clientConfiguration As IConfiguration = Db4oFactory.NewConfiguration()
                Dim sender As IMessageSender = clientConfiguration.ClientServer().GetMessageSender()
                Dim client As IObjectContainer = Db4oFactory.OpenClient(clientConfiguration, "localhost", &HDB40, "user", "password")
                ' send message object to the server
                sender.Send(New UpdateServer())
                client.Close()
            Finally
                server.Close()
            End Try
            CheckCars()
        End Sub
        ' end UpdateCarsWithMessage

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class


    Public Class UpdateEvaluation
        Implements IEvaluation

        Public Sub Evaluate(ByVal candidate As ICandidate) Implements IEvaluation.Evaluate
            ' evaluate method is executed on the server
            ' use it to run update code
            Dim objectContainer As IObjectContainer = candidate.ObjectContainer()
            Dim q2 As IQuery = objectContainer.Query()
            q2.Constrain(GetType(Car))
            Dim objectSet As IObjectSet = q2.Execute()
            While objectSet.HasNext()
                Dim car As Car = CType(objectSet.Next(), Car)
                car.Model = "Update1-" + car.Model
                objectContainer.Set(car)
            End While
            objectContainer.Commit()
        End Sub
    End Class


    Public Class UpdateMessageRecipient
        Implements IMessageRecipient
        Public Sub ProcessMessage(ByVal context As IMessageContext, ByVal message As Object) Implements IMessageRecipient.ProcessMessage
            ' message type defines the code to be executed
            If message.GetType().Equals(GetType(UpdateServer)) Then
                Dim q As IQuery = context.Container.Query()
                q.Constrain(GetType(Car))
                Dim objectSet As IObjectSet = q.Execute()
                While objectSet.HasNext()
                    Dim car As Car = CType(objectSet.Next(), Car)
                    car.Model = "Updated2-" + car.Model
                    context.Container.Set(car)
                End While
                context.Container.Commit()
            End If
        End Sub
    End Class
End Namespace


