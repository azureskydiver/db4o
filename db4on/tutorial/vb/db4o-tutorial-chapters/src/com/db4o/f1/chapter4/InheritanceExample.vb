Imports System
Imports System.IO
Imports com.db4o

Imports com.db4o.query
Namespace com.db4o.f1.chapter4
	Public Class InheritanceExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
			File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
			Try
				StoreFirstCar(db)
				StoreSecondCar(db)
				RetrieveTemperatureReadoutsQBE(db)
				RetrieveAllSensorReadoutsQBE(db)
				RetrieveAllSensorReadoutsQBEAlternative(db)
				RetrieveAllSensorReadoutsQuery(db)
				RetrieveAllObjects(db)
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

		Public Shared Sub RetrieveAllSensorReadoutsQBE(ByVal db As ObjectContainer)
			Dim proto As SensorReadout = New SensorReadout(DateTime.MinValue, Nothing, Nothing)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveTemperatureReadoutsQBE(ByVal db As ObjectContainer)
			Dim proto As SensorReadout = New TemperatureSensorReadout(DateTime.MinValue, Nothing, Nothing, 0)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveAllSensorReadoutsQBEAlternative(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](GetType(SensorReadout))
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveAllSensorReadoutsQuery(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(SensorReadout))
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveAllObjects(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Object())
			ListResult(result)
		End Sub

	End Class
End Namespace
