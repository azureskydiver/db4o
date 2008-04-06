' Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Diagnostic
Imports Db4objects.Db4o.TA


Namespace Db4objects.Db4odoc.TP.Rollback
    Public Class TPCloneExample

        Private Const Db4oFileName As String = "reference.db4o"

        Private Shared _container As IObjectContainer = Nothing

        Public Shared Sub Main(ByVal args As String())
            StoreCar()
            ModifyAndRollback()
            ModifyRollbackAndCheck()
            ModifyWithRollbackStrategy()
        End Sub
        ' end Main

        Private Shared Sub StoreCar()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Database(Db4oFactory.NewConfiguration())
            If container IsNot Nothing Then
                Try
                    ' create a car
                    Dim car As New Car("BMW", New Pilot("Rubens Barrichello", 1))
                    container.Store(car)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end StoreCar

        Private Shared Function ConfigureTP() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            ' add TP support
            configuration.Add(New TransparentPersistenceSupport())
            Return configuration
        End Function
        ' end ConfigureTP

        Private Shared Function ConfigureTPForRollback() As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            ' add TP support and rollback strategy
            configuration.Add(New TransparentPersistenceSupport(New RollbackDeactivateStrategy()))
            Return configuration
        End Function

        ' end ConfigureTPForRollback


        Private Class RollbackDeactivateStrategy
            Implements IRollbackStrategy
            Public Sub Rollback(ByVal container As IObjectContainer, ByVal obj As Object) _
            Implements IRollbackStrategy.Rollback
                container.Ext().Deactivate(obj)
            End Sub
        End Class

        ' end RollbackDeactivateStrategy


        Private Shared Sub ModifyAndRollback()
            Dim container As IObjectContainer = Database(ConfigureTP())
            If container IsNot Nothing Then
                Try
                    ' create a car
                    Dim car As Car = DirectCast(container.QueryByExample(New Car(Nothing, Nothing))(0), Car)
                    System.Console.WriteLine("Initial car: " + car.ToString() + "(" + container.Ext().GetID(car).ToString() + ")")
                    car.Model = "Ferrari"
                    car.Pilot = New Pilot("Michael Schumacher", 123)
                    container.Rollback()
                    System.Console.WriteLine("Car after rollback: " + car.ToString() + "(" + container.Ext().GetID(car).ToString() + ")")
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end ModifyAndRollback

        Private Shared Sub ModifyRollbackAndCheck()
            Dim container As IObjectContainer = Database(ConfigureTP())
            If container IsNot Nothing Then
                Try
                    ' create a car
                    Dim car As Car = DirectCast(container.QueryByExample(New Car(Nothing, Nothing))(0), Car)
                    Dim pilot As Pilot = car.Pilot
                    System.Console.WriteLine("Initial car: " + car.ToString() + "(" + container.Ext().GetID(car).ToString() + ")")
                    System.Console.WriteLine("Initial pilot: " + pilot.ToString() + "(" + container.Ext().GetID(pilot).ToString() + ")")
                    car.Model = "Ferrari"
                    car.ChangePilot("Michael Schumacher", 123)
                    container.Rollback()
                    container.Deactivate(car, Int32.MaxValue)
                    System.Console.WriteLine("Car after rollback: " + car.ToString() + "(" + container.Ext().GetID(car).ToString() + ")")
                    System.Console.WriteLine("Pilot after rollback: " + pilot.ToString() + "(" + container.Ext().GetID(pilot).ToString() + ")")
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub

        ' end ModifyRollbackAndCheck

        Private Shared Sub ModifyWithRollbackStrategy()
            Dim container As IObjectContainer = Database(ConfigureTPForRollback())
            If container IsNot Nothing Then
                Try
                    ' create a car
                    Dim car As Car = DirectCast(container.QueryByExample(New Car(Nothing, Nothing))(0), Car)
                    Dim pilot As Pilot = car.Pilot
                    System.Console.WriteLine("Initial car: " + car.ToString() + "(" + container.Ext().GetID(car).ToString() + ")")
                    System.Console.WriteLine("Initial pilot: " + pilot.ToString() + "(" + container.Ext().GetID(pilot).ToString() + ")")
                    car.Model = "Ferrari"
                    car.ChangePilot("Michael Schumacher", 123)
                    container.Rollback()
                    System.Console.WriteLine("Car after rollback: " + car.ToString() + "(" + container.Ext().GetID(car).ToString() + ")")
                    System.Console.WriteLine("Pilot after rollback: " + pilot.ToString() + "(" + container.Ext().GetID(pilot).ToString() + ")")
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end ModifyWithRollbackStrategy

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
