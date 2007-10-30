' Copyright (C) 2007  db4objects Inc.  http://www.db4o.com 

Imports System.IO
Imports Db4objects.Db4o


Namespace Db4objects.Db4odoc.Refactoring.Initial
    Class RefactoringExample

        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            StoreData()
            ReadData()
        End Sub

        Public Shared Sub StoreData()
            File.Delete(Db4oFileName)
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim a As New A()
                a.name = "A class"
                container.[Set](a)

                Dim b As New B()
                b.name = "B class"
                b.number = 1
                container.[Set](b)

                Dim c As New C()
                c.name = "C class"
                c.number = 2
                container.[Set](c)
            Finally
                container.Close()
            End Try
        End Sub
        ' end StoreData

        Public Shared Sub ReadData()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = container.[Get](New A())
                System.Console.WriteLine("A class: ")
                ListResult(result)

                result = container.[Get](New B())
                System.Console.WriteLine()
                System.Console.WriteLine("B class: ")
                ListResult(result)

                result = container.[Get](New C())
                System.Console.WriteLine()
                System.Console.WriteLine("C class: ")
                ListResult(result)
            Finally
                container.Close()
            End Try
        End Sub
        ' end ReadData

        Private Shared Sub ListResult(ByVal result As IObjectSet)
            System.Console.WriteLine(result.Count)
            For i As Integer = 0 To result.Count - 1
                System.Console.WriteLine(result(i))
            Next
        End Sub
        ' end ListResult

    End Class

End Namespace

