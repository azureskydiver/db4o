' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.TA

Namespace Db4ojects.Db4odoc.TPExample

    Public Class TPExample
        Private Const Db4oFileName As String = "reference.db4o"
        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            TestTransparentPersistence()
        End Sub
        ' end Main

        Private Shared Sub StoreSensorPanel()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(Db4oFactory.NewConfiguration)
            If Not (container Is Nothing) Then
                Try
                    ' create a linked list with length 10
                    Dim list As SensorPanel = (New SensorPanel).CreateList(10)
                    container.Set(list)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end StoreSensorPanel

        Private Shared Function ConfigureTA() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration
            ' add TA support
            configuration.Add(New TransparentPersistenceSupport)
            Return configuration
        End Function
        ' end ConfigureTA

        Private Shared Sub TestTransparentPersistence()
            StoreSensorPanel()
            Dim configuration As IConfiguration = ConfigureTA()
            Dim container As IObjectContainer = Database(configuration)
            If Not (container Is Nothing) Then
                Try
                    Dim result As IObjectSet = container.QueryByExample(New SensorPanel(1))
                    Dim sensor As SensorPanel = Nothing
                    ListResult(result)
                    If result.Size > 0 Then
                        System.Console.WriteLine("Before modification: ")
                        sensor = CType(result(0), SensorPanel)
                        ' the object is a linked list, so each call to next()
                        ' will need to activate a new object
                        Dim nextSensor As SensorPanel = sensor.NextSensor
                        While Not (nextSensor Is Nothing)
                            System.Console.WriteLine(nextSensor)
                            ' modify the next sensor
                            nextSensor.Sensor = CType((10 + CType(nextSensor.Sensor, Int32)), Object)
                            nextSensor = nextSensor.NextSensor
                        End While
                        ' Explicit commit stores and commits the changes at any time
                        container.Commit()
                    End If
                Finally
                    ' If there are unsaved changes to activatable objects, they 
                    ' will be implicitely saved and committed when the database 
                    ' is closed
                    CloseDatabase()
                End Try
            End If
            ' reopen the database and check the modifications
            container = Database(configuration)
            If Not (container Is Nothing) Then
                Try
                    Dim result As IObjectSet = container.QueryByExample(New SensorPanel(1))
                    Dim sensor As SensorPanel = Nothing
                    ListResult(result)
                    If result.Size > 0 Then
                        System.Console.WriteLine("After modification: ")
                        sensor = CType(result(0), SensorPanel)
                        Dim nextSensor As SensorPanel = sensor.NextSensor
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
        ' end TestTransparentPersistence

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