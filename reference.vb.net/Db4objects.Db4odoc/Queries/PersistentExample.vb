' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.Queries
    Public Class PersistentExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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
        ' end Main

        Public Shared Sub StoreFirstPilot(ByVal db As IObjectContainer)
            Dim pilot1 As Pilot = New Pilot("Michael Schumacher", 100)
            db.Set(pilot1)
            Console.WriteLine("Stored {0}", pilot1)
        End Sub
        ' end StoreFirstPilot

        Public Shared Sub StoreSecondPilot(ByVal db As IObjectContainer)
            Dim pilot2 As Pilot = New Pilot("Rubens Barrichello", 99)
            db.[Set](pilot2)
            Console.WriteLine("Stored {0}", pilot2)
        End Sub
        ' end StoreSecondPilot

        Public Shared Sub RetrieveAllPilotQBE(ByVal db As IObjectContainer)
            Dim proto As Pilot = New Pilot(Nothing, 0)
            Dim result As IObjectSet = db.[Get](proto)
            ListResult(result)
        End Sub
        ' end RetrieveAllPilotQBE

        Public Shared Sub RetrieveAllPilots(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](GetType(Pilot))
            ListResult(result)
        End Sub
        ' end RetrieveAllPilots

        Public Shared Sub RetrievePilotByName(ByVal db As IObjectContainer)
            Dim proto As Pilot = New Pilot("Michael Schumacher", 0)
            Dim result As IObjectSet = db.[Get](proto)
            ListResult(result)
        End Sub
        ' end RetrievePilotByName

        Public Shared Sub RetrievePilotByExactPoints(ByVal db As IObjectContainer)
            Dim proto As Pilot = New Pilot(Nothing, 100)
            Dim result As IObjectSet = db.[Get](proto)
            ListResult(result)
        End Sub
        ' end RetrievePilotByExactPoints

        Public Shared Sub UpdatePilot(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](New Pilot("Michael Schumacher", 0))
            Dim found As Pilot = DirectCast(result.[Next](), Pilot)
            found.AddPoints(11)
            db.[Set](found)
            Console.WriteLine("Added 11 points for {0}", found)
            RetrieveAllPilots(db)
        End Sub
        ' end UpdatePilot

        Public Shared Sub DeleteFirstPilotByName(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](New Pilot("Michael Schumacher", 0))
            Dim found As Pilot = DirectCast(result.[Next](), Pilot)
            db.Delete(found)
            Console.WriteLine("Deleted {0}", found)
            RetrieveAllPilots(db)
        End Sub
        ' end DeleteFirstPilotByName

        Public Shared Sub DeleteSecondPilotByName(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](New Pilot("Rubens Barrichello", 0))
            Dim found As Pilot = DirectCast(result.[Next](), Pilot)
            db.Delete(found)
            Console.WriteLine("Deleted {0}", found)
            RetrieveAllPilots(db)
        End Sub
        ' end DeleteSecondPilotByName

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
