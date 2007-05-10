' Copyright (C) 2007 db4objects Inc. http:'www.db4o.com 

Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.VersionUpdate
    Public Class UpdateExample

        Public Shared Sub Main(ByVal args As String())
            Db4oFactory.Configure().AllowVersionUpdates(True)
            Dim objectContainer As IObjectContainer = Db4oFactory.OpenFile(args(0))
            objectContainer.Close()
            System.Console.WriteLine("The database is ready for the version " + Db4o.Db4oVersion.NAME)
        End Sub
    End Class
End Namespace

