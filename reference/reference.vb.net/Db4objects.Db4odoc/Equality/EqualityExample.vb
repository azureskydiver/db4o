' Copyright (C) 2007 db4objects Inc. http://www.db4o.com

Imports System
Imports System.Collections.Generic
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Query


Namespace Db4objects.Db4odoc.Equality
    Class EqualityExample
        Private Const Db4oFileName As String = "reference.db4o"

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            File.Delete(Db4oFileName)

            StorePilot()
            TestEquality()
            RetrieveEqual()
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

        Private Shared Sub StorePilot()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim pilot As New Pilot("Kimi Raikkonnen", 100)
                    container.[Set](pilot)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end StorePilot

        Private Shared Sub TestEquality()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result As IList(Of Pilot) = container.Query(Of Pilot)(AddressOf PilotTestMatch)
                    Dim obj As Pilot = result(0)
                    Dim pilot As New Pilot("Kimi Raikkonnen", 100)
                    Dim equality As String = IIf(obj.Equals(pilot), "equal", "not equal").ToString
                    System.Console.WriteLine("Pilots are " + equality)
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end TestEquality

        Private Shared Sub RetrieveEqual()
            Dim container As IObjectContainer = Database()
            If container IsNot Nothing Then
                Try
                    Dim result As IObjectSet = container.[Get](New Pilot("Kimi Raikkonnen", 100))
                    If result.Count > 0 Then
                        System.Console.WriteLine("Found equal object: " + result.[Next]().ToString())
                    Else
                        System.Console.WriteLine("No equal object exist in the database")
                    End If
                Catch ex As Exception
                    System.Console.WriteLine("System Exception: " + ex.Message)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end RetrieveEqual


        Private Shared Function PilotTestMatch(ByVal p As Pilot) As Boolean
            Return p.Name.Equals("Kimi Raikkonnen") AndAlso p.Points = 100
        End Function
        ' end PilotTestMatch

    End Class
End Namespace