' Copyright (C) 2007 db4objects Inc. http://www.db4o.com

Imports System
Imports System.Collections
Imports System.Collections.Generic
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.QBE
    Class QBEExample
        Private Const Db4oFileName As String = "reference.db4o"

        Private Const ObjectCount As Integer = 10

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            File.Delete(Db4oFileName)

            Test()
            Test1()
            Test2()
            Test3()
            Test4()
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
            If _container IsNot Nothing Then
                _container.Close()
                _container = Nothing
            End If
        End Sub

        ' end CloseDatabase

        Private Shared Sub Test()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim pilot As New Pilot("Kimi Raikonnen", 100)
                    container.[Set](pilot)
                    container.Commit()
                    Dim result As IObjectSet = container.[Get](New Pilot("Kimi Raikonnen", 100))
                    ListResult(result)
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end Test

        Private Shared Sub Test1()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    ' Pilot1 contains re-initialisation in the constructor
                    Dim pilot As New Pilot1("Kimi Raikonnen", 100)
                    container.[Set](pilot)
                    container.Commit()
                    ' QBE returns result with wrong points
                    Dim result As IObjectSet = container.[Get](New Pilot1("Kimi Raikonnen", 100))
                    System.Console.WriteLine("Test QBE on class with member re-initialization in constructor")
                    ListResult(result)
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end Test1

        Private Shared Sub Test2()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    ' Pilot1Derived derives the constructor with re-initialisation
                    Dim pilot As New Pilot1Derived("Kimi Raikonnen", 100)
                    container.[Set](pilot)
                    container.Commit()
                    ' QBE returns result with wrong points
                    Dim result As IObjectSet = container.[Get](New Pilot1Derived("Kimi Raikonnen", 100))
                    System.Console.WriteLine("Test QBE on class with member re-initialization in ancestor constructor")
                    ListResult(result)
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end Test2

        Private Shared Sub Test3()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    ' Pilot2 uses static value to initializ points
                    Dim pilot As New Pilot2("Kimi Raikonnen", 100)
                    container.[Set](pilot)
                    container.Commit()
                    ' QBE returns result with wrong points
                    Dim result As IObjectSet = container.[Get](New Pilot2("Kimi Raikonnen", 100))
                    System.Console.WriteLine("Test QBE on class with member initialization using static value")
                    ListResult(result)
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end Test3

        Private Shared Sub Test4()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    ' Pilot2Derived is derived from class with initialization of points member using static value
                    Dim pilot As New Pilot2Derived("Kimi Raikonnen", 100)
                    container.[Set](pilot)
                    container.Commit()
                    ' QBE returns result with wrong points
                    Dim result As IObjectSet = container.[Get](New Pilot2Derived("Kimi Raikonnen", 100))
                    System.Console.WriteLine("Test QBE on class derived from a class with member initialization using static member")
                    ListResult(result)
                Catch ex As Db4oException
                    System.Console.WriteLine("Db4o Exception: " + ex.Message)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end Test4

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