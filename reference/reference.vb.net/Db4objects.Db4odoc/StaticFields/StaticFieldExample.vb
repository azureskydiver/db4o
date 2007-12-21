' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query
Imports System.Drawing

Namespace Db4objects.Db4odoc.StaticFields
    Public Class StaticFieldExample
        Private Const Db4oFileName As String = "reference.db4o"

        Private Shared _container As IObjectContainer = Nothing
        Private Shared _configuration As IConfiguration = Nothing

        Public Sub New()
        End Sub

        Public Shared Sub Main(ByVal args As String())
            Console.WriteLine("In the default setting, static constants are not continously stored and updated.")

            SetPilots()
            CheckPilots()
            '
            Configure()
            SetPilots()
            CheckPilots()
            UpdatePilots()
            UpdatePilotCategories()
            CheckPilots()
            AddDeleteConfiguration()
            DeleteTest()
        End Sub
        ' end Main

		Private Shared Sub SetCar()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim car As Car = New Car()
                car._color = Color.Green
                db.Set(car)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetCar

        Private Shared Function Database() As IObjectContainer
            If _container Is Nothing Then
                Try
                    If _configuration Is Nothing Then
                        _container = Db4oFactory.OpenFile(Db4oFileName)
                    Else
                        _container = Db4oFactory.OpenFile(_configuration, Db4oFileName)
                    End If
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

        Private Shared Sub Configure()
            System.Console.WriteLine("Saving static fields can be turned on for individual classes.")
            _configuration = Db4oFactory.NewConfiguration()
            _configuration.ObjectClass(GetType(PilotCategories)).PersistStaticFieldValues()
        End Sub
        ' end Configure

        Private Shared Sub SetPilots()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Database()
            If db IsNot Nothing Then
                Try
                    db.[Set](New Pilot("Michael Schumacher", PilotCategories.Winner))
                    db.[Set](New Pilot("Rubens Barrichello", PilotCategories.Talented))
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end SetPilots


        Private Shared Sub CheckPilots()
            Dim db As IObjectContainer = Database()
            If db IsNot Nothing Then
                Try
                    Dim result As IObjectSet = db.[Get](GetType(Pilot))
                    For x As Integer = 0 To result.Count - 1
                        Dim pilot As Pilot = DirectCast(result(x), Pilot)
                        If pilot.Category Is PilotCategories.WINNER Then
                            Console.WriteLine("Winner pilot: " + pilot.ToString())
                        ElseIf pilot.Category Is PilotCategories.TALENTED Then
                            Console.WriteLine("Talented pilot: " + pilot.ToString())
                        Else
                            Console.WriteLine("Uncategorized pilot: " + pilot.ToString())
                        End If
                    Next
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end CheckPilots

        Private Shared Sub UpdatePilots()
            Console.WriteLine("Updating PilotCategory in pilot reference:")
            Dim db As IObjectContainer = Database()
            If db IsNot Nothing Then
                Try
                    Dim result As IObjectSet = db.[Get](GetType(Pilot))
                    For x As Integer = 0 To result.Count - 1
                        Dim pilot As Pilot = DirectCast(result(x), Pilot)
                        If pilot.Category Is PilotCategories.WINNER Then
                            Console.WriteLine("Winner pilot: " + pilot.ToString())
                            Dim pc As PilotCategories = pilot.Category
                            pc.TestChange("WINNER2006")
                            db.[Set](pilot)
                        End If
                    Next
                    PrintCategories(db)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end UpdatePilots

        Private Shared Sub UpdatePilotCategories()
            Console.WriteLine("Updating PilotCategories explicitly:")
            Dim db As IObjectContainer = Database()
            If db IsNot Nothing Then
                Try
                    Dim result As IObjectSet = db.[Get](GetType(PilotCategories))
                    For x As Integer = 0 To result.Count - 1
                        Dim pc As PilotCategories = DirectCast(result(x), PilotCategories)
                        If pc Is PilotCategories.WINNER Then
                            pc.TestChange("WINNER2006")
                            db.[Set](pc)
                        End If
                    Next
                    PrintCategories(db)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end UpdatePilotCategories

        Private Shared Sub AddDeleteConfiguration()
            If _configuration IsNot Nothing Then
                _configuration.ObjectClass(GetType(Pilot)).CascadeOnDelete(True)
            End If
        End Sub
        ' end AddDeleteConfiguration

        Private Shared Sub DeleteTest()
            Dim db As IObjectContainer = Database()
            If db IsNot Nothing Then
                Try
                    Console.WriteLine("Deleting Pilots :")
                    Dim result As IObjectSet = db.[Get](GetType(Pilot))
                    For x As Integer = 0 To result.Count - 1
                        Dim pilot As Pilot = DirectCast(result(x), Pilot)
                        db.Delete(pilot)
                    Next
                    PrintCategories(db)
                    Console.WriteLine("Deleting PilotCategories :")
                    result = db.[Get](GetType(PilotCategories))
                    For x As Integer = 0 To result.Count - 1
                        db.Delete(result(x))
                    Next
                    PrintCategories(db)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end DeleteTest

        Private Shared Sub PrintCategories(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](GetType(PilotCategories))
            Console.WriteLine("Stored categories: " + result.Count.ToString())
            For x As Integer = 0 To result.Count - 1
                Dim pc As PilotCategories = DirectCast(result(x), PilotCategories)
                Console.WriteLine("Category: " + pc.ToString())
            Next
        End Sub
        ' end PrintCategories

        Private Shared Sub DeletePilotCategories()
            Dim db As IObjectContainer = Database()
            If db IsNot Nothing Then
                Try
                    PrintCategories(db)
                    Dim result As IObjectSet = db.[Get](GetType(PilotCategories))
                    For x As Integer = 0 To result.Count - 1
                        Dim pc As PilotCategories = DirectCast(result(x), PilotCategories)
                        db.Delete(pc)
                    Next
                    PrintCategories(db)
                Finally
                    CloseDatabase()
                End Try
            End If
        End Sub
        ' end DeletePilotCategories
    End Class
End Namespace
