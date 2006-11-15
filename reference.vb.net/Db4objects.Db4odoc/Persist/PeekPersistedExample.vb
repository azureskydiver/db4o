' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports System.Threading
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.Persist
    Public Class PeekPersistedExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            MeasureCarTemperature()
        End Sub
        ' end Main

        Public Shared Sub SetObjects()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim car As Car = New Car("BMW")
                db.Set(car)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetObjects

        Public Shared Sub MeasureCarTemperature()
            SetObjects()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Car))
                If result.Size() > 0 Then
                    Dim car As Car = CType(result(0), Car)
                    Dim car1 As Car = CType(db.Ext().PeekPersisted(car, 5, True), Car)
                    Dim ch1 As Change1 = New Change1()
                    ch1.Init(car1)
                    Dim car2 As Car = CType(db.Ext().PeekPersisted(car, 5, True), Car)
                    Dim ch2 As Change2 = New Change2()
                    ch2.Init(car2)
                    Thread.Sleep(300)
                    ' We can work on the database object at the same time
                    car.Model = "BMW M3Coupe"
                    db.Set(car)
                    ch1.Kill()
                    ch2.Kill()
                    System.Console.WriteLine("car1 saved to the database: " + db.Ext().IsStored(car1).ToString())
                    System.Console.WriteLine("car2 saved to the database: " + db.Ext().IsStored(car1).ToString())
                    Dim temperature As Integer = CType(((car1.Temperature + car2.Temperature) / 2), Integer)
                    car.Temperature = temperature
                    db.Set(car)
                End If
            Finally
                db.Close()
            End Try
            ÑheckCar()
        End Sub
        ' end MeasureCarTemperature

        Public Shared Sub ÑheckCar()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Car))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end ÑheckCar

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace


