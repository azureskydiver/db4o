Imports System
Imports System.Collections
Imports System.IO
Imports com.db4o
Imports com.db4o.query
Namespace com.db4o.f1.chapter3
	Public Class CollectionsExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
			File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
			Try
				StoreFirstCar(db)
				StoreSecondCar(db)
				RetrieveAllSensorReadouts(db)
				RetrieveSensorReadoutQBE(db)
				RetrieveCarQBE(db)
				RetrieveCollections(db)
				RetrieveArrays(db)
				RetrieveSensorReadoutQuery(db)
				RetrieveCarQuery(db)
				db.Close()
				UpdateCarPart1()
                db = Db4oFactory.OpenFile(Util.YapFileName)
				UpdateCarPart2(db)
				UpdateCollection(db)
				db.Close()
				DeleteAllPart1()
                db = Db4oFactory.OpenFile(Util.YapFileName)
				DeleteAllPart2(db)
				RetrieveAllSensorReadouts(db)
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
			Dim car2 As Car = New Car("BMW")
			car2.Pilot = pilot2
			car2.Snapshot()
			car2.Snapshot()
			db.[Set](car2)
		End Sub

		Public Shared Sub RetrieveAllSensorReadouts(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](GetType(SensorReadout))
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveSensorReadoutQBE(ByVal db As ObjectContainer)
			Dim proto As SensorReadout = New SensorReadout(New Double() {0.3, 0.1}, DateTime.MinValue, Nothing)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveCarQBE(ByVal db As ObjectContainer)
			Dim protoReadout As SensorReadout = New SensorReadout(New Double() {0.6, 0.2}, DateTime.MinValue, Nothing)
			Dim protoHistory As IList = New ArrayList()
			protoHistory.Add(protoReadout)
			Dim protoCar As Car = New Car(Nothing, protoHistory)
			Dim result As ObjectSet = db.[Get](protoCar)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveCollections(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New ArrayList())
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveArrays(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Double() {0.6, 0.4})
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveSensorReadoutQuery(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(SensorReadout))
			Dim valueQuery As Query = query.Descend("_values")
			valueQuery.Constrain(0.3)
			valueQuery.Constrain(0.1)
			Dim results As ObjectSet = query.Execute()
			ListResult(results)
		End Sub

		Public Shared Sub RetrieveCarQuery(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Car))
			Dim historyQuery As Query = query.Descend("_history")
			historyQuery.Constrain(GetType(SensorReadout))
			Dim valueQuery As Query = historyQuery.Descend("_values")
			valueQuery.Constrain(0.3)
			valueQuery.Constrain(0.1)
			Dim results As ObjectSet = query.Execute()
			ListResult(results)
		End Sub

		Public Class RetrieveSensorReadoutPredicate
		Inherits Predicate
			Public Function Match(ByVal candidate As SensorReadout) As Boolean
				Return Array.IndexOf(candidate.Values, 0.3) > - 1 AndAlso Array.IndexOf(candidate.Values, 0.1) > - 1
			End Function

		End Class
		Public Shared Sub RetrieveSensorReadoutNative(ByVal db As ObjectContainer)
			Dim results As ObjectSet = db.Query(New RetrieveSensorReadoutPredicate())
			ListResult(results)
		End Sub

		Public Class RetrieveCarPredicate
		Inherits Predicate
			Public Function Match(ByVal car As Car) As Boolean
				For Each sensor As SensorReadout In car.History
					If Array.IndexOf(sensor.Values, 0.3) > - 1 AndAlso Array.IndexOf(sensor.Values, 0.1) > - 1 Then
						Return True
					End If
				Next
				Return False
			End Function

		End Class
		Public Shared Sub RetrieveCarNative(ByVal db As ObjectContainer)
			Dim results As ObjectSet = db.Query(New RetrieveCarPredicate())
			ListResult(results)
		End Sub

		Public Shared Sub UpdateCarPart1()
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CascadeOnUpdate(True)
		End Sub

		Public Shared Sub UpdateCarPart2(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car("BMW", Nothing))
			Dim car As Car = DirectCast(result.[Next](), Car)
			car.Snapshot()
			db.[Set](car)
			RetrieveAllSensorReadouts(db)
		End Sub

		Public Shared Sub UpdateCollection(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Car))
			Dim result As ObjectSet = query.Descend("_history").Execute()
			Dim history As IList = DirectCast(result.[Next](), IList)
			history.RemoveAt(0)
			db.[Set](history)
			Dim proto As Car = New Car(Nothing, Nothing)
			result = db.[Get](proto)
			For Each car As Car In result
				For Each readout As Object In car.History
					Console.WriteLine(readout)
				Next
			Next
		End Sub

		Public Shared Sub DeleteAllPart1()
            Db4oFactory.Configure().ObjectClass(GetType(Car)).CascadeOnDelete(True)
		End Sub

		Public Shared Sub DeleteAllPart2(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Car(Nothing, Nothing))
			For Each car As Object In result
				db.Delete(car)
			Next
			Dim readouts As ObjectSet = db.[Get](New SensorReadout(Nothing, DateTime.MinValue, Nothing))
			For Each readout As Object In readouts
				db.Delete(readout)
			Next
		End Sub

	End Class
End Namespace
