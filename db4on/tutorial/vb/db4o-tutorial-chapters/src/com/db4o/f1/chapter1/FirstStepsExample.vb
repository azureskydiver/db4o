Imports System
Imports System.IO
Imports com.db4o

Namespace com.db4o.f1.chapter1
	Public Class FirstStepsExample
	Inherits Util
		Public Shared Sub Main(ByVal args As String())
			File.Delete(Util.YapFileName)
			AccessDb4o()
			File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
			Try
				StoreFirstPilot(db)
				StoreSecondPilot(db)
				RetrieveAllPilots(db)
				RetrievePilotByName(db)
				RetrievePilotByExactPoints(db)
				UpdatePilot(db)
				DeleteFirstPilotByName(db)
				DeleteSecondPilotByName(db)
			Finally
				db.Close()
			End Try
		End Sub

		Public Shared Sub AccessDb4o()
            Dim db As ObjectContainer = Db4oFactory.OpenFile(Util.YapFileName)
			Try
				' do something with db4o
			Finally
				db.Close()
			End Try
		End Sub

		Public Shared Sub StoreFirstPilot(ByVal db As ObjectContainer)
			Dim pilot1 As Pilot = New Pilot("Michael Schumacher", 100)
            db.Set(pilot1)
			Console.WriteLine("Stored {0}", pilot1)
		End Sub

		Public Shared Sub StoreSecondPilot(ByVal db As ObjectContainer)
			Dim pilot2 As Pilot = New Pilot("Rubens Barrichello", 99)
			db.[Set](pilot2)
			Console.WriteLine("Stored {0}", pilot2)
		End Sub

		Public Shared Sub RetrieveAllPilotQBE(ByVal db As ObjectContainer)
			Dim proto As Pilot = New Pilot(Nothing, 0)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrieveAllPilots(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](GetType(Pilot))
			ListResult(result)
		End Sub

		Public Shared Sub RetrievePilotByName(ByVal db As ObjectContainer)
			Dim proto As Pilot = New Pilot("Michael Schumacher", 0)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub RetrievePilotByExactPoints(ByVal db As ObjectContainer)
			Dim proto As Pilot = New Pilot(Nothing, 100)
			Dim result As ObjectSet = db.[Get](proto)
			ListResult(result)
		End Sub

		Public Shared Sub UpdatePilot(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Pilot("Michael Schumacher", 0))
			Dim found As Pilot = DirectCast(result.[Next](), Pilot)
			found.AddPoints(11)
			db.[Set](found)
			Console.WriteLine("Added 11 points for {0}", found)
			RetrieveAllPilots(db)
		End Sub

		Public Shared Sub DeleteFirstPilotByName(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Pilot("Michael Schumacher", 0))
			Dim found As Pilot = DirectCast(result.[Next](), Pilot)
			db.Delete(found)
			Console.WriteLine("Deleted {0}", found)
			RetrieveAllPilots(db)
		End Sub

		Public Shared Sub DeleteSecondPilotByName(ByVal db As ObjectContainer)
			Dim result As ObjectSet = db.[Get](New Pilot("Rubens Barrichello", 0))
			Dim found As Pilot = DirectCast(result.[Next](), Pilot)
			db.Delete(found)
			Console.WriteLine("Deleted {0}", found)
			RetrieveAllPilots(db)
		End Sub

	End Class
End Namespace
