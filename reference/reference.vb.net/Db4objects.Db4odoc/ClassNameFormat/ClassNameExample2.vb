' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.ClassNameFormat

    Class ClassNameExample2
        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            CheckDatabase()
        End Sub
        ' end Main

        Private Shared Sub CheckDatabase()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                ' Read db4o contents from another application
                Dim result As IObjectSet = container.Get(GetType(Test))
                ListResult(result)
                ' Check what classes are actualy stored in the database
                Dim storedClasses As IStoredClass() = container.Ext.StoredClasses
                Dim storedClass As IStoredClass
                For Each storedClass In storedClasses
                    System.Console.WriteLine("Stored class: " + storedClass.GetName)
                Next
            Finally
                container.Commit()
            End Try
        End Sub
        ' end CheckDatabase

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine("Objects found: " + result.Size.ToString())
            While result.HasNext
                System.Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

    End Class
End Namespace