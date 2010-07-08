Imports Db4objects.Db4o
Imports Db4objects.Db4o.Config
Imports Db4objects.Db4o.IO

Namespace silverlight.Code.configuration.io
	Public Class IOExamples
		Public Sub useIsolatedStorage()
			' #example: use the isolated storage on silverlight
			Dim configuration As IEmbeddedConfiguration = Db4oEmbedded.NewConfiguration()
			configuration.File.Storage = New IsolatedStorageStorage()
			' #end example

			Dim container As IObjectContainer = Db4oEmbedded.OpenFile(configuration, "database.db4o")
		End Sub
	End Class
End Namespace