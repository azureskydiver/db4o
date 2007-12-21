' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.TA

Namespace Db4ojects.Db4odoc.TAExamples

    Public Class TAExample
        Private Const Db4oFileName As String = "reference.db4o"
        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            TestActivation()
            TestCollectionActivation()
        End Sub
        ' end Main

        Private Shared Sub StoreSensorPanel()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(Db4oFactory.NewConfiguration)
            If Not (container Is Nothing) Then
                Try
                    ' create a linked list with length 10
                    Dim list As SensorPanelTA = (New SensorPanelTA).CreateList(10)
                    container.Set(list)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StoreSensorPanel

        Private Class TADiagnostics
            Implements IDiagnosticListener

            Public Sub OnDiagnostic(ByVal diagnostic As IDiagnostic) Implements IDiagnosticListener.OnDiagnostic
                If Not (TypeOf diagnostic Is NotTransparentActivationEnabled) Then
                    Return
                End If
                System.Console.WriteLine(diagnostic)
            End Sub
        End Class
        ' end TADiagnostics

        Private Shared Sub ActivateDiagnostics(ByVal configuration As IConfiguration)
            ' Add diagnostic listener that will show all the classes that are not
            ' TA aware.
            configuration.Diagnostic.AddListener(New TADiagnostics)
        End Sub
        ' end ActivateDiagnostics

        Private Shared Function ConfigureTA() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration
            ' add TA support
            configuration.Add(New TransparentActivationSupport)
            ' activate TA diagnostics to reveal the classes that are not TA-enabled.
            ' ActivateDiagnostics(configuration)
            Return configuration
        End Function
        ' end ConfigureTA

        Private Shared Sub TestActivation()
            StoreSensorPanel()
            Dim configuration As IConfiguration = ConfigureTA()
            Dim container As IObjectContainer = Database(configuration)
            If Not (container Is Nothing) Then
                Try
                    System.Console.WriteLine("Zero activation depth")
                    Dim result As IObjectSet = container.Get(New SensorPanelTA(1))
                    ListResult(result)
                    If result.Size > 0 Then
                        Dim sensor As SensorPanelTA = CType(result(0), SensorPanelTA)
                        ' the object is a linked list, so each call to next()
                        ' will need to activate a new object
                        Dim nextSensor As SensorPanelTA = sensor.NextSensor
                        While Not (nextSensor Is Nothing)
                            System.Console.WriteLine(nextSensor)
                            nextSensor = nextSensor.NextSensor
                        End While
                    End If
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end TestActivation

        Private Shared Sub StoreCollection()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(ConfigureTA)
            If Not (container Is Nothing) Then
                Try
                    Dim team As Team = New Team
                    Dim i As Integer = 0
                    While i < 10
                        team.AddPilot(New Pilot("Pilot #" + i.ToString()))
                        i = i + 1
                    End While
                    container.Set(team)
                    container.Commit()
                Catch ex As Exception
                    System.Console.WriteLine(ex.StackTrace)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StoreCollection

        Private Shared Sub TestCollectionActivation()
            StoreCollection()
            Dim container As IObjectContainer = Database(ConfigureTA)
            If Not (container Is Nothing) Then
                Try
                    Dim team As Team = CType(container.Get(New Team).Next, Team)
                    ' this method will activate all the members in the collection 
                    team.ListAllPilots()
                Catch ex As Exception
                    System.Console.WriteLine(ex.StackTrace)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end TestCollectionActivation

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
            If Not (_container Is Nothing) Then
                _container.Close()
                _container = Nothing
            End If
        End Sub
        ' end CloseDatabase

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Size)
            While result.HasNext
                System.Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult
    End Class
End Namespace