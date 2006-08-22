' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System.IO

Imports com.db4o
Imports com.db4o.query
Imports com.db4odoc.f1.evaluations

Namespace com.db4odoc.f1.lists

    Public Class CollectionExample
        Inherits Util

        Public Shared Sub Main(ByVal args() As String)
            SetTeam()
            SetTeam()
        End Sub

        Public Shared Sub SetTeam()
            File.Delete(Util.YapFileName)
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim ferrariTeam As Team = New Team()
                ferrariTeam.Name = "Ferrari"

                Dim pilot1 As evaluations.Pilot = New evaluations.Pilot("Michael Schumacher", 100)
                ferrariTeam.AddPilot(pilot1)
                Dim pilot2 As evaluations.Pilot = New evaluations.Pilot("David Schumacher", 98)
                ferrariTeam.AddPilot(pilot2)

                db.Set(ferrariTeam)
                Dim protoList As IList = CollectionFactory.NewList()
                Dim result As ObjectSet = db.Get(protoList)
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub


        Public Shared Sub UpdateTeam()
            Dim db As ObjectContainer = Db4o.OpenFile(Util.YapFileName)
            Try
                Dim query As Query = db.Query()
                query.Constrain(GetType(Team))
                query.Descend("_name").Constrain("Ferrari")
                Dim result As ObjectSet = query.Execute()
                If result.HasNext() Then
                    Dim ferrariTeam As Team = CType(result.Next(), Team)

                    Dim pilot As evaluations.Pilot = New evaluations.Pilot("David Schumacher", 100)
                    ferrariTeam.UpdatePilot(1, pilot)

                    db.Set(ferrariTeam)
                End If
                Dim protoList As IList = CollectionFactory.NewList()
                result = db.Get(protoList)
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
    End Class
End Namespace

