' Copyright (C) 2007 db4objects Inc. http:'www.db4o.com

Imports System
Imports System.IO
Imports System.Collections.Generic
Imports System.Collections

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.NQCollection
    Class ParameterizedExamples

        Private Const Db4oFileName As String = "reference.db4o"

        Private Const ObjectCount As Integer = 10

        Private Shared _container As IObjectContainer = Nothing


        Public Shared Sub Main(ByVal args As String())
            StorePilots()
            GetTestPilots()
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
        ' end storePilots

        Private Class PilotNamePredicate
            Inherits Query.Predicate
            Private startsWith As String

            Public Sub New(ByVal startsWith As String)
                Me.startsWith = startsWith
            End Sub

            Public Function Match(ByVal p As Pilot) As Boolean
                Return p.Name.StartsWith(startsWith)
            End Function
        End Class
        ' end PilotNamePredicate

        Private Shared Sub GetTestPilots()
            Dim container As IObjectContainer = Database()
            If Not container Is Nothing Then
                Try
                    Dim result As IObjectSet = container.Query(New PilotNamePredicate("Test"))
                    ListResult(result)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end GetTestPilots



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
