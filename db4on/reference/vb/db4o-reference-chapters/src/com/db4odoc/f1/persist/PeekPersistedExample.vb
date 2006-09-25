' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports System.Threading
Imports com.db4o

Namespace com.db4odoc.f1.persist
    Public Class PeekPersistedExample
        Inherits Util
        Public Shared Sub main(ByVal args() As String)
            MeasureCarTemperature()
        End Sub

        Public Shared Sub SetObjects()
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim car As Car = New Car("BMW")
                db.Set(car)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub MeasureCarTemperature()
            SetObjects()
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim result As ObjectSet = db.Get(GetType(Car))
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

        Public Shared Sub ÑheckCar()
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim result As ObjectSet = db.Get(GetType(Car))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
    End Class
End Namespace


