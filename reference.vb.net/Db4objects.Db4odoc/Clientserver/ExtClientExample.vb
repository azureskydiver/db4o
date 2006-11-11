' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext


Namespace Db4objects.Db4odoc.ClientServer
    Public Class ExtClientExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"
        Public Shared ReadOnly ExtFileName As String = "formula1e.yap"

        Public Shared Sub Main(ByVal args() As String)
            SwitchExtClients()
        End Sub
        ' end Main

        Public Shared Sub SwitchExtClients()
            File.Delete(YapFileName)
            File.Delete(ExtFileName)
            Dim server As IObjectServer = Db4o.OpenServer(YapFileName, 0)
            Try
                Dim client As IObjectContainer = server.OpenClient()
                Dim car As Car = New Car("BMW")
                client.Set(car)
                System.Console.WriteLine("Objects in the Main database file:")
                RetrieveAll(client)

                System.Console.WriteLine("Switching to additional database:")
                Dim clientExt As IExtClient = CType(client, IExtClient)
                clientExt.SwitchToFile(ExtFileName)
                car = New Car("Ferrari")
                clientExt.Set(car)
                RetrieveAll(clientExt)
                System.Console.WriteLine("Main database file again: ")
                clientExt.SwitchToMainFile()
                RetrieveAll(clientExt)
                clientExt.Close()
            Finally
                server.Close()
            End Try
        End Sub
        ' end SwitchExtClients

        Public Shared Sub RetrieveAll(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Car))
            ListResult(result)
        End Sub
        ' end RetrieveAll

        Public Shared Sub DeleteAll(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Car))
            Dim item As Object
            For Each item In result
                db.Delete(item)
            Next
        End Sub
        ' end DeleteAll

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
