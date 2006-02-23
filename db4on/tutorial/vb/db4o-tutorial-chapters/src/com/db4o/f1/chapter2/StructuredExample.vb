Imports System
Imports System.IO
Imports com.db4o
Imports com.db4o.query

Namespace com.db4o.f1.chapter2
	Public Class StructuredExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
			File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
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
                db = Db4oFactory.OpenFile(Util.YapFileName)
				UpdatePilotSeparateSessionsPart2(db)
				db.Close()
				UpdatePilotSeparateSessionsImprovedPart1(db)
                db = Db4oFactory.OpenFile(Util.YapFileName)
				UpdatePilotSeparateSessionsImprovedPart2(db)
				db.Close()
                db = Db4oFactory.OpenFile(Util.YapFileName)
				UpdatePilotSeparateSessionsImprovedPart3(db)
				DeleteFlat(db)
				db.Close()
				DeleteDeepPart1(db)
                db = Db4oFactory.OpenFile(Util.YapFileName)
				DeleteDeepPart2(db)
				DeleteDeepRevisited(db)
			Finally
				db.Close()
			End Try
		End Sub

		Public Shared Sub StoreFirstCar(ByVal db As ObjectContainer)
			Dim car1 As Car = New Car("Ferrari")
			Dim pilot1 As Pilot = New Pilot("Michael Schumacher", 100)
			car1.Pilot = pilot1
			db.[Set](car1)
		End Sub

		Public Shared Sub StoreSecondCar(ByVal db As ObjectContainer)
			Dim pilot2 As Pilot = New Pilot("Rubens Barrichello", 99)
			db.[Set](pilot2)
			Dim car2 As Car = New Car("BMW")
			car2.Pilot = pilot2
			db.[Set](car2)
		End Sub

		Public Shared Sub RetrieveAllCarsQBE(ByVal db As ObjectContainer)
			Dim proto As Car = New Car(Nothing)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveAllPilotsQBE(ByVal db As ObjectContainer)
			Dim proto As Pilot = New Pilot(Nothing, 0)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveCarByPilotQBE(ByVal db As ObjectContainer)
			Dim pilotproto As Pilot = New Pilot("Rubens Barrichello", 0)
			Dim carproto As Car = New Car(Nothing)
			carproto.Pilot = pilotproto
			Dim result As ObjectSet = db.[Get](carproto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveCarByPilotNameQuery(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Car))
			query.Descend("_pilot").Descend("_name").Constrain("Rubens Barrichello")
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveCarByPilotProtoQuery(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Car))
			Dim proto As Pilot = New Pilot("Rubens Barrichello", 0)
			query.Descend("_pilot").Constrain(proto)
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrievePilotByCarModelQuery(ByVal db As ObjectContainer)
			Dim carQuery As Query = db.Query()
			carQuery.Constrain(GetType(Car))
			carQuery.Descend("_model").Constrain("Ferrari")
			Dim pilotQuery As Query = carQuery.Descend("_pilot")
			Dim result As ObjectSet = pilotQuery.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveAllPilots(ByVal db As ObjectContainer)
			Dim results As ObjectSet = db.[Get](GetType(Pilot))
			ListResult(results)
		End Sub

		Public Shared Sub RetrieveAllCars(ByVal db As ObjectContainer)
			Dim results As ObjectSet = db.[Get](GetType(Car))
			ListResult(results)
		End Sub

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
		Public Shared Sub RetrieveCarsByPilotNameNative(ByVal db As ObjectContainer)
			Dim pilotName As String = "Rubens Barrichello"
			Dim results As ObjectSet = db.Query(New RetrieveCarsByPilotNamePredicate(pilotName))
			ListResult(results)
		End Sub

		Public Shared Sub UpdateCar(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("Ferrari"))
			Dim found As Car = DirectCast(result.[Next](), Car)
			found.Pilot = New Pilot("Somebody else", 0)
			db.[Set](found)
			result = db.[Get](New Car("Ferrari"))
			ListResult(result)
		End Sub

		Public Shared Sub UpdatePilotSingleSession(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("Ferrari"))
			Dim found As Car = DirectCast(result.[Next](), Car)
			found.Pilot.AddPoints(1)
			db.[Set](found)
			result = db.[Get](New Car("Ferrari"))
			ListResult(result)
		End Sub

		Public Shared Sub UpdatePilotSeparateSessionsPart1(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("Ferrari"))
			Dim found As Car = DirectCast(result.[Next](), Car)
			found.Pilot.AddPoints(1)
			db.[Set](found)
		End Sub

		Public Shared Sub UpdatePilotSeparateSessionsPart2(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("Ferrari"))
			ListResult(result)
		End Sub

		Public Shared Sub UpdatePilotSeparateSessionsImprovedPart1(ByVal db As ObjectContainer)
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CascadeOnUpdate(True)
		End Sub

		Public Shared Sub UpdatePilotSeparateSessionsImprovedPart2(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("Ferrari"))
			Dim found As Car = DirectCast(result.[Next](), Car)
			found.Pilot.AddPoints(1)
			db.[Set](found)
		End Sub

		Public Shared Sub UpdatePilotSeparateSessionsImprovedPart3(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("Ferrari"))
			ListResult(result)
		End Sub

		Public Shared Sub DeleteFlat(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("Ferrari"))
			Dim found As Car = DirectCast(result.[Next](), Car)
			db.Delete(found)
			result = db.[Get](New Car(Nothing))
			ListResult(result)
		End Sub

		Public Shared Sub DeleteDeepPart1(ByVal db As ObjectContainer)
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CascadeOnDelete(True)
		End Sub

		Public Shared Sub DeleteDeepPart2(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("BMW"))
			Dim found As Car = DirectCast(result.[Next](), Car)
			db.Delete(found)
			result = db.[Get](New Car(Nothing))
			ListResult(result)
		End Sub

		Public Shared Sub DeleteDeepRevisited(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Pilot("Michael Schumacher", 0))
			Dim pilot As Pilot = DirectCast(result.[Next](), Pilot)
			Dim car1 As Car = New Car("Ferrari")
			Dim car2 As Car = New Car("BMW")
			car1.Pilot = pilot
			car2.Pilot = pilot
			db.[Set](car1)
			db.[Set](car2)
			db.Delete(car2)
			result = db.[Get](New Car(Nothing))
			ListResult(result)
		End Sub

	End Class
End Namespace
