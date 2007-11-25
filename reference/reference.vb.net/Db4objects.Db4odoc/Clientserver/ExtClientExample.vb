' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext


Namespace Db4objects.Db4odoc.ClientServer
    Public Class ExtClientExample
        Private Const Db4oFileName As String = "reference.db4o"
        Public Shared ReadOnly ExtFileName As String = "reference_e.db4o"

        Public Shared Sub Main(ByVal args() As String)
            SwitchExtClients()
        End Sub
        ' end Main

        Private Shared Sub SwitchExtClients()
            File.Delete(Db4oFileName)
            File.Delete(ExtFileName)
            Dim server As IObjectServer = Db4oFactory.OpenServer(Db4oFileName, 0)
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

        Private Shared Sub RetrieveAll(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Car))
            ListResult(result)
        End Sub
        ' end RetrieveAll

        Private Shared Sub DeleteAll(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Car))
            Dim item As Object
            For Each item In result
                db.Delete(item)
            Next
        End Sub
        ' end DeleteAll

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
