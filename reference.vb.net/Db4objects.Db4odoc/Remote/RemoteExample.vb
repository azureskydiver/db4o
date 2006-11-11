' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Messaging

Namespace Db4objects.Db4odoc.Remote
    Public Class RemoteExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            SetObjects()
            UpdateCars()
            SetObjects()
            UpdateCarsWithMessage()
        End Sub
        ' end Main

        Public Shared Sub SetObjects()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4o.OpenFile(YapFileName)
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


        Public Shared Sub UpdateCars()
            ' triggering mass updates with a singleton
            ' complete server-side execution
            Dim server As IObjectServer = Db4o.OpenServer(YapFileName, 0)
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
            Dim db As IObjectContainer = Db4o.OpenFile(YapFileName)
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

        Public Shared Sub UpdateCarsWithMessage()
            Dim server As IObjectServer = Db4o.OpenServer(YapFileName, 0)
            ' create message handler on the server
            server.Ext().Configure().SetMessageRecipient(New UpdateMessageRecipient())
            Try
                Dim client As IObjectContainer = server.OpenClient()
                ' send message object to the server
                Dim sender As IMessageSender = client.Ext().Configure().GetMessageSender()
                sender.Send(New UpdateServer())
                client.Close()
            Finally
                server.Close()
            End Try
            CheckCars()
        End Sub
        ' end UpdateCarsWithMessage

        Public Shared Sub ListResult(ByVal result As IObjectSet)
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
        Public Sub ProcessMessage(ByVal objectContainer As IObjectContainer, ByVal message As Object) Implements IMessageRecipient.ProcessMessage
            ' message type defines the code to be executed
            If message.GetType().Equals(GetType(UpdateServer)) Then
                Dim q As IQuery = objectContainer.Query()
                q.Constrain(GetType(Car))
                Dim objectSet As IObjectSet = q.Execute()
                While objectSet.HasNext()
                    Dim car As Car = CType(objectSet.Next(), Car)
                    car.Model = "Updated2-" + car.Model
                    objectContainer.Set(car)
                End While
                objectContainer.Commit()
            End If
        End Sub
    End Class
End Namespace


