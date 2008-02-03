' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.TA

Namespace Db4objects.Db4odoc.TPClone

    Public Class TPCloneExample
        Private Const Db4oFileName As String = "reference.db4o"
        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StoreCar()
            TestClone()
        End Sub
        ' end Main

        Private Shared Sub StoreCar()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(Db4oFactory.NewConfiguration())
            If container IsNot Nothing Then
                Try
                    ' create a car
                    Dim car As New Car("BMW", New Pilot("Rubens Barrichello"))
                    container.Store(car)
                    Dim car1 As Car = DirectCast(car.Clone(), Car)
                    container.Store(car1)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end StoreCar



        Private Shared Function ConfigureTP() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration
            ' add TP support
            configuration.Add(New TransparentPersistenceSupport)
            Return configuration
        End Function
        ' end ConfigureTP

        Private Shared Sub TestClone()
            Dim configuration As IConfiguration = ConfigureTP()

            Dim container As IObjectContainer = Database(configuration)
            If container IsNot Nothing Then
                Try
                    Dim result As IObjectSet = container.QueryByExample(New Car(Nothing, Nothing))
                    ListResult(result)
                    Dim car As Car = Nothing
                    Dim car1 As Car = Nothing
                    If result.Size() > 0 Then
                        car = DirectCast(result(0), Car)
                        System.Console.WriteLine("Retrieved car: " + car.ToString())
                        car1 = DirectCast(car.Clone(), Car)
                        System.Console.WriteLine("Storing cloned car: " + car1.ToString())
                        container.Store(car1)
                        container.Commit()
                    End If
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end TestClone



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