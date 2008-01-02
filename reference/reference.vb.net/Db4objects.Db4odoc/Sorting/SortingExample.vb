' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports System.Collections
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Sorting

    Class SortingExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            Db4oFactory.Configure.ObjectClass(GetType(Pilot)).ObjectField("_name").Indexed(True)
            Db4oFactory.Configure.ObjectClass(GetType(Pilot)).ObjectField("_points").Indexed(True)
            SetObjects()
            GetObjectsNQ()
            GetObjectsSODA()
            GetObjectsEval()
        End Sub
        ' end Main

        Private Shared Sub SetObjects()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim i As Integer = 0
                While i < 10
                    Dim j As Integer = 0
                    While j < 5
                        Dim pilot As Pilot = New Pilot("Pilot #" + i.ToString(), j + 1)
                        db.Set(pilot)
                        System.Math.Min(System.Threading.Interlocked.Increment(j), j - 1)
                    End While
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetObjects

        Private Class PilotComparer
            Implements IComparer
            Public Function Compare(ByVal p1 As Object, ByVal p2 As Object) As Integer Implements IComparer.Compare
                If TypeOf p1 Is Pilot AndAlso TypeOf p2 Is Pilot Then
                    Dim pilot1 As Pilot = CType(p1, Pilot)
                    Dim pilot2 As Pilot = CType(p2, Pilot)
                    Return pilot1.Name.CompareTo(pilot2.Name)
                End If
                Return 0
            End Function
        End Class
        ' end PilotComparer

        Private Class PilotEvaluation
            Implements IEvaluation

            Public Sub Evaluate(ByVal candidate As ICandidate) Implements IEvaluation.Evaluate
                Dim pilot As Pilot = CType(candidate.GetObject, Pilot)
                candidate.Include(pilot.Points Mod 2 = 0)
            End Sub
        End Class
        ' end PilotEvaluation

        Private Shared Sub GetObjectsEval()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim query As IQuery = db.Query
                query.Constrain(GetType(Pilot))
                query.Constrain(New PilotEvaluation)
                Dim result As ArrayList = New ArrayList(query.Execute)
                result.Sort(New PilotComparer)
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Time to execute with Evaluation query and collection sorting: " + diff.TotalMilliseconds.ToString() + " ms.")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjectsEval

        Private Shared Sub GetObjectsSODA()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim query As IQuery = db.Query
                query.Constrain(GetType(Pilot))
                query.Descend("_name").OrderAscending()
                query.Descend("_points").OrderDescending()
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = query.Execute
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Time to query and sort with SODA: " + diff.TotalMilliseconds.ToString() + " ms.")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjectsSODA

        Private Class PilotQueryComparer
            Implements IQueryComparator

            Public Function Compare(ByVal p1 As Object, ByVal p2 As Object) As Integer Implements IQueryComparator.Compare
                If TypeOf p1 Is Pilot AndAlso TypeOf p2 Is Pilot Then
                    Dim pilot1 As Pilot = CType(p1, Pilot)
                    Dim pilot2 As Pilot = CType(p2, Pilot)
                    Dim result As Integer = pilot1.Points - pilot2.Points
                    If result = 0 Then
                        Return pilot1.Name.CompareTo(pilot2.Name)
                    Else
                        Return -result
                    End If
                End If
                Return 0
            End Function
        End Class
        ' end PilotQueryComparer

        Private Class AllPilots
            Inherits Predicate
            Public Function Match(ByVal pilot As Pilot) As Boolean
                Return True
            End Function
        End Class
        ' end AllPilots

        Private Shared Sub GetObjectsNQ()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim dt1 As DateTime = DateTime.UtcNow
                Dim result As IObjectSet = db.Query(New AllPilots, New PilotQueryComparer)
                Dim dt2 As DateTime = DateTime.UtcNow
                Dim diff As TimeSpan = dt2 - dt1
                Console.WriteLine("Time to execute with NQ and comparator: " + diff.TotalMilliseconds.ToString() + " ms.")
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end GetObjectsNQ

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

        Private Shared Sub ListResult(ByVal result As ArrayList)
            Console.WriteLine(result.Count)
            Dim i As Integer = 0
            While i < result.Count
                Console.WriteLine(result(i))
                System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
            End While
        End Sub
        ' end ListResult

    End Class
End Namespace