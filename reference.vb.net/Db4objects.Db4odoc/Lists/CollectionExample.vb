' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 

Imports System.IO

Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query

Namespace Db4objects.Db4odoc.Lists

    Public Class CollectionExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args() As String)
            SetTeam()
            SetTeam()
        End Sub
        ' end Main

        Public Shared Sub SetTeam()
            File.Delete(YapFileName)
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim ferrariTeam As Team = New Team()
                ferrariTeam.Name = "Ferrari"

                Dim pilot1 As Pilot = New Pilot("Michael Schumacher", 100)
                ferrariTeam.AddPilot(pilot1)
                Dim pilot2 As Pilot = New Pilot("David Schumacher", 98)
                ferrariTeam.AddPilot(pilot2)

                db.Set(ferrariTeam)
                Dim protoList As IList = CollectionFactory.NewList()
                Dim result As IObjectSet = db.Get(protoList)
                ListResult(result)
            Finally
                db.Close()
            End Try
        End Sub
        ' end SetTeam


        Public Shared Sub UpdateTeam()
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                Dim query As IQuery = db.Query()
                query.Constrain(GetType(Team))
                query.Descend("_name").Constrain("Ferrari")
                Dim result As IObjectSet = query.Execute()
                If result.HasNext() Then
                    Dim ferrariTeam As Team = CType(result.Next(), Team)

                    Dim pilot As Evaluations.Pilot = New Evaluations.Pilot("David Schumacher", 100)
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
        ' end UpdateTeam

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace

