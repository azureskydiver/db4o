' Copyright (C) 2007 db4objects Inc. http:'www.db4o.com

Imports System
Imports System.IO
Imports System.Collections.Generic
Imports System.Collections

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.NQCollection
    Class SimpleExamples

        Private Const Db4oFileName As String = "reference.db4o"

        Private Const ObjectCount As Integer = 10

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StorePilots()
            SelectAllPilots()
            SelectAllPilotsNonGeneric()
            SelectPilot5Points()
            SelectTestPilots()
            SelectPilotsNumberX6()
            SelectTestPilots6PointsMore()
            SelectPilots6To12Points()
            SelectPilotsRandom()
            SelectPilotsEven()
            SelectAnyOnePilot()
            GetSortedPilots()
            GetPilotsSortByNameAndPoints()
            SelectAndChangePilots()
            StoreDuplicates()
            SelectDistinctPilots()
        End Sub
        ' end Main

        Private Shared Function Database() As IObjectContainer
            If _container Is Nothing Then
                Try
                    _container = Db4oFactory.OpenFile(Db4oFileName)
                Catch ex As DatabaseFileLockedException
                    System.Console.WriteLine(ex.Message)
                End Try
            End If
            Return _container
        End Function
        ' end Database

        Private Shared Sub CloseDatabase()
            If Not _container Is Nothing Then
                _container.Close()
                _container = Nothing
            End If
        End Sub
        ' end CloseDatabase

        Private Shared Sub StorePilots()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim pilot As Pilot
                    Dim i As Integer
                    For i = 0 To ObjectCount - 1 Step i + 1
                        pilot = New Pilot("Test Pilot #" + i.ToString(), i)
                        container.Set(pilot)
                    Next
                    i = 0
                    For i = 0 To ObjectCount - 1 Step i + 1
                        pilot = New Pilot("Professional Pilot #" + (i + 10).ToString(), i + 10)
                        container.Set(pilot)
                    Next
                    container.Commit()
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StorePilots

        Private Shared Sub StoreDuplicates()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim pilot As Pilot
                    Dim i As Integer
                    For i = 0 To ObjectCount - 1 Step i + 1
                        pilot = New Pilot("Test Pilot #" + i.ToString(), i)
                        container.Set(pilot)
                    Next
                    i = 0
                    For i = 0 To ObjectCount - 1 Step i + 1
                        pilot = New Pilot("Test Pilot #" + i.ToString(), i)
                        container.Set(pilot)
                    Next
                    container.Commit()
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StoreDuplicates

        Private Shared Function AllPilotsMatch(ByVal p As Pilot) As Boolean
            ' each Pilot is included in the result
            Return True
        End Function
        ' end AllPilotsMatch

        Private Shared Sub SelectAllPilots()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf AllPilotsMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectAllPilots

        Private Shared Function Pilot5PointsMatch(ByVal p As Pilot) As Boolean
            ' pilots with 5 points are included in the
            ' result
            Return p.Points = 5
        End Function
        ' end Pilot5PointsMatch

        Private Shared Sub SelectPilot5Points()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf Pilot5PointsMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end selectPilot5Points

        Private Class NonGenericPredicate
            Inherits Query.Predicate
            Public Function Match(ByVal obj As Object) As Boolean
                ' each Pilot is included in the result
                If TypeOf obj Is Pilot Then
                    Return True
                End If
                Return False
            End Function
        End Class
        ' end NonGenericPredicate 

        Private Shared Sub SelectAllPilotsNonGeneric()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IObjectSet = container.Query(New NonGenericPredicate())
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectAllPilotsNonGeneric

        Private Shared Function TestPilotsMatch(ByVal p As Pilot) As Boolean
            ' all Pilots containing "Test" in the name
            ' are included in the result
            Return p.Name.IndexOf("Test") >= 0
        End Function
        ' end TestPilotsMatch

        Private Shared Sub SelectTestPilots()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf TestPilotsMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectTestPilots

        Private Shared Function PilotsX6Match(ByVal p As Pilot) As Boolean
            ' all Pilots with the name ending with 6 will
            ' be included
            Return p.Name.EndsWith("6")
        End Function
        ' end PilotsX6Match

        Private Shared Sub SelectPilotsNumberX6()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf PilotsX6Match)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectPilotsNumberX6

        Private Shared Function TestPilots6PointsMoreMatch(ByVal p As Pilot) As Boolean
            ' all Pilots containing "Test" in the name
            ' and 6 point are included in the result
            Dim b1 As Boolean = p.Name.IndexOf("Test") >= 0
            Dim b2 As Boolean = p.Points > 6
            Return b1 AndAlso b2
        End Function
        ' end TestPilots6PointsMoreMatch

        Private Shared Sub SelectTestPilots6PointsMore()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf TestPilots6PointsMoreMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectTestPilots6PointsMore

        Private Shared Function Pilots6To12PointsMatch(ByVal p As Pilot) As Boolean
            ' all Pilots having 6 to 12 point are
            ' included in the result
            Return ((p.Points >= 6) AndAlso (p.Points <= 12))
        End Function
        ' end Pilots6To12PointsMatch

        Private Shared Sub SelectPilots6To12Points()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf Pilots6To12PointsMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectPilots6To12Points

        Private Class RandomPredicate
            Inherits Query.Predicate
            Private randomArray As IList = Nothing

            Private Function GetRandomArray() As IList
                If randomArray Is Nothing Then
                    Dim rand As Random = New Random()
                    randomArray = New ArrayList()
                    Dim i As Integer
                    For i = 0 To 9 Step i + 1
                        Dim random As Integer = CType(rand.Next(10), Integer)
                        randomArray.Add(random)
                    Next
                End If
                Return randomArray
            End Function

            Public Function Match(ByVal p As Pilot) As Boolean
                ' all Pilots having points in the values of
                ' the randomArray
                Return GetRandomArray().Contains(p.Points)
            End Function
        End Class
        ' end RandomPredicate

        Private Shared Sub SelectPilotsRandom()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IObjectSet = container.Query(New RandomPredicate())
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectPilotsRandom

        Private Shared Function PilotsEvenMatch(ByVal p As Pilot) As Boolean
            ' all Pilots having even points
            Return p.Points Mod 2 = 0
        End Function
        ' end PilotsEvenMatch

        Private Shared Sub SelectPilotsEven()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf PilotsEvenMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectPilotsEven

        Private Class AnyPilotPredicate
            Inherits Query.Predicate
            Dim selected As Boolean = False

            Public Function Match(ByVal p As Pilot) As Boolean
                ' return only first result (first result can
                ' be any value from the resultset)
                If Not selected Then
                    selected = True
                    Return selected
                Else
                    Return selected = Not selected
                End If
            End Function
        End Class
        ' end AnyPilotPredicate

        Private Shared Sub SelectAnyOnePilot()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IObjectSet = container.Query(New AnyPilotPredicate())
                    SimpleExamples.ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectAnyOnePilot

        Private Shared Function PilotPointsCompare(ByVal p1 As Pilot, ByVal p2 As Pilot) As Integer
            Return p2.Points - p1.Points
        End Function
        ' end PilotPointsCompare

        Private Shared Sub GetSortedPilots()
            Dim container As IObjectContainer = Database()
            Try
                Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf AllPilotsMatch, AddressOf PilotPointsCompare)
                ListResult(result)
            Catch ex As Exception
                System.Console.WriteLine("System Exception: " + ex.Message)
            Finally
                CloseDatabase()
            End Try
        End Sub
        ' end GetSortedPilots

        Private Shared Function PilotPointsAndNameCompare(ByVal p1 As Pilot, ByVal p2 As Pilot) As Integer
            ' sort by name then by points: descending
            Dim compareResult As Integer = p1.Name.CompareTo(p2.Name)
            If (compareResult = 0) Then
                Return p1.Points - p2.Points
            Else
                Return -compareResult
            End If
        End Function
        ' end PilotPointsAndNameCompare

        Public Shared Sub GetPilotsSortByNameAndPoints()
            Dim container As IObjectContainer = Database()
            Try
                Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf AllPilotsMatch, AddressOf PilotPointsAndNameCompare)
                ListResult(result)
            Catch ex As Exception
                System.Console.WriteLine("System Exception: " + ex.Message)
            Finally
                CloseDatabase()
            End Try
        End Sub
        ' end GetPilotsSortByNameAndPoints

        Public Shared Sub GetPilotsSortWithComparator()
            Dim container As IObjectContainer = Database()
            Try
                Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf AllPilotsMatch, New PilotComparator())
                ListResult(result)
            Catch ex As Exception
                System.Console.WriteLine("System Exception: " + ex.Message)
            Finally
                CloseDatabase()
            End Try
        End Sub
        ' end GetPilotsSortWithComparator

        Private Class PilotComparator
            Implements IComparer(Of Pilot)

            Public Function Compare(ByVal p1 As Pilot, ByVal p2 As Pilot) As Integer Implements System.Collections.Generic.IComparer(Of Pilot).Compare
                Dim result As Integer = p1.Name.CompareTo(p2.Name)
                If result = 0 Then
                    Return p1.Points - p2.Points
                Else
                    Return -result
                End If
            End Function
        End Class
        ' end PilotComparator

        Private Class DistinctPilotsPredicate
            Inherits Query.Predicate

            Public uniqueResult As Dictionary(Of Pilot, Object) = New Dictionary(Of Pilot, Object)()

            Public Function Match(ByVal p As Pilot) As Boolean
                ' each Pilot is included in the result
                uniqueResult.Add(p, Nothing)
                Return False
            End Function
        End Class
        ' end DistinctPilotsPredicate

        Private Shared Sub SelectDistinctPilots()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim predicate As DistinctPilotsPredicate = New DistinctPilotsPredicate()
                    Dim result As IObjectSet = container.Query(predicate)
                    ListResult(predicate.uniqueResult)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectDistinctPilots

        Private Shared Function RankPilotsAndMatch(ByVal p As Pilot) As Boolean
            ' Add ranking to the pilots during the query.
            ' Note: pilot records in the database won't
            ' be changed!!!
            If p.Points <= 5 Then
                p.Name = p.Name + ": weak"
            ElseIf p.Points > 5 AndAlso p.Points <= 15 Then
                p.Name = p.Name + ": average"
            ElseIf p.Points > 15 Then
                p.Name = p.Name + ": strong"
            End If
            Return True
        End Function
        ' end RankPilotsAndMatch

        Private Shared Sub SelectAndChangePilots()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf RankPilotsAndMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectAndChangePilots

        Private Shared Sub ListResult(ByVal result As IList(Of Pilot))
            System.Console.WriteLine(result.Count)
            Dim obj As Pilot
            For Each obj In result
                System.Console.WriteLine(obj)
            Next
        End Sub
        ' end ListResult

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Count)
            Dim obj As Object
            For Each obj In result
                System.Console.WriteLine(obj)
            Next
        End Sub
        ' end ListResult

        Private Shared Sub ListResult(ByVal result As Dictionary(Of Pilot, Object))
            System.Console.WriteLine(result.Count)
            For Each kvp As KeyValuePair(Of Pilot, Object) In result
                Console.WriteLine(kvp.Key)
            Next
        End Sub
        ' end ListResult

    End Class
End Namespace
