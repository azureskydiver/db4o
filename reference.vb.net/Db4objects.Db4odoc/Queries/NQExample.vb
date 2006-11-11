' Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Query
Imports System.Collections

Namespace Db4objects.Db4odoc.Queries
    Public Class NQExample
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            Dim db As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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

        Public Shared Sub PrimitiveQuery(ByVal db As IObjectContainer)
            Dim pilots As IList = db.Query(New PilotHundredPoints())
        End Sub
        ' end PrimitiveQuery

        Public Shared Sub StorePilots(ByVal db As IObjectContainer)
            db.[Set](New Pilot("Michael Schumacher", 100))
            db.[Set](New Pilot("Rubens Barrichello", 99))
        End Sub
        ' end StorePilots

        Public Shared Sub RetrieveComplexSODA(ByVal db As IObjectContainer)
            Dim query As IQuery = db.Query()
            query.Constrain(GetType(Pilot))
            Dim pointQuery As IQuery = query.Descend("_points")
            query.Descend("_name").Constrain("Rubens Barrichello").[Or](pointQuery.Constrain(99).Greater().[And](pointQuery.Constrain(199).Smaller()))
            Dim result As IObjectSet = query.Execute()
            ListResult(result)
        End Sub
        ' end RetrieveComplexSODA

        Public Shared Sub RetrieveComplexNQ(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Query(New ComplexQuery())
            ListResult(result)
        End Sub
        ' end RetrieveComplexNQ

        Public Shared Sub RetrieveArbitraryCodeNQ(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.Query(New ArbitraryQuery(New Integer() {1, 100}))
            ListResult(result)
        End Sub
        ' end RetrieveArbitraryCodeNQ

        Public Shared Sub ClearDatabase(ByVal db As IObjectContainer)
            Dim result As IObjectSet = db.[Get](GetType(Pilot))
            While result.HasNext()
                db.Delete(result.[Next]())
            End While
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
