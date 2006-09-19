' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.ext


Namespace com.db4odoc.f1.clientserver
    Public Class ExtClientExample
        Inherits Util
        Public Shared ReadOnly ExtFileName As String = "formula1e.yap"

        Public Shared Sub main(ByVal args() As String)
            SwitchExtClients()
        End Sub

        Public Shared Sub SwitchExtClients()
            File.Delete(Util.YapFileName)
            File.Delete(ExtFileName)
            Dim server As ObjectServer = Db4o.OpenServer(Util.YapFileName, 0)
            Try
                Dim client As ObjectContainer = server.OpenClient()
                Dim car As Car = New Car("BMW")
                client.Set(car)
                System.Console.WriteLine("Objects in the main database file:")
                RetrieveAll(client)

                System.Console.WriteLine("Switching to additional database:")
                Dim clientExt As ExtClient = CType(client, ExtClient)
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

        Public Shared Sub RetrieveAll(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.Get(GetType(Car))
            ListResult(result)
        End Sub

        Public Shared Sub DeleteAll(ByVal db As ObjectContainer)
            Dim result As ObjectSet = db.Get(GetType(Car))
            Dim item As Object
            For Each item In result
                db.Delete(item)
            Next
        End Sub
    End Class
End Namespace
