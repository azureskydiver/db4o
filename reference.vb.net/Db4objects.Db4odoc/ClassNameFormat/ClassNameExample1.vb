' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports System.IO
Imports Db4objects.Db4o

Namespace Db4objects.Db4odoc.ClassNameFormat

    Class ClassNameExample1
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            StoreObjects()
        End Sub
        ' end Main

        Public Shared Sub StoreObjects()
            File.Delete(YapFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
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