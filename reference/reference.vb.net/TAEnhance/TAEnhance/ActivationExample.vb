' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Imports System.IO
Imports System.Collections.Generic
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.TA
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query
Imports Db4objects.Db4o.Types
Imports Db4objects.Db4o.Diagnostic

Namespace Db4objects.Db4odoc.Activating

    Public Class ActivationExample
        Private Const Db4oFileName As String = "reference.db4o"
        Private Shared _container As IObjectContainer

        Public Shared Sub Main(ByVal args As String())
            TestCollection()
        End Sub
        ' end Main

        Private Shared Sub StoreCollection()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Database(ConfigureTA())
            If db IsNot Nothing Then
                Try
                    ' create a linked list with length 10
                    Dim sensorPanel As SensorPanel = New SensorPanel().CreateList(10)
                    ' store all elements with one statement, since all elements are new		
                    db.[Set](sensorPanel)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StoreCollection

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

        Private Shared Function ConfigureTA() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.Add(New TransparentActivationSupport())
            ActivateDiagnostics(configuration)

            Return configuration
        End Function
        ' end configureTA

        Private Shared Sub ActivateDiagnostics(ByVal configuration As IConfiguration)
            ' Add diagnostic listener that will show all the classes that are not
            ' TA aware.
            configuration.Diagnostic().AddListener(New TaDiagListener())
        End Sub
        ' end ActivateDiagnostics


        Public Class TaDiagListener
            Inherits DiagnosticToConsole
            Public Overloads Overrides Sub OnDiagnostic(ByVal d As IDiagnostic)
                If TypeOf d Is NotTransparentActivationEnabled Then
                    System.Console.WriteLine(d)
                End If
            End Sub
        End Class
        ' end TaDiagListener

        Private Shared Function FirstPanel(ByVal sensorPanel As SensorPanel) As Boolean
            If (sensorPanel.Sensor.Equals(1)) Then
                Return True
            End If
            Return False
        End Function
        ' end FirstPanel

        Private Shared Sub TestCollection()
            StoreCollection()
            Dim db As IObjectContainer = Database(ConfigureTA())
            If db IsNot Nothing Then
                Try
                    Dim result As IList(Of SensorPanel) = db.Query(Of SensorPanel)(AddressOf FirstPanel)
                    Console.WriteLine(result.Count)
                    If result.Count > 0 Then
                        Dim sensor As SensorPanel = DirectCast(result(0), SensorPanel)
                        Dim [next] As SensorPanel = sensor.Next
                        While [next] IsNot Nothing
                            Console.WriteLine([next].ToString)
                            [next] = [next].Next
                        End While
                    End If
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end TestCollection


        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item.ToString)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
