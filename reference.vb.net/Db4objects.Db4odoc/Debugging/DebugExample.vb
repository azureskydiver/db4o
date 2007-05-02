' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Debugging
    Public Class DebugExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            SetCars()
            SetCarsWithFileOutput()
        End Sub
        ' end Main

        Private Shared Sub SetCars()
            ' Set the debug message levet to the maximum
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.MessageLevel(3)
            ' Do some db4o operations
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim car1 As Car = New Car("BMW")
                db.Set(car1)
                Dim car2 As Car = New Car("Ferrari")
                db.Set(car2)
                db.Deactivate(car1, 2)
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                Dim results As IObjectSet = query.Execute()
                ListResult(results)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetCars

        Private Shared Sub SetCarsWithFileOutput()
            ' Create StreamWriter for a file
            Dim f As FileInfo = New FileInfo("Debug.txt")
            Dim debugWriter As StreamWriter = f.CreateText()

            ' Redirect debug output to the specified writer
            Db4oFactory.Configure().SetOut(debugWriter)

            ' Set the debug message levet to the maximum
            Db4oFactory.Configure().MessageLevel(3)
            ' Do some db4o operations
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim car1 As Car = New Car("BMW")
                db.Set(car1)
                Dim car2 As Car = New Car("Ferrari")
                db.Set(car2)
                db.Deactivate(car1, 2)
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                Dim results As IObjectSet = query.Execute()
                ListResult(results)
            Finally
                db.Close()
                debugWriter.Close()
            End Try
            Db4oFactory.Configure().MessageLevel(0)
        End Sub
        ' end SetCarsWithFileOutput

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace

