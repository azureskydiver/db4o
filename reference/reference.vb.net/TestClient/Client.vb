' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.Collections.Generic

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.NoClasses.Client

    Class Client
        Private Const COUNT = 10

        Public Shared Sub Main(ByVal args As String())
            SavePilots()
            GetPilotsQBE()
            GetPilotsSODA()
            GetPilotsNative()
            GetPilotsNativeUnoptimized()
            GetPilotsEvaluation()
            SaveMultiArray()
            GetMultiArray()
        End Sub
        ' end Main

        Private Shared Sub SavePilots()
            Console.WriteLine("Saving Pilot objects without Pilot class on the server")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim i As Integer = 0
                While i < COUNT
                    oc.Set(New Pilot("Pilot #" + i, i))
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
            Finally
                oc.Close()
            End Try
        End Sub
        ' end SavePilots

        Private Shared Sub GetPilotsQBE()
            Console.WriteLine("Retrieving Pilot objects: QBE")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim result As IObjectSet = oc.Get(New Pilot(Nothing, 0))
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetPilotsQBE

        Private Shared Sub GetPilotsSODA()
            Console.WriteLine("Retrieving Pilot objects: SODA")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim query As IQuery = oc.Query
                query.Constrain(GetType(Pilot))
                query.Descend("_points").Constrain(5)
                Dim result As IObjectSet = query.Execute
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetPilotsSODA

        Private Shared Sub GetPilotsNative()
            Console.WriteLine("Retrieving Pilot objects: Native Query")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim result As IList(Of Pilot) = oc.Query(Of Pilot)(AddressOf Pilot5Points)
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetPilotsNative

        Private Shared Function Pilot5Points(ByVal pilot As Pilot) As Boolean
            Return pilot.Points.Equals(5)
        End Function
        ' end Pilot5Points

        Private Shared Sub GetPilotsNativeUnoptimized()
            Console.WriteLine("Retrieving Pilot objects: Native Query Unoptimized")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim result As IList(Of Pilot) = oc.Query(Of Pilot)(AddressOf Pilot5Points)
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetPilotsNativeUnoptimized

        Private Shared Function PilotEven(ByVal pilot As Pilot) As Boolean
            Return pilot.Points Mod 2 = 0
        End Function
        ' end PilotEven

        Private Shared Sub GetPilotsEvaluation()
            Console.WriteLine("Retrieving Pilot objects: Evaluation")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim query As IQuery = oc.Query
                query.Constrain(GetType(Pilot))
                query.Constrain(New EvenPointsEvaluation)
                Dim result As IObjectSet = query.Execute
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetPilotsEvaluation

        Private Shared Sub SaveMultiArray()
            Console.WriteLine("Testing saving an object with multidimentional array field")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim recordBook As RecordBook = New RecordBook
                recordBook.AddRecord("September 2006", "Michael Schumacher", "last race")
                recordBook.AddRecord("September 2006", "Kimi Raikkonen", "no notes")
                oc.Set(recordBook)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end SaveMultiArray

        Private Shared Sub GetMultiArray()
            Console.WriteLine("Testing retrieving an object with multidimentional array field")
            Dim oc As IObjectContainer = Db4oFactory.OpenClient("localhost", &HDB40, "db4o", "db4o")
            Try
                Dim result As IObjectSet = oc.Get(New RecordBook)
                ListResult(result)
            Finally
                oc.Close()
            End Try
        End Sub
        ' end GetMultiArray

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Size)
            While result.HasNext
                Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

        Public Shared Sub ListResult(ByVal result As IList(Of Pilot))
            Console.WriteLine(result.Count)
            Dim i As Integer
            For i = 0 To result.Count Step i + 1
                Console.WriteLine(result(i))
            Next
        End Sub
        ' end ListResult

        Private Class EvenPointsEvaluation
            Implements IEvaluation

            Public Sub Evaluate(ByVal candidate As ICandidate) Implements IEvaluation.Evaluate
                Dim pilot As Pilot = CType(candidate.GetObject, Pilot)
                candidate.Include(pilot.Points Mod 2 = 0)
            End Sub
        End Class
        ' end EvenPointsEvaluation

    End Class

End Namespace