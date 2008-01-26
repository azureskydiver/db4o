' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.Collections.Generic
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.TA


Namespace Db4objects.Db4odoc.TPExample
    Public Class TPCollectionExample

        Private Const Db4oFileName As String = "reference.db4o"

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            TestCollectionPersistence()
        End Sub
        ' end Main

        Private Shared Function ConfigureTP() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            ' add TP support
            configuration.Add(New TransparentPersistenceSupport())
            Return configuration
        End Function
        ' end ConfigureTP

        Private Shared Sub StoreCollection()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(ConfigureTP())
            If container IsNot Nothing Then
                Try
                    Dim team As New Team()
                    For i As Integer = 0 To 9
                        team.AddPilot(New Pilot("Pilot #" + i.ToString))
                    Next
                    container.Store(team)
                    container.Commit()
                Catch ex As Exception
                    System.Console.WriteLine(ex.StackTrace)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end StoreCollection

        Private Shared Sub TestCollectionPersistence()
            StoreCollection()
            Dim container As IObjectContainer = Database(ConfigureTP())
            If container IsNot Nothing Then
                Try
                    Dim team As Team = DirectCast(container.QueryByExample(New Team()).[Next](), Team)
                    ' this method will activate all the members in the collection
                    Dim pilots As IList(Of Pilot) = team.Pilots
                    For Each p As Pilot In pilots
                        p.Name = "Modified: " + p.Name
                    Next
                    team.AddPilot(New Pilot("New pilot"))
                    ' explicitly commit to persist changes
                    container.Commit()
                Catch ex As Exception
                    System.Console.WriteLine(ex.Message)
                Finally
                    ' If TP changes were not committed explicitly,
                    ' they would be persisted with the #close call
                    CloseDatabase()
                End Try
            End If
            ' reopen the database and check the changes
            container = Database(ConfigureTP())
            If container IsNot Nothing Then
                Try
                    Dim result As IObjectSet = container.QueryByExample(New Team())
                    Dim team As Team = DirectCast(result(0), Team)
                    team.ListAllPilots()
                Catch ex As Exception
                    System.Console.WriteLine(ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end TestCollectionPersistence


        Private Shared Function Database(ByVal configuration As IConfiguration) As IObjectContainer
            If _container Is Nothing Then
                Try
                    _container = Db4oFactory.OpenFile(configuration, Db4oFileName)
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

    End Class
End Namespace
