Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.query

Namespace com.db4o.f1.chapter21
    Public Class IndexedExample
        Inherits Util



        Public Shared Sub noIndex()
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1;
                Console.WriteLine("Test 1: no indexes")
                Console.WriteLine("Execution time=" + diff.Milliseconds + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub fillUpDB()
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                For i As Integer = 0 To 10000
     				AddCar(db,i);
                Next
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub pilotIndex()
            Db4o.Configure().ObjectClass(GetType(Car).ObjectField("_pilot").Indexed(true)
            Db4o.Configure().ObjectClass(GetType(Pilot).ObjectField("_points").Indexed(false)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 3: index on pilot")
                Console.WriteLine("Execution time=" + diff.Milliseconds + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub

        Public Shared Sub pointsIndex()
            Db4o.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(false)
            Db4o.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 4: index on points")
                Console.WriteLine("Execution time=" + diff.Milliseconds + " ms")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub


        Public Shared Sub fullIndex()
            Db4o.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(true)
            Db4o.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Car))
                query.Descend("_pilot").Descend("_points").Constrain("99")
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As ObjectSet = query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Test 2: index on pilot and points")
                Console.WriteLine("Execution time=" + diff.Milliseconds + " ms")
                ListResult(result);
            Finally
                db.Close()
            End Try
        End Sub


        Public Shared Sub AddCar(ByVal db As ObjectContainer, ByVal points As Integer)
            Dim car As Car = New Car("BMW")
            Dim pilot As Pilot = New Pilot("Tester", points)
            car.Pilot = pilot
            db.[Set](car)
        End Sub


	}
    End Class