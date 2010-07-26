Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.IO

Namespace Db4oDoc.Silverlight.Model
    Public Class QueriesInSilverlight
        Private Sub SodaQuery()
            Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
            configuration.File.Storage = New IsolatedStorageStorage()

            Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
            ' #example: Queries in Silverlight
            Dim query = container.Query()
            query.Constrain(GetType(Person))
            query.Descend("FirstName").Constrain("Roman").Contains()

            Dim queryResult As IObjectSet = query.Execute()
            ' do something with the persons
            For Each person As Person In queryResult
            Next
            ' #end example


        End Sub
    End Class
End Namespace