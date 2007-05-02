' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports System.Drawing

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.StaticFields
    Public Class StaticFieldExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            SetPilotsSimple()
            CheckPilots()

            SetPilotsStatic()
            CheckPilots()
            UpdatePilots()
            UpdatePilotCategories()
            CheckPilots()
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

        Private Shared Sub SetPilotsSimple()
            Console.WriteLine("In the default setting, static constants are not continously stored and updated.")
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                db.Set(New Pilot("Michael Schumacher", PilotCategories.WINNER))
                db.Set(New Pilot("Rubens Barrichello", PilotCategories.TALENTED))
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetPilotsSimple

        Private Shared Sub SetPilotsStatic()
            Console.WriteLine("The feature can be turned on for individual classes.")
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(PilotCategories)).PersistStaticFieldValues()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(configuration, Db4oFileName)
            Try
                db.Set(New Pilot("Michael Schumacher", PilotCategories.WINNER))
                db.Set(New Pilot("Rubens Barrichello", PilotCategories.TALENTED))
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetPilotsStatic

        Private Shared Sub CheckPilots()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pilot As Pilot = CType(result(x), Pilot)
                    If pilot.Category Is PilotCategories.WINNER Then
                        Console.WriteLine("Winner pilot: " + pilot.ToString())
                    ElseIf pilot.Category Is PilotCategories.TALENTED Then
                        Console.WriteLine("Talented pilot: " + pilot.ToString())
                    Else
                        Console.WriteLine("Uncategorized pilot: " + pilot.ToString())
                    End If
                Next
            Finally
                db.Close()
            End Try
        End Sub
        ' end CheckPilots

        Private Shared Sub UpdatePilots()
            Console.WriteLine("Updating PilotCategory in pilot reference:")
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pilot As Pilot = CType(result(x), Pilot)
                    If pilot.Category Is PilotCategories.WINNER Then
                        Console.WriteLine("Winner pilot: " + pilot.ToString())
                        Dim pc As PilotCategories = pilot.Category
                        pc.TestChange("WINNER2006")
                        db.Set(pilot)
                    End If
                Next
                PrintCategories(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end UpdatePilots

        Private Shared Sub UpdatePilotCategories()
            Console.WriteLine("Updating PilotCategories explicitly:")
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(PilotCategories))
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pc As PilotCategories = CType(result(x), PilotCategories)
                    If pc Is PilotCategories.WINNER Then
                        pc.TestChange("WINNER2006")
                        db.Set(pc)
                    End If
                Next
                PrintCategories(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end UpdatePilotCategories

        Private Shared Sub DeleteTest()
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Pilot)).CascadeOnDelete(True)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Console.WriteLine("Deleting Pilots :")
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pilot As Pilot = CType(result(x), Pilot)
                    db.Delete(pilot)
                Next
                PrintCategories(db)
                Console.WriteLine("Deleting PilotCategories :")
                result = db.Get(GetType(PilotCategories))
                For x = 0 To result.Count - 1 Step x + 1
                    db.Delete(result(x))
                Next
                PrintCategories(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end DeleteTest

        Private Shared Sub PrintCategories(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(PilotCategories))
            Console.WriteLine("Stored categories: " + (result.Count).ToString())
            Dim x As Integer
            For x = 0 To result.Count - 1 Step x + 1
                Dim pc As PilotCategories = CType(result(x), PilotCategories)
                Console.WriteLine("Category: " + pc.ToString())
            Next
        End Sub
        ' end PrintCategories

        Private Shared Sub DeletePilotCategories()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                PrintCategories(db)
                Dim result As IObjectSet = db.Get(GetType(PilotCategories))
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pc As PilotCategories = CType(result(x), PilotCategories)
                    db.Delete(pc)
                Next
                PrintCategories(db)
            Finally
                db.Close()
            End Try
        End Sub
        ' end DeletePilotCategories
    End Class
End Namespace
