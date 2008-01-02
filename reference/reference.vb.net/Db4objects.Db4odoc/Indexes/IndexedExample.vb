' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Indexes
    Public Class IndexedExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            FillUpDB()
            NoIndex()
            FullIndex()
            PilotIndex()
            PointsIndex()
        End Sub
        ' end Main

        Private Shared Sub NoIndex()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 1: no indexes")
                Console.WriteLine("Execution time=" + diff.TotalMilliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end NoIndex

        Private Shared Sub FillUpDB()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                For i As Integer = 0 To 10000
                    AddCar(db, i)
                Next
            Finally
                db.Close()
            End Try
        End Sub
        ' end FillUpDB

        Private Shared Sub PilotIndex()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Car)).ObjectField("_pilot").Indexed(True)
            configuration.ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(False)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 3: index on pilot")
                Console.WriteLine("Execution time=" + diff.TotalMilliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end PilotIndex

        Private Shared Sub PointsIndex()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Car)).ObjectField("_pilot").Indexed(False)
            configuration.ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 4: index on points")
                Console.WriteLine("Execution time=" + diff.TotalMilliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end PointsIndex


        Private Shared Sub FullIndex()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Car)).ObjectField("_pilot").Indexed(True)
            configuration.ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 2: index on pilot and points")
                Console.WriteLine("Execution time=" + diff.TotalMilliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end FullIndex


        Private Shared Sub AddCar(ByVal db As IObjectContainer, ByVal points As Integer)
            Dim car As Car = New Car("BMW")
            Dim pilot As Pilot = New Pilot("Tester", points)
            car.Pilot = pilot
            db.Set(car)
        End Sub
        ' end AddCar

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace