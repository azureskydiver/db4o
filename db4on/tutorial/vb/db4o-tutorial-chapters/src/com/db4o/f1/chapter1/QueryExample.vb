Imports System
Imports com.db4o
Imports com.db4o.query

Namespace com.db4o.f1.chapter1
	Public Class QueryExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
			Try
				StoreFirstPilot(db)
				StoreSecondPilot(db)
				RetrieveAllPilots(db)
				RetrievePilotByName(db)
				RetrievePilotByExactPoints(db)
				RetrieveByNegation(db)
				RetrieveByConjunction(db)
				RetrieveByDisjunction(db)
				RetrieveByComparison(db)
				RetrieveByDefaultFieldValue(db)
				RetrieveSorted(db)
				ClearDatabase(db)
			Finally
				db.Close()
			End Try
		End Sub

		Public Shared Sub StoreFirstPilot(ByVal db As ObjectContainer)
			Dim pilot1 As Pilot = New Pilot("Michael Schumacher", 100)
			db.[Set](pilot1)
			Console.WriteLine("Stored {0}", pilot1)
		End Sub

		Public Shared Sub StoreSecondPilot(ByVal db As ObjectContainer)
			Dim pilot2 As Pilot = New Pilot("Rubens Barrichello", 99)
			db.[Set](pilot2)
			Console.WriteLine("Stored {0}", pilot2)
		End Sub

		Public Shared Sub RetrieveAllPilots(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrievePilotByName(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			query.Descend("_name").Constrain("Michael Schumacher")
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrievePilotByExactPoints(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			query.Descend("_points").Constrain(100)
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveByNegation(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			query.Descend("_name").Constrain("Michael Schumacher").[Not]()
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveByConjunction(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			Dim constr As Constraint = query.Descend("_name").Constrain("Michael Schumacher")
			query.Descend("_points").Constrain(99).[And](constr)
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveByDisjunction(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			Dim constr As Constraint = query.Descend("_name").Constrain("Michael Schumacher")
			query.Descend("_points").Constrain(99).[Or](constr)
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveByComparison(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			query.Descend("_points").Constrain(99).Greater()
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveByDefaultFieldValue(ByVal db As ObjectContainer)
			Dim somebody As Pilot = New Pilot("Somebody else", 0)
			db.[Set](somebody)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			query.Descend("_points").Constrain(0)
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
			db.Delete(somebody)
		End Sub

		Public Shared Sub RetrieveSorted(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			query.Descend("_name").OrderAscending()
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
			query.Descend("_name").OrderDescending()
			result = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub ClearDatabase(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](GetType(Pilot))
			For Each item As Object In result
				db.Delete(item)
			Next
		End Sub

	End Class
End Namespace
