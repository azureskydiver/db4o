Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.query

Namespace com.db4odoc.f1.indexes
    Public Class IndexedExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub NoIndex()
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 1: no indexes")
                Console.WriteLine("Execution time=" + diff.Milliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end NoIndex

        Public Shared Sub FillUpDB()
            File.Delete(YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                For i As Integer = 0 To 10000
                    AddCar(db, i)
                Next
            Finally
                db.Close()
            End Try
        End Sub
        ' end FillUpDB

        Public Shared Sub PilotIndex()
            Db4o.Configure().ObjectClass(GetType(Car)).ObjectField("_pilot").Indexed(True)
            Db4o.Configure().ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(False)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 3: index on pilot")
                Console.WriteLine("Execution time=" + diff.Milliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end PilotIndex

        Public Shared Sub PointsIndex()
            Db4o.Configure().ObjectClass(GetType(Car)).ObjectField("_pilot").Indexed(False)
            Db4o.Configure().ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(True)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 4: index on points")
                Console.WriteLine("Execution time=" + diff.Milliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end PointsIndex


        Public Shared Sub FullIndex()
            Db4o.Configure().ObjectClass(GetType(Car)).ObjectField("_pilot").Indexed(True)
            Db4o.Configure().ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(True)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 2: index on pilot and points")
                Console.WriteLine("Execution time=" + diff.Milliseconds.ToString() + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end FullIndex


        Public Shared Sub AddCar(ByVal db As ObjectContainer, ByVal points As Integer)
            Dim car As Car = New Car("BMW")
            Dim pilot As Pilot = New Pilot("Tester", points)
            car.Pilot = pilot
            db.[Set](car)
        End Sub
        ' end AddCar

        Public Shared Sub ListResult(ByVal result As ObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
	End Class
End Namespace