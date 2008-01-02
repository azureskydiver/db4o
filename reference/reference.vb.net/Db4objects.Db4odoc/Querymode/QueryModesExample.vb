' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Querymode

    Class QueryModesExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            Db4oFactory.Configure.ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(True)
            TestImmediateQueries()
            TestLazyQueries()
            TestSnapshotQueries()
            TestLazyConcurrent()
            TestSnapshotConcurrent()
            TestImmediateChanged()
        End Sub
        ' end Main

        Private Shared Sub FillUpDB(ByVal pilotCount As Integer)
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim i As Integer = 0
                While i < pilotCount
                    AddPilot(db, i)
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
            Finally
                db.Close()
            End Try
        End Sub
        ' end FillUpDB

        Private Shared Sub AddPilot(ByVal db As IObjectContainer, ByVal points As Integer)
            Dim pilot As Pilot = New Pilot("Tester", points)
            db.Set(pilot)
        End Sub
        ' end AddPilot

        Private Shared Sub TestImmediateQueries()
            Console.WriteLine("Testing query performance on 10000 pilot objects in Immediate mode")
            FillUpDB(10000)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Queries.EvaluationMode(QueryEvaluationMode.IMMEDIATE)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query As IQuery = db.Query
                query.Constrain(GetType(Pilot))
                query.Descend("_points").Constrain(99).Greater()
                Dim dt1 As DateTime = DateTime.UtcNow
                query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Query execution time=" + diff.TotalMilliseconds.ToString() + " ms")
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestImmediateQueries

        Private Shared Sub TestLazyQueries()
            Console.WriteLine("Testing query performance on 10000 pilot objects in Lazy mode")
            FillUpDB(10000)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Queries.EvaluationMode(QueryEvaluationMode.LAZY)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query As IQuery = db.Query
                query.Constrain(GetType(Pilot))
                query.Descend("_points").Constrain(99).Greater()
                Dim dt1 As DateTime = DateTime.UtcNow
                query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Query execution time=" + diff.TotalMilliseconds.ToString() + " ms")
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestLazyQueries

        Private Shared Sub TestLazyConcurrent()
            Console.WriteLine("Testing lazy mode with concurrent modifications")
            FillUpDB(10)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Queries.EvaluationMode(QueryEvaluationMode.LAZY)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query1 As IQuery = db.Query
                query1.Constrain(GetType(Pilot))
                query1.Descend("_points").Constrain(5).Smaller()
                Dim result1 As IObjectSet = query1.Execute
                Dim query2 As IQuery = db.Query
                query2.Constrain(GetType(Pilot))
                query2.Descend("_points").Constrain(1)
                Dim result2 As IObjectSet = query2.Execute
                Dim pilotToDelete As Pilot = CType(result2(0), Pilot)
                Console.WriteLine("Pilot to be deleted: " + pilotToDelete.ToString())
                db.Delete(pilotToDelete)
                Dim pilot As Pilot = New Pilot("Tester", 2)
                Console.WriteLine("Pilot to be added: " + pilot.ToString())
                db.Set(pilot)
                Console.WriteLine("Query result after changing from the same transaction")
                ListResult(result1)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestLazyConcurrent

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

        Private Shared Sub TestSnapshotQueries()
            Console.WriteLine("Testing query performance on 10000 pilot objects in Snapshot mode")
            FillUpDB(10000)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Queries.EvaluationMode(QueryEvaluationMode.SNAPSHOT)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query As IQuery = db.Query
                query.Constrain(GetType(Pilot))
                query.Descend("_points").Constrain(99).Greater()
                Dim dt1 As DateTime = DateTime.UtcNow
                query.Execute()
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Query execution time=" + diff.TotalMilliseconds.ToString() + " ms")
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestSnapshotQueries

        Private Shared Sub TestSnapshotConcurrent()
            Console.WriteLine("Testing snapshot mode with concurrent modifications")
            FillUpDB(10)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Queries.EvaluationMode(QueryEvaluationMode.SNAPSHOT)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query1 As IQuery = db.Query
                query1.Constrain(GetType(Pilot))
                query1.Descend("_points").Constrain(5).Smaller()
                Dim result1 As IObjectSet = query1.Execute
                Dim query2 As IQuery = db.Query
                query2.Constrain(GetType(Pilot))
                query2.Descend("_points").Constrain(1)
                Dim result2 As IObjectSet = query2.Execute
                Dim pilotToDelete As Pilot = CType(result2(0), Pilot)
                Console.WriteLine("Pilot to be deleted: " + pilotToDelete.ToString())
                db.Delete(pilotToDelete)
                Dim pilot As Pilot = New Pilot("Tester", 2)
                Console.WriteLine("Pilot to be added: " + pilot.ToString())
                db.Set(pilot)
                Console.WriteLine("Query result after changing from the same transaction")
                ListResult(result1)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestSnapshotConcurrent

        Private Shared Sub TestImmediateChanged()
            Console.WriteLine("Testing immediate mode with field changes")
            FillUpDB(10)
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Queries.EvaluationMode(QueryEvaluationMode.IMMEDIATE)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                Dim query1 As IQuery = db.Query
                query1.Constrain(GetType(Pilot))
                query1.Descend("_points").Constrain(5).Smaller()
                Dim result1 As IObjectSet = query1.Execute
                Dim query2 As IQuery = db.Query
                query2.Constrain(GetType(Pilot))
                query2.Descend("_points").Constrain(2)
                Dim result2 As IObjectSet = query2.Execute
                Dim pilot2 As Pilot = CType(result2(0), Pilot)
                pilot2.AddPoints(22)
                db.Set(pilot2)
                ListResult(result1)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestImmediateChanged

    End Class
End Namespace