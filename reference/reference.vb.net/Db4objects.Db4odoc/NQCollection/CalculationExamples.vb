' Copyright (C) 2007 db4objects Inc. http:'www.db4o.com 
Imports System
Imports System.IO
Imports System.Collections.Generic
Imports System.Collections

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.NQCollection

    Class CalculationExamples


        Private Const Db4oFileName As String = "reference.db4o"

        Private Const ObjectCount As Integer = 10

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StorePilots()
            SumPilotPoints()
            SelectMinPointsPilot()
            AveragePilotPoints()
            CountSubGroups()
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

        Private Shared Sub SumPilotPoints()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim sumPredicate As SumPredicate = New SumPredicate()
                    Dim result As IObjectSet = container.Query(sumPredicate)
                    ListResult(result)
                    System.Console.WriteLine("Sum of pilots points: " + sumPredicate.sum.ToString())
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SumPilotPoints

        Private Class SumPredicate
            Inherits Query.Predicate

            Public sum As Integer = 0

            Public Function Match(ByVal p As Pilot) As Boolean
                ' return all pilots
                sum += p.Points
                Return True
            End Function
        End Class
        ' end SumPredicate

        Private Shared Function AllPilotsMatch(Of Pilot)(ByVal p As Pilot) As Boolean
            ' return all pilots
            Return True
        End Function
        ' end AllPilotsMatch

        Private Shared Function PilotPointsCompare(ByVal p1 As Pilot, ByVal p2 As Pilot) As Integer
            ' sort by points then by name
            Return p1.Points - p2.Points
        End Function
        ' end PilotPointsCompare

        Private Shared Sub SelectMinPointsPilot()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf AllPilotsMatch, New System.Comparison(Of Pilot)(AddressOf PilotPointsCompare))
                    If result.Count > 0 Then
                        System.Console.WriteLine("The min points result is: " + result(0).ToString())
                    End If
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectMinPointsPilot

        Private Shared Sub AveragePilotPoints()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim averagePredicate As AveragePredicate = New AveragePredicate()
                    Dim result As IObjectSet = container.Query(averagePredicate)
                    If averagePredicate.count > 0 Then
                        System.Console.WriteLine("Average points for professional pilots: " _
                                        + (averagePredicate.sum / averagePredicate.count).ToString())
                    Else
                        System.Console.WriteLine("No results")
                    End If
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end AveragePilotPoints

        Private Class AveragePredicate
            Inherits Query.Predicate
            Public sum As Integer = 0

            Public count As Integer = 0

            Public Function Match(ByVal p As Pilot) As Boolean
                ' return professional pilots
                If p.Name.StartsWith("Professional") Then
                    sum = sum + p.Points
                    count = count + 1
                    Return True
                End If
                Return False
            End Function
        End Class
        ' end AveragePredicate

        Private Class CountPredicate
            Inherits Query.Predicate
        
            Public countTable As Hashtable = New Hashtable()

            Public Function Match(ByVal p As Pilot) As Boolean
                ' return all Professional and Test pilots and count in
                ' each category
                Dim keywords As String() = {"Professional", "Test"}
                Dim keyword As String
                For Each keyword In keywords
                    If (p.Name.StartsWith(keyword)) Then
                        If countTable.ContainsKey(keyword) Then
                            countTable(keyword) = CType(countTable(keyword), Integer) + 1
                        Else
                            countTable.Add(keyword, 1)
                        End If
                        Return True
                    End If
                Next
                Return False
            End Function
        End Class
        ' end CountPredicate

        Private Shared Sub CountSubGroups()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim predicate As CountPredicate = New CountPredicate()
                    Dim result As IObjectSet = container.Query(predicate)
                    ListResult(result)
                    Dim enumerator As IDictionaryEnumerator = predicate.countTable.GetEnumerator()
                    While enumerator.MoveNext()
                        System.Console.WriteLine(enumerator.Key.ToString() + ": " + enumerator.Value.ToString())
                    End While
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end CountSubGroups

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

    End Class
End Namespace
