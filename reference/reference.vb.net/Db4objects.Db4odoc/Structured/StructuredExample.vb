' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Structured
    Public Class StructuredExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            File.Delete(Db4oFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                StoreFirstCar(db)
                StoreSecondCar(db)
                RetrieveAllCarsQBE(db)
                RetrieveAllPilotsQBE(db)
                RetrieveCarByPilotQBE(db)
                RetrieveCarByPilotNameQuery(db)
                RetrieveCarByPilotProtoQuery(db)
                RetrievePilotByCarModelQuery(db)
                UpdateCar(db)
                UpdatePilotSingleSession(db)
                UpdatePilotSeparateSessionsPart1(db)
                db.Close()
                db = Db4oFactory.OpenFile(Db4oFileName)
                UpdatePilotSeparateSessionsPart2(db)
                db.Close()
                Dim configuration As IConfiguration = UpdatePilotSeparateSessionsImprovedPart1(db)
                db = Db4oFactory.OpenFile(configuration, Db4oFileName)
                UpdatePilotSeparateSessionsImprovedPart2(db)
                db.Close()
                db = Db4oFactory.OpenFile(configuration, Db4oFileName)
                UpdatePilotSeparateSessionsImprovedPart3(db)
                DeleteFlat(db)
                db.Close()
                configuration = DeleteDeepPart1(db)
                db = Db4oFactory.OpenFile(configuration, Db4oFileName)
                DeleteDeepPart2(db)
                DeleteDeepRevisited(db)
            Finally
                db.Close()
            End Try
        End Sub
	' end Main

        Private Shared Sub StoreFirstCar(ByVal db As IObjectContainer)
            Dim car1 As Car = New Car("Ferrari")
            Dim pilot1 As Pilot = New Pilot("Michael Schumacher", 100)
            car1.Pilot = pilot1
            db.Set(car1)
        End Sub
	' end StoreFirstCar

        Private Shared Sub StoreSecondCar(ByVal db As IObjectContainer)
            Dim pilot2 As Pilot = New Pilot("Rubens Barrichello", 99)
            db.Set(pilot2)
            Dim car2 As Car = New Car("BMW")
            car2.Pilot = pilot2
            db.Set(car2)
        End Sub
	' end StoreSecondCar

        Private Shared Sub RetrieveAllCarsQBE(ByVal db As IObjectContainer)
            Dim proto As Car = New Car(Nothing)
            Dim result As IObjectSet = db.Get(proto)
            ListResult(result)
        End Sub
	' end RetrieveAllCarsQBE

        Private Shared Sub RetrieveAllPilotsQBE(ByVal db As IObjectContainer)
            Dim proto As Pilot = New Pilot(Nothing, 0)
            Dim result As IObjectSet = db.Get(proto)
            ListResult(result)
        End Sub
	' end RetrieveAllPilotsQBE

        Private Shared Sub RetrieveCarByPilotQBE(ByVal db As IObjectContainer)
            Dim pilotproto As Pilot = New Pilot("Rubens Barrichello", 0)
            Dim carproto As Car = New Car(Nothing)
            carproto.Pilot = pilotproto
            Dim result As IObjectSet = db.Get(carproto)
            ListResult(result)
        End Sub
	' end RetrieveCarByPilotQBE

        Private Shared Sub RetrieveCarByPilotNameQuery(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Car))
            query.Descend("_pilot").Descend("_name").Constrain("Rubens Barrichello")
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
	' end RetrieveCarByPilotNameQuery

        Private Shared Sub RetrieveCarByPilotProtoQuery(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Car))
            Dim proto As Pilot = New Pilot("Rubens Barrichello", 0)
            query.Descend("_pilot").Constrain(proto)
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
	' end RetrieveCarByPilotProtoQuery

        Private Shared Sub RetrievePilotByCarModelQuery(ByVal db As IObjectContainer)
            Dim carQuery As IQuery = db.Query()
            carQuery.Constrain(GetType(Car))
            carQuery.Descend("_model").Constrain("Ferrari")
            Dim pilotQuery As IQuery = carQuery.Descend("_pilot")
            Dim result As IObjectSet = pilotQuery.Execute()
            ListResult(result)
        End Sub
	' end RetrievePilotByCarModelQuery

        Private Shared Sub RetrieveAllPilots(ByVal db As IObjectContainer)
            Dim results As IObjectSet = db.Get(GetType(Pilot))
            ListResult(results)
        End Sub
	' end RetrieveAllPilots

        Private Shared Sub RetrieveAllCars(ByVal db As IObjectContainer)
            Dim results As IObjectSet = db.Get(GetType(Car))
            ListResult(results)
        End Sub
	' end RetrieveAllCars

        Public Class RetrieveCarsByPilotNamePredicate
            Inherits Predicate
            ReadOnly _pilotName As String

            Public Sub New(ByVal pilotName As String)
                _pilotName = pilotName
            End Sub

            Public Function Match(ByVal candidate As Car) As Boolean
                Return candidate.Pilot.Name = _pilotName
            End Function
        End Class
        ' end RetrieveCarsByPilotNamePredicate

        Private Shared Sub RetrieveCarsByPilotNameNative(ByVal db As IObjectContainer)
            Dim pilotName As String = "Rubens Barrichello"
            Dim results As IObjectSet = db.Query(New RetrieveCarsByPilotNamePredicate(pilotName))
            ListResult(results)
        End Sub
	' end RetrieveCarsByPilotNameNative

        Private Shared Sub UpdateCar(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("Ferrari"))
            Dim found As Car = DirectCast(result.Next(), Car)
            found.Pilot = New Pilot("Somebody else", 0)
            db.Set(found)
            result = db.Get(New Car("Ferrari"))
            ListResult(result)
        End Sub
	' end UpdateCar

        Private Shared Sub UpdatePilotSingleSession(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("Ferrari"))
            Dim found As Car = DirectCast(result.Next(), Car)
            found.Pilot.AddPoints(1)
            db.Set(found)
            result = db.Get(New Car("Ferrari"))
            ListResult(result)
        End Sub
	' end UpdatePilotSingleSession

        Private Shared Sub UpdatePilotSeparateSessionsPart1(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("Ferrari"))
            Dim found As Car = DirectCast(result.Next(), Car)
            found.Pilot.AddPoints(1)
            db.Set(found)
        End Sub
	' end UpdatePilotSeparateSessionsPart1

        Private Shared Sub UpdatePilotSeparateSessionsPart2(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("Ferrari"))
            ListResult(result)
        End Sub
	' end UpdatePilotSeparateSessionsPart2

        Private Shared Function UpdatePilotSeparateSessionsImprovedPart1(ByVal db As IObjectContainer) As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Car)).CascadeOnUpdate(True)
            Return configuration
        End Function
	' end UpdatePilotSeparateSessionsImprovedPart1

        Private Shared Sub UpdatePilotSeparateSessionsImprovedPart2(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("Ferrari"))
            Dim found As Car = DirectCast(result.Next(), Car)
            found.Pilot.AddPoints(1)
            db.Set(found)
        End Sub
	' end UpdatePilotSeparateSessionsImprovedPart2

        Private Shared Sub UpdatePilotSeparateSessionsImprovedPart3(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("Ferrari"))
            ListResult(result)
        End Sub
	' end UpdatePilotSeparateSessionsImprovedPart3

        Private Shared Sub DeleteFlat(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("Ferrari"))
            Dim found As Car = DirectCast(result.Next(), Car)
            db.Delete(found)
            result = db.Get(New Car(Nothing))
            ListResult(result)
        End Sub
	' end DeleteFlat

        Private Shared Function DeleteDeepPart1(ByVal db As IObjectContainer) As IConfiguration
            Dim configuration As IConfiguration = Db4oFactory.NewConfiguration()
            configuration.ObjectClass(GetType(Car)).CascadeOnDelete(True)
            Return configuration
        End Function
	' end DeleteDeepPart1

        Private Shared Sub DeleteDeepPart2(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Car("BMW"))
            Dim found As Car = DirectCast(result.Next(), Car)
            db.Delete(found)
            result = db.Get(New Car(Nothing))
            ListResult(result)
        End Sub
	' end DeleteDeepPart2

        Private Shared Sub DeleteDeepRevisited(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(New Pilot("Michael Schumacher", 0))
            Dim pilot As Pilot = DirectCast(result.Next(), Pilot)
            Dim car1 As Car = New Car("Ferrari")
            Dim car2 As Car = New Car("BMW")
            car1.Pilot = pilot
            car2.Pilot = pilot
            db.Set(car1)
            db.Set(car2)
            db.Delete(car2)
            result = db.Get(New Car(Nothing))
            ListResult(result)
        End Sub
	' end DeleteDeepRevisited

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
	' end ListResult
    End Class
End Namespace
