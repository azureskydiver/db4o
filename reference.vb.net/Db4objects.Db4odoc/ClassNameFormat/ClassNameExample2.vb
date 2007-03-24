' Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com
Imports Db4objects.Db4o
Imports Db4objects.Db4o.Ext

Namespace Db4objects.Db4odoc.ClassNameFormat

    Class ClassNameExample2
        Public Shared ReadOnly YapFileName As String = "formula1.yap"

        Public Shared Sub Main(ByVal args As String())
            CheckDatabase()
        End Sub
        ' end Main

        Public Shared Sub CheckDatabase()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(YapFileName)
            Try
                ' Read db4o contents from another application
                Dim result As IObjectSet = container.Get(GetType(Test))
                ListResult(result)
                ' Check what classes are actualy stored in the database
                Dim storedClasses As IStoredClass() = container.Ext.StoredClasses
                Dim i As Integer = 0
                While i < storedClasses.Length
                    System.Console.WriteLine("Stored class: " + storedClasses(i).GetName)
                    System.Math.Min(System.Threading.Interlocked.Increment(i), i - 1)
                End While
            Finally
                container.Commit()
            End Try
        End Sub
        ' end CheckDatabase

        Public Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine("Objects found: " + result.Size.ToString())
            While result.HasNext
                System.Console.WriteLine(result.Next)
            End While
        End Sub
        ' end ListResult

    End Class
End Namespace