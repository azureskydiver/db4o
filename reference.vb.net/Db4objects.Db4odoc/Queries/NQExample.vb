' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com 
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports System.Collections

Namespace Db4objects.Db4odoc.Queries
    Public Class NQExample
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            Dim db As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
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
        ' end Main

        Private Shared Sub PrimitiveQuery(ByVal db As IObjectContainer)
            Dim pilots As IList = db.Query(New PilotHundredPoints())
        End Sub
        ' end PrimitiveQuery

        Private Shared Sub StorePilots(ByVal db As IObjectContainer)
            db.Set(New Pilot("Michael Schumacher", 100))
            db.Set(New Pilot("Rubens Barrichello", 99))
        End Sub
        ' end StorePilots

        Private Shared Sub RetrieveComplexSODA(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            Dim pointQuery As IQuery = query.Descend("_points")
            query.Descend("_name").Constrain("Rubens Barrichello").Or(pointQuery.Constrain(99).Greater().Or(pointQuery.Constrain(199).Smaller()))
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrieveComplexSODA

        Private Shared Sub RetrieveComplexNQ(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Query(New ComplexQuery())
            ListResult(result)
        End Sub
        ' end RetrieveComplexNQ

        Private Shared Sub RetrieveArbitraryCodeNQ(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Query(New ArbitraryQuery(New Integer() {1, 100}))
            ListResult(result)
        End Sub
        ' end RetrieveArbitraryCodeNQ

        Private Shared Sub ClearDatabase(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Get(GetType(Pilot))
            While result.HasNext()
                db.Delete(result.Next())
            End While
        End Sub
        ' end ClearDatabase

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            Console.WriteLine(result.Count)
            For Each item As Object In result
                Console.WriteLine(item)
            Next
        End Sub
        ' end ListResult
    End Class
End Namespace
