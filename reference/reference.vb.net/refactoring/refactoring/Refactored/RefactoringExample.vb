' Copyright (C) 2007  db4objects Inc.  http://www.db4o.com 

Imports System.IO
Imports Db4objects.Db4o


Namespace Db4objects.Db4odoc.Refactoring.Refactored
    Class RefactoringExample

        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            ReadData()
        End Sub


        Public Shared Sub ReadData()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                Dim result As IObjectSet = container.[Get](New D())
                System.Console.WriteLine()
                System.Console.WriteLine("D class: ")
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

