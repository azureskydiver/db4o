' Copyright (C) 2007 db4objects Inc. http://www.db4o.com 


Imports System
'Imports System.Linq
Imports System.IO
Imports System.Collections.Generic
Imports System.Collections

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Linq
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.Config

Namespace Db4objects.Db4odoc.Linq
    Class LinqExample

        Private Shared ReadOnly Db4oFileName As String = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "reference.db4o")


        Private Const ObjectCount As Integer = 10

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StoreObjects()
            'Test()
            SelectEverythingByName()
            SelectPilotByNameAndPoints()
            SelectUnoptimized()
        End Sub

        ' end Main

        Private Shared Function Database(ByVal config As IConfiguration) As IObjectContainer
            If _container Is Nothing Then
                Try
                    _container = Db4oFactory.OpenFile(config, Db4oFileName)
                Catch ex As DatabaseFileLockedException
                    System.Console.WriteLine(ex.Message)
                End Try
            End If
            Return _container
        End Function

        ' end Database

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

        Private Shared Sub StoreObjects()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim pilot As Pilot
                    Dim car As Car
                    For i As Integer = 0 To ObjectCount - 1
                        pilot = New Pilot("Test Pilot #" + i.ToString(), i + 10)
                        car = New Car("Test model #" + i.ToString(), pilot)
                        container.Store(car)
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

        ' end StoreObjects

        Private Shared Sub SelectEverythingByName()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From o In container Where o.ToString().StartsWith("Test") Select o
                    Dim objects As IList = result.ToList()
                    ListResult(objects)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end SelectEverythingByName

        Private Shared Sub SelectUnoptimized()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim pilots = From p As Pilot In container _
                                 Where p.Points = p.Name.Length Select p
                    ListResult(pilots)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectUnoptimized

        Private Shared Sub SelectPilotByNameAndPoints()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result = From p As Pilot In container _
                                 Where p.Name.StartsWith("Test") And p.Points > 12 Select p
                    ListResult(result)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SelectPilotByNameAndPoints

        Private Shared Sub Test()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim pilots = From p As Pilot In container Select p
                    ListResult(pilots)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end Test


        Private Shared Sub ListResult(ByVal result As IList(Of Car))
            System.Console.WriteLine(result.Count)
            For Each car As Car In result
                System.Console.WriteLine(car)
            Next
        End Sub

        ' end ListResult

        Private Shared Sub ListResult(ByVal result As IList)
            System.Console.WriteLine(result.Count)
            For Each o As Object In result
                System.Console.WriteLine(o)
            Next
        End Sub

        ' end ListResult

        Private Shared Sub ListResult(Of T)(ByVal result As IEnumerable(Of T))
            System.Console.WriteLine(result.Count())
            For Each obj As Object In result
                If obj.GetType() Is GetType(T) Then
                    System.Console.WriteLine(obj)
                End If
            Next
        End Sub

        ' end ListResult


    End Class
End Namespace
