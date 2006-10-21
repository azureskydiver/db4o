' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.query

Namespace com.db4odoc.f1.debugging
    Public Class DebugExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            SetCars()
        End Sub
        ' end Main

        Public Shared Sub SetCars()
            Db4o.Configure().MessageLevel(3)
            File.Delete(YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(YapFileName)
            Try
                Dim car1 As Car = New Car("BMW")
                db.Set(car1)
                Dim car2 As Car = New Car("Ferrari")
                db.Set(car2)
                db.Deactivate(car1, 2)
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                Dim results As ObjectSet = query.Execute()
                ListResult(results)
            Finally
                db.Close()
            End Try
            Db4o.Configure().MessageLevel(0)
        End Sub
        ' end SetCars

        Public Shared Sub ListResult(ByVal result As ObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace

