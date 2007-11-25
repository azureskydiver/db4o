' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 

Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Identity
    Public Class IdentityExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args() As String)
            CheckUniqueness()
            CheckReferenceCache()
            CheckReferenceCacheWithPurge()
            TestBind()

            TestCopyingWithPurge()
        End Sub
        ' end Main

        Private Shared Sub SetObjects()
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim car As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                db.Set(car)
                car = New Car("Ferrari", New Pilot("Michael Schumacher"))
                db.Set(car)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetObjects

        Private Shared Sub CheckUniqueness()
            SetObjects()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim cars As IObjectSet = db.Get(GetType(Car))
                Dim car As Car = CType(cars(0), Car)
                Dim pilotName As String = car.Pilot.Name
                Dim pilots As IObjectSet = db.Get(New Pilot(pilotName))
                Dim pilot As Pilot = CType(pilots(0), Pilot)
                System.Console.WriteLine("Retrieved objects are identical: " + (pilot Is car.Pilot).ToString())
            Finally
                db.Close()
            End Try
        End Sub
        ' end CheckUniqueness

        Private Shared Sub CheckReferenceCache()
            SetObjects()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim pilots As IObjectSet = db.Get(GetType(Pilot))
                Dim pilot As Pilot = CType(pilots(0), Pilot)
                Dim pilotName As String = pilot.Name
                pilot.Name = "new name"
                System.Console.WriteLine("Retrieving pilot by name: " + pilotName)
                Dim pilots1 As IObjectSet = db.Get(New Pilot(pilotName))
                ListResult(pilots1)
            Finally
                db.Close()
            End Try
        End Sub
        ' end CheckReferenceCache

        Private Shared Sub CheckReferenceCacheWithPurge()
            SetObjects()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim pilots As IObjectSet = db.Get(GetType(Pilot))
                Dim pilot As Pilot = CType(pilots(0), Pilot)
                Dim pilotName As String = pilot.Name
                pilot.Name = "new name"
                System.Console.WriteLine("Retrieving pilot by name: " + pilotName)
                Dim pilotID As Long = db.Ext().GetID(pilot)
                If db.Ext().IsCached(pilotID) Then
                    db.Ext().Purge(pilot)
                End If
                Dim pilots1 As IObjectSet = db.Get(New Pilot(pilotName))
                ListResult(pilots1)
            Finally
                db.Close()
            End Try
        End Sub
        ' end CheckReferenceCacheWithPurge

        Private Shared Sub TestCopyingWithPurge()
            SetObjects()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim pilots As IObjectSet = db.Get(GetType(Pilot))
                Dim pilot As Pilot = CType(pilots(0), Pilot)
                db.Ext().Purge(pilot)
                db.Set(pilot)
                pilots = db.Get(GetType(Pilot))
                ListResult(pilots)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestCopyingWithPurge

        Private Shared Sub TestBind()
            SetObjects()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim q As IQuery = db.Query()
                q.Constrain(GetType(Car))
                q.Descend("_model").Constrain("Ferrari")
                Dim result As IObjectSet = q.Execute()
                Dim car1 As Car = CType(result(0), Car)
                Dim IdCar1 As Long = db.Ext().GetID(car1)
                Dim car2 As Car = New Car("BMW", New Pilot("Rubens Barrichello"))
                db.Ext().Bind(car2, IdCar1)
                db.Set(car2)

                result = db.Get(GetType(Car))
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end TestBind

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult

    End Class
End Namespace
