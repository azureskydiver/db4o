Imports System.IO
Imports com.db4o.query
Imports com.db4o

Namespace com.db4o.f1.chapter6
	Public Class EvaluationExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
			File.Delete(Util.YapFileName)
            Dim db As Global.com.db4o.ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
			Try
				StoreCars(db)
				QueryWithEvaluation(db)
			Finally
				db.Close()
			End Try
		End Sub

		Public Shared Sub StoreCars(ByVal db As ObjectContainer)
            Dim pilot1 As chapter3.Pilot = New chapter3.Pilot("Michael Schumacher", 100)
            Dim car1 As chapter3.Car = New chapter3.Car("Ferrari")
			car1.Pilot = pilot1
			car1.Snapshot()
			db.[Set](car1)
            Dim pilot2 As chapter3.Pilot = New chapter3.Pilot("Rubens Barrichello", 99)
            Dim car2 As chapter3.Car = New chapter3.Car("BMW")
			car2.Pilot = pilot2
			car2.Snapshot()
			car2.Snapshot()
			db.[Set](car2)
		End Sub

		Public Shared Sub QueryWithEvaluation(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
            query.Constrain(GetType(chapter3.Car))
			query.Constrain(New EvenHistoryEvaluation())
			Dim result As ObjectSet = query.Execute()
			Util.ListResult(result)
		End Sub

	End Class
End Namespace
