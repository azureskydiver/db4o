' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports System.Drawing

Namespace Db4objects.Db4odoc.StaticFields
    Public Class StaticFieldExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Sub New()
        End Sub

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

        Public Shared Sub SetCar()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim car As Car = New Car()
                car._color = Color.Green
                db.Set(car)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetCar

        Public Shared Sub SetPilotsSimple()
            Console.WriteLine("In the default setting, static constants are not continously stored and updated.")
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                db.Set(New Pilot("Michael Schumacher", PilotCategories.WINNER))
                db.Set(New Pilot("Rubens Barrichello", PilotCategories.TALENTED))
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetPilotsSimple

        Public Shared Sub SetPilotsStatic()
            Console.WriteLine("The feature can be turned on for individual classes.")
            Db4oFactory.Configure().ObjectClass(GetType(PilotCategories)).PersistStaticFieldValues()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                db.Set(New Pilot("Michael Schumacher", PilotCategories.WINNER))
                db.Set(New Pilot("Rubens Barrichello", PilotCategories.TALENTED))
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetPilotsStatic

        Public Shared Sub CheckPilots()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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

        Public Shared Sub UpdatePilots()
            Console.WriteLine("Updating PilotCategory in pilot reference:")
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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
            Finally
                db.Close()
            End Try
            PrintCategories()
        End Sub
        ' end UpdatePilots

        Public Shared Sub UpdatePilotCategories()
            Console.WriteLine("Updating PilotCategories explicitly:")
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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
            Finally
                db.Close()
            End Try
            PrintCategories()
        End Sub
        ' end UpdatePilotCategories

        Public Shared Sub DeleteTest()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            db.Ext().Configure().ObjectClass(GetType(Pilot)).CascadeOnDelete(True)
            Try
                Console.WriteLine("Deleting Pilots :")
                Dim result As IObjectSet = db.Get(GetType(Pilot))
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pilot As Pilot = CType(result(x), Pilot)
                    db.Delete(pilot)
                Next
                PrintCategories()
                Console.WriteLine("Deleting PilotCategories :")
                result = db.Get(GetType(PilotCategories))
                For x = 0 To result.Count - 1 Step x + 1
                    db.Delete(result(x))
                Next
                PrintCategories()
            Finally
                db.Close()
            End Try
        End Sub
        ' end DeleteTest

        Public Shared Sub PrintCategories()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(PilotCategories))
                Console.WriteLine("Stored categories: " + (result.Count).ToString())
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pc As PilotCategories = CType(result(x), PilotCategories)
                    Console.WriteLine("Category: " + pc.ToString())
                Next
            Finally
                db.Close()
            End Try
        End Sub
        ' end PrintCategories

        Public Shared Sub DeletePilotCategories()
            PrintCategories()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim result As IObjectSet = db.Get(GetType(PilotCategories))
                Dim x As Integer
                For x = 0 To result.Count - 1 Step x + 1
                    Dim pc As PilotCategories = CType(result(x), PilotCategories)
                    db.Delete(pc)
                Next
            Finally
                db.Close()
            End Try
            PrintCategories()
        End Sub
        ' end DeletePilotCategories
    End Class
End Namespace
