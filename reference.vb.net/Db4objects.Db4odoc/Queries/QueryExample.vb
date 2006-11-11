' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports System
Imports System.IO
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Queries
    Public Class QueryExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            StorePilot()
            UpdatePilotWrong()
            UpdatePilot()
            DeletePilot()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
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
        ' end Main

        Public Shared Sub StorePilot()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim pilot As Pilot = New Pilot("Michael Schumacher", 0)
                db.Set(pilot)
                Console.WriteLine("Stored {0}", pilot)
                ' change pilot and resave updated
                pilot.AddPoints(10)
                db.Set(pilot)
                Console.WriteLine("Stored {0}", pilot)
            Finally
                db.Close()
            End Try
            RetrieveAllPilots()
        End Sub
        ' end StorePilot

        Public Shared Sub UpdatePilotWrong()
            StorePilot()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                'Even completely identical Pilot object
                ' won't work for update of the saved pilot
                Dim pilot As Pilot = New Pilot("Michael Schumacher", 10)
                pilot.AddPoints(10)
                db.Set(pilot)
                Console.WriteLine("Stored {0}", pilot)
            Finally
                db.Close()
            End Try
            RetrieveAllPilots()
        End Sub
        ' end UpdatePilotWrong

        Public Shared Sub UpdatePilot()
            StorePilot()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                'first retrieve the object from the database
                Dim result As IObjectSet = db.Get(New Pilot("Michael Schumacher", 10))
                Dim pilot As Pilot = CType(result(0), Pilot)
                pilot.AddPoints(10)
                db.Set(pilot)
                Console.WriteLine("Added 10 points to {0}", pilot)
            Finally
                db.Close()
            End Try
            RetrieveAllPilots()
        End Sub
        ' end UpdatePilot

        Public Shared Sub DeletePilot()
            StorePilot()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                'first retrieve the object from the database
                Dim result As IObjectSet = db.Get(New Pilot("Michael Schumacher", 10))
                Dim pilot As Pilot = CType(result(0), Pilot)
                db.Delete(pilot)
                Console.WriteLine("Deleted {0}", pilot)
            Finally
                db.Close()
            End Try
            RetrieveAllPilots()
        End Sub
        ' end DeletePilot

        Public Shared Sub RetrieveAllPilots()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Pilot))
                Dim result As IObjectSet = query.Execute()
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end RetrieveAllPilots

        Public Shared Sub RetrievePilotByName(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            query.Descend("_name").Constrain("Michael Schumacher")
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrievePilotByName

        Public Shared Sub RetrievePilotByExactPoints(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            query.Descend("_points").Constrain(100)
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrievePilotByExactPoints

        Public Shared Sub RetrieveByNegation(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            query.Descend("_name").Constrain("Michael Schumacher").[Not]()
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrieveByNegation

        Public Shared Sub RetrieveByConjunction(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            Dim constr As Constraint = query.Descend("_name").Constrain("Michael Schumacher")
            query.Descend("_points").Constrain(99).[And](constr)
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrieveByConjunction

        Public Shared Sub RetrieveByDisjunction(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            Dim constr As Constraint = query.Descend("_name").Constrain("Michael Schumacher")
            query.Descend("_points").Constrain(99).[Or](constr)
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrieveByDisjunction

        Public Shared Sub RetrieveByComparison(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            query.Descend("_points").Constrain(99).Greater()
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrieveByComparison

        Public Shared Sub RetrieveByDefaultFieldValue(ByVal db As IObjectContainer)
            Dim somebody As Pilot = New Pilot("Somebody else", 0)
            db.[Set](somebody)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            query.Descend("_points").Constrain(0)
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
            db.Delete(somebody)
        End Sub
        ' end RetrieveByDefaultFieldValue

        Public Shared Sub RetrieveSorted(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            query.Descend("_name").OrderAscending()
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
            query.Descend("_name").OrderDescending()
            result = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrieveSorted

        Public Shared Sub ClearDatabase(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](GetType(Pilot))
            For Each item As Object In result
                db.Delete(item)
            Next
        End Sub
        ' end ClearDatabase

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
