' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.TA


Namespace Db4ojects.Db4odoc.TAMigrate
    Public Class TAExample

        Private Const FirstDbName As String = "reference.db4o"
        Private Const SecondDbName As String = "migrate.db4o"


        Public Shared Sub Main(ByVal args As String())
            ' TestSwitchDatabases()
            TestSwitchDatabasesFixed()
        End Sub
        ' end Main

        Private Shared Sub StoreSensorPanel()
            File.Delete(FirstDbName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(FirstDbName)
            If container IsNot Nothing Then
                Try
                    ' create a linked list with length 10
                    Dim list As SensorPanelTA = New SensorPanelTA().CreateList(10)
                    container.Store(list)
                Finally
                    container.Close()
                End Try
            End If
        End Sub
        ' end StoreSensorPanel


        Private Shared Function ConfigureTA() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            ' add TA support
            configuration.Add(New TransparentActivationSupport())
            Return configuration
        End Function
        ' end ConfigureTA

        Private Shared Sub TestSwitchDatabases()
            StoreSensorPanel()

            Dim firstDb As IObjectContainer = Db4oFactory.OpenFile(ConfigureTA(), FirstDbName)
            Dim secondDb As IObjectContainer = Db4oFactory.OpenFile(ConfigureTA(), SecondDbName)
            Try
                Dim result As IObjectSet = firstDb.QueryByExample(New SensorPanelTA(1))
                If result.Count > 0 Then
                    Dim sensor As SensorPanelTA = DirectCast(result(0), SensorPanelTA)
                    firstDb.Close()
                    ' Migrating an object from the first database
                    ' into a second database
                    secondDb.Store(sensor)
                End If
            Finally
                firstDb.Close()
                secondDb.Close()
            End Try
        End Sub
        ' end TestSwitchDatabases


        Private Shared Sub TestSwitchDatabasesFixed()
            StoreSensorPanel()

            Dim firstDb As IObjectContainer = Db4oFactory.OpenFile(ConfigureTA(), FirstDbName)
            Dim secondDb As IObjectContainer = Db4oFactory.OpenFile(ConfigureTA(), SecondDbName)
            Try
                Dim result As IObjectSet = firstDb.QueryByExample(New SensorPanelTA(1))
                If result.Count > 0 Then
                    Dim sensor As SensorPanelTA = DirectCast(result(0), SensorPanelTA)
                    ' Unbind the object from the first database
                    sensor.Bind(Nothing)
                    ' Migrating the object into the second database
                    secondDb.Store(sensor)


                    System.Console.WriteLine("Retrieving previous query results from " + FirstDbName + ":")
                    Dim [next] As SensorPanelTA = sensor.NextSensor
                    While [next] IsNot Nothing
                        System.Console.WriteLine([next])
                        [next] = [next].NextSensor
                    End While

                    System.Console.WriteLine("Retrieving previous query results from " + FirstDbName + " with manual activation:")
                    firstDb.Activate(sensor, Int32.MaxValue)
                    [next] = sensor.NextSensor
                    While [next] IsNot Nothing
                        System.Console.WriteLine([next])
                        [next] = [next].NextSensor
                    End While

                    System.Console.WriteLine("Retrieving sensorPanel from " + SecondDbName + ":")
                    result = secondDb.QueryByExample(New SensorPanelTA(1))
                    [next] = sensor.NextSensor
                    While [next] IsNot Nothing
                        System.Console.WriteLine([next])
                        [next] = [next].NextSensor
                    End While
                End If
            Finally
                firstDb.Close()
                secondDb.Close()
            End Try
        End Sub
        ' end TestSwitchDatabasesFixed

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Size())
            While result.HasNext()
                System.Console.WriteLine(result.[Next]())
            End While
        End Sub
        ' end ListResult
    End Class
End Namespace
