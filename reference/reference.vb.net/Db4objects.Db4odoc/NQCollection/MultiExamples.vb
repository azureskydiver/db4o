' Copyright (C) 2007 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO
Imports System.Collections.Generic

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.NQCollection

    Class MultiExamples

        Private Const Db4oFileName As String = "reference.db4o"

        Private Const ObjectCount As Integer = 10

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StorePilotsAndTrainees()
            SelectPilotsAndTrainees()
            StoreCars()
            SelectPilotsInRange()
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

        Private Shared Sub StorePilotsAndTrainees()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim pilot As Pilot
                    Dim trainee As Trainee
                    Dim i As Integer
                    For i = 0 To ObjectCount - 1 Step i + 1
                        pilot = New Pilot("Professional Pilot #" + i.ToString(), i)
                        trainee = New Trainee("Trainee #" + i.ToString(), pilot)
                        container.Set(trainee)
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
        ' end StorePilotsAndTrainees

        Private Shared Sub StoreCars()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim car As Car
                    Dim i As Integer
                    For i = 0 To ObjectCount - 1 Step i + 1
                        car = New Car("BMW", New Pilot("Test Pilot #" + i.ToString(), i))
                        container.Set(car)
                    Next
                    For i = 0 To ObjectCount - 1 Step i + 1
                        car = New Car("Ferrari", New Pilot("Professional Pilot #" + (i + 10).ToString(), (i + 10)))
                        container.Set(car)
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
        ' end StoreCars

        Private Shared Function AllPersonMatch(Of Person)(ByVal p As Person) As Boolean
            ' all persons
            Return True
        End Function
        ' end AllPersonMatch

        Private Shared Sub SelectPilotsAndTrainees()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Person) = container.Query(Of Person)(AddressOf AllPersonMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectPilotsAndTrainees

        Private Class CarPilotPredicate
            Inherits Query.Predicate

            Private pilots As IList(Of Pilot) = Nothing

            Private Shared Function TestPilotsMatch(ByVal p As Pilot) As Boolean
                Return p.Name.StartsWith("Test")
            End Function
            ' end TestPilotsMatch

            Private Function GetPilotsList() As IList(Of Pilot)
                If pilots Is Nothing Then
                    pilots = Database().Query(Of Pilot)(AddressOf TestPilotsMatch)
                End If
                Return pilots
            End Function

            Public Function Match(ByVal car As Car) As Boolean

                ' all Cars that have pilot field in the
                ' Pilots array
                Return GetPilotsList().Contains(car.Pilot)
            End Function
        End Class
        ' end CarPilotPredicate

        Private Shared Sub SelectPilotsInRange()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IObjectSet = container.Query(New CarPilotPredicate())
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectPilotsInRange

        Private Shared Sub ListResult(ByVal result As IList(Of Person))
            System.Console.WriteLine(result.Count)
            Dim obj As Person
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
