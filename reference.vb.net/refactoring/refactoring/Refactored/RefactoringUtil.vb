' Copyright (C) 2007  db4objects Inc.  http://www.db4o.com 

Imports Db4objects.Db4o
Imports Db4objects.Db4odoc.Refactoring.Initial

Namespace Db4objects.Db4odoc.Refactoring.Refactored
    Class RefactoringUtil

        Private Const Db4oFileName As String = "reference.db4o"

        Public Shared Sub Main(ByVal args As String())
            MoveValues()
        End Sub

        Public Shared Sub MoveValues()
            Dim container As IObjectContainer = Db4oFactory.OpenFile(Db4oFileName)
            Try
                ' querying for B will bring back B and C values
                Dim result As IObjectSet = container.[Get](New Initial.B())
                For i As Integer = 0 To result.Count - 1
                    Dim b As Initial.B = DirectCast(result(i), Initial.B)
                    Dim d As New D()
                    d.name = b.name
                    d.number = b.number
                    container.Delete(b)
                    container.[Set](d)

                Next
            Finally
                container.Close()
                System.Console.WriteLine("Done")
            End Try
        End Sub
        ' end moveValues


    End Class

End Namespace
