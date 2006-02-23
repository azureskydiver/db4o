Imports com.db4o
Imports com.db4o.query

Namespace com.db4o.f1.chapter1
	Public Class NQExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
			Try
				StorePilots(db)
				RetrieveComplexSODA(db)
				RetrieveComplexNQ(db)
				RetrieveArbitraryCodeNQ(db)
				ClearDatabase(db)
			Finally
				db.Close()
			End Try
		End Sub

		Public Shared Sub StorePilots(ByVal db As ObjectContainer)
			db.[Set](New Pilot("Michael Schumacher", 100))
			db.[Set](New Pilot("Rubens Barrichello", 99))
		End Sub

		Public Shared Sub RetrieveComplexSODA(ByVal db As ObjectContainer)
			Dim query As Query = db.Query()
			query.Constrain(GetType(Pilot))
			Dim pointQuery As Query = query.Descend("_points")
			query.Descend("_name").Constrain("Rubens Barrichello").[Or](pointQuery.Constrain(99).Greater().[And](pointQuery.Constrain(199).Smaller()))
			Dim result As ObjectSet = query.Execute()
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveComplexNQ(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.Query(New ComplexQuery())
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveArbitraryCodeNQ(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.Query(New ArbitraryQuery(New Integer() {1, 100}))
			ListResult(result)
		End Sub

		Public Shared Sub ClearDatabase(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](GetType(Pilot))
			While result.HasNext()
				db.Delete(result.[Next]())
			End While
		End Sub

	End Class
End Namespace
