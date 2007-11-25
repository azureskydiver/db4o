' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System.IO
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.ClassNameFormat

    Class ClassNameExample1
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            StoreObjects()
        End Sub
        ' end Main

        Private Shared Sub StoreObjects()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                ' Store a simple class to the database
                Dim test As Test = New Test
                container.Set(test)
            Finally
                container.Commit()
            End Try
        End Sub
        ' end StoreObjects

    End Class
End Namespace