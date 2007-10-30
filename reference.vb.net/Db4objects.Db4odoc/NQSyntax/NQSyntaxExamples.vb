' Copyright (C) 2007 db4objects Inc. http:'www.db4o.com
Imports System
Imports System.Collections.Generic
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.NQSyntax

    Class NQSyntaxExamples

        Private Const Db4oFileName As String = "reference.db4o"

        Private Const ObjectCount As Integer = 10

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StorePilots()
            QuerySyntax1()
            QuerySyntax2()
            QuerySyntax3()
            QuerySyntax4()
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

        Private Shared Sub QuerySyntax1()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(GetType(Pilot))
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end QuerySyntax1

        Private Shared Function PilotTestMatch(ByVal p As Pilot) As Boolean
            ' test pilots 
            Return p.Name.StartsWith("Test")
        End Function
        ' end PilotTestMatch


        Private Shared Sub QuerySyntax2()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf PilotTestMatch)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end QuerySyntax2

        Private Class PilotPredicate
            Inherits Query.Predicate
            Public Function Match(ByVal obj As Object) As Boolean
                ' each Pilot is included in the result
                If TypeOf obj Is Pilot Then
                    Return True
                End If
                Return False
            End Function
        End Class
        ' end PilotPredicate

        Private Shared Sub QuerySyntax3()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IObjectSet = container.Query(New PilotPredicate())
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end QuerySyntax3


        Private Shared Function ComparePilots(ByVal pilot1 As Pilot, ByVal pilot2 As Pilot) As Integer
            Return pilot1.Points - pilot2.Points
        End Function
        ' end ComparePilots

        Private Shared Sub QuerySyntax4()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf PilotTestMatch, AddressOf ComparePilots)
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end QuerySyntax4

        Private Shared Sub ListResult(ByVal result As IList(Of Pilot))
            System.Console.WriteLine(result.Count)
            Dim i As Integer
            For i = 0 To result.Count - 1 Step i + 1
                System.Console.WriteLine(result(i))
            Next
        End Sub
        ' end ListResult

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Count)
            Dim i As Integer
            For i = 0 To result.Count - 1 Step i + 1
                System.Console.WriteLine(result(i).ToString())
            Next
        End Sub
        ' end ListResult

    End Class
End Namespace

